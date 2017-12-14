package auth.background.service;
 

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import auth.background.bean.User;
import auth.background.bean.UserRoleKey;
import auth.background.config.MyConstants;
import auth.background.dao.UserMapper;
import auth.background.dao.UserRoleMapper;
import auth.background.dto.MessageBase;
import auth.background.dto.ResetPasswordModel;
import auth.background.dto.UserDto;
import auth.background.dto.UserRoleDto;
import auth.background.util.BeanMapper;
import auth.background.util.PageHelper;
 

@Service
public class UserAppService {

	private static final String Key="User";
    @Resource
    private UserMapper userDao;
    
    @Resource
    private UserRoleMapper userRoleDao;
	
	@Resource
    private BeanMapper dzmapper;
    
	@Resource
    private CacheService<UserDto> cacheService;
    
	@Resource
    private CacheService<UserRoleDto> cacheService2;
    
	@Resource
	private QueueSerivce<UserDto> queueSerivce;
	
    public UserDto CheckUser(String userName, String password){
    	return dzmapper.map(userDao.CheckUser(userName,password), UserDto.class);
    }
    
    public UserDto Get(String id)
    {
    	User obj = userDao.selectByPrimaryKey(id);
    	UserDto dto = dzmapper.map(obj, UserDto.class);
    	return dto;
    }
    
    public List<UserDto> GetAllList(){
    	String okey=MyConstants._instance+Key;
    	Runnable<UserDto> handler = () -> 
    	{ 
    		return dzmapper.mapList(userDao.GetAllList(),  UserDto.class); 
    	};
    	return cacheService.GetSortList(handler, okey,0, 0,-1);
    }
    
    public int GetChildrenByDepartmentCount(String departmentid){
    	String okey=MyConstants._instance+Key+departmentid;
    	RunnableCount handler = () -> 
    	{ 
    		return userDao.GetChildrenByDepartmentCount(departmentid); 
    	};
    	return cacheService.zcard(handler, okey);
    }
    
  //https://www.oschina.net/news/62668/mybatis-pagehelper-3-7-3
    public List<UserDto> GetChildrenByDepartment(String departmentid, int startPage, int pageSize,int count){
    	String okey=MyConstants._instance+Key+departmentid;
    	Runnable<UserDto> handler = () -> 
    	{ 
    		List<User> llist = userDao.GetChildrenByDepartment(departmentid);
    		return dzmapper.mapList(llist,  UserDto.class); 
    	};
    	List<UserDto> dlist =cacheService.GetSortList(handler, okey, startPage,pageSize,count);
    	PageHelper<UserDto> ph= new PageHelper<UserDto>();
    	return ph.paged(dlist, startPage, pageSize, count);
    }
    
    public List<UserRoleDto> GetUserRoles(String userId){
    	String okey=MyConstants._instance+Key+userId;
    	Runnable<UserRoleDto> handler = () -> { return dzmapper.mapList(userRoleDao.GetUserRoles(userId), UserRoleDto.class); };
    	return cacheService2.GetSortList(handler, okey, 0, 0,-1);
    }
    
    @Transactional 
    public void BatchUpdateUserRoles(List<String> userIds, List<String> roleIds){
    	List<UserRoleKey> list = new ArrayList<UserRoleKey>();
    	UserRoleKey e = null;
    	for(String uid : userIds){
    		for(String rid : roleIds){
    			e=new UserRoleKey();
    			e.setUserid(uid);
    			e.setRoleid(rid);
    			list.add(e);
    		}
    	}
    	userRoleDao.BatchDeleteUserRoles(userIds);
    	userRoleDao.addUserRoleBatch(list);
    }
    
     public String InsertUpdateImpl(UserDto dto){
    	User record = dzmapper.map(dto, User.class);
		String id = record.getId();

    	if(id != null && id.length() != 0)
    		 userDao.updateByPrimaryKeySelective(record);
    	else 
    	{
    		id = UUID.randomUUID().toString();
    		record.setId(id);
    		 userDao.insertSelective(record);
    	}
    	return id;
    }
     
    //更新和新增，mq
    public boolean InsertUpdate(UserDto dto,String currUserId){
		String id = dto.getId();
		UserDto cuser;
    	if(id != null && id.length() != 0)
    	{
    		cuser = dto;
            if (currUserId != null && currUserId.length() != 0)
            {
                cuser.setCreateUserId(currUserId);
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dt=new Date();
                cuser.setCreateTime(sdf.format(dt));//.replace(" ", "T"));
            }
    	}
    	else
    	{
    		cuser = Get(dto.getId());
            cuser.setName(dto.getName()); 
            cuser.setUserName(dto.getUserName()); 
            cuser.setEmail(dto.getEmail()); 
            cuser.setMobileNumber(dto.getMobileNumber()); 
            cuser.setRemarks(dto.getRemarks()); 
    	}
    	MessageBase user=null;
    	updateMessage(cuser,user);
    	return true;
    }
    @SuppressWarnings("unchecked")
	private void updateMessage(UserDto dto,MessageBase replyMsg){
    	MessageBase delobj = new MessageBase();
    	delobj.setFailedTimes(0);
    	delobj.setMessageBodyReturnByte(null);
    	delobj.setexchangeName(QueueSerivce.exchangeName);
    	delobj.setMessageRouter("user_update_insertupdate_rpc");
    	delobj.setMessageBodyByte(JSON.toJSONBytes(dto));
//		Object reply = (Message<MessageBase>) queueSerivce.getAmqpTemplate().convertSendAndReceive(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
		RunnableQueueSucc<UserDto,MessageBase> succHandle
		= (x,y) -> {
			x.setId(JSON.parseObject( y.getMessageBodyReturnByte(), String.class)); 
			InsertOrUpdateCache(x);
		};
		try {
			queueSerivce.Request(dto, delobj, replyMsg, succHandle, 0);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    }
    private void InsertOrUpdateCache(UserDto userdto)
    {
        if (userdto != null)
        {
        	RunnableCompare<UserDto> handler = (t) -> { return userdto.getId().equals(t.getId()); };
            cacheService.GetClass(userdto);//T GetClass
            cacheService.SortedSetUpdate(MyConstants._instance+Key+userdto.getDepartmentId(), userdto, handler, false);
        }
    }
    
    public void Delete(String id){
    	List<String> ids = new ArrayList<String>(1);
    	if(!"".equals(ids))
    		ids.add(id);
    	DeleteBatch(ids);
    }
    
    @Transactional
    public void DeleteBatchImpl(List<String> userIds){
    	userRoleDao.BatchDeleteUserRoles(userIds);
    	userDao.deleteBatchByPrimaryKey(userIds);
    }
    private void DeleteCache(List<String> ids)
    {
        List<UserDto> userdtos = dzmapper.mapList(userDao.GetListByIds(ids),UserDto.class);
        if (userdtos != null)
        {
        	RunnableCompare<UserDto> handler=null;
            for(UserDto userdto : userdtos)
            {
            	handler = (t) -> { return userdto.getId().equals(t.getId()); };
            	cacheService.GetClass(userdto);//T GetClass
                cacheService.SortedSetUpdate(MyConstants._instance+Key+userdto.getDepartmentId(), userdto, handler, true);
            }
        }
    }
    //批量删除,mq
    public void DeleteBatch(List<String> ids){
    	if(ids.isEmpty()) return;
    	DeleteCache(ids);
    	MessageBase delobj = new MessageBase();
    	delobj.setFailedTimes(0);
    	delobj.setMessageBodyReturnByte(null);
    	delobj.setexchangeName(QueueSerivce.exchangeName);
    	delobj.setMessageRouter("user_delete_deleteuser_normal");
    	delobj.setMessageBodyByte(JSON.toJSONBytes(ids));
    	queueSerivce.getAmqpTemplate().convertAndSend(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
    }
    
    public String ResetPassword(ResetPasswordModel rpm){   	
    	if(!rpm.getNewPassword().equals(rpm.getNewPassword2())){
    		return "新密码和确认新密码不匹配";
    	}
    	User obj = userDao.selectByPrimaryKey(rpm.getResetPasswordId());
    	if(rpm.getOldPassword().equals( obj.getPassword()) ){
    		obj.setPassword(rpm.getNewPassword());
    		userDao.updateByPrimaryKeySelective(obj);
    		return "Success";
    	}else{
    		return "旧密码不正确";
    	}
    	 
    }
}
