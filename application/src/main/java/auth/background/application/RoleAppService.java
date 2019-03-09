package auth.background.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import auth.background.domain.bean.Role;
import auth.background.domain.bean.RoleMenuKey;
import auth.background.domain.bean.User;
import auth.background.domain.bean.UserRoleKey;
import auth.background.utils.config.RedisConstants;
import auth.background.repositories.dao.RoleMapper;
import auth.background.repositories.dao.RoleMenuMapper;
import auth.background.domain.dto.MessageBase;
import auth.background.domain.dto.RoleDto;
import auth.background.domain.dto.RoleMenuDto;
import auth.background.domain.dto.RoleMenuMsg;
import auth.background.domain.dto.UserDto;
import auth.background.domain.dto.msg.role_delete_deleterole_normal;
import auth.background.domain.dto.msg.role_update_insertupdate_rpc;
import auth.background.domain.dto.msg.role_update_rolemenus_normal;
import auth.background.application.common.CacheService;
import auth.background.application.common.QueueSerivce;
import auth.background.application.runnable.RunnableCacheList;
import auth.background.application.runnable.RunnableCacheSignel;
import auth.background.application.runnable.RunnableCompare;
import auth.background.application.runnable.RunnableQueueSucc;
import auth.background.utils.util.BeanMapper;
import auth.background.utils.util.PageHelper;

@Service
public class RoleAppService {
    @Resource
    private RoleMapper roleDao;
    @Resource
    private RoleMenuMapper roleMenuDao;
	@Resource
    private BeanMapper dzmapper;
	@Resource
    private CacheService<RoleDto> cacheService;
	@Resource
    private CacheService<List<RoleMenuDto>> cacheService2;
	@Resource
	private QueueSerivce<RoleDto> queueSerivce;
	
	//查询role
    public RoleDto Get(String id){
    	if(id==null||id.length()<=0) return null;
    	String key = RedisConstants._instance+RedisConstants.RoleKey+id;
    	RunnableCacheSignel<RoleDto,String> handler = (x) -> 
    	{ 
    		return dzmapper.map(roleDao.selectByPrimaryKey(x), RoleDto.class); 
    	};
    	return cacheService.Get(handler, key, id);
    }

    public List<RoleDto> GetAllList()
    {
    	String key = RedisConstants._instance+RedisConstants.RoleKey;
    	RunnableCacheList<RoleDto> handler = () -> 
    	{ 
    		List<Role> llist = roleDao.GetAllList();
    		return dzmapper.mapList(llist,  RoleDto.class); 
    	};
    	return cacheService.GetSortList(handler, key, 0, 0,-1);
    }
 
    public List<RoleDto> GetListPaged(int startPage, int pageSize){
    	List<RoleDto> list = GetAllList();
    	PageHelper<RoleDto> ph= new PageHelper<RoleDto>();
    	return ph.paged(list, startPage, pageSize, list.size());
    }
    //单个删除
    public void Delete(String id){
    	List<String> ids = new ArrayList<String>(1);
    	if(!"".equals(ids))
    		ids.add(id);
    	DeleteBatch(ids);
    }
    //批量删除,mq
    public void DeleteBatch(List<String> ids){
    	if(ids.isEmpty()) return;
    	DeleteCache(ids);
    	role_delete_deleterole_normal delobj = new role_delete_deleterole_normal(QueueSerivce.exchangeName, ids);
    	queueSerivce.getAmqpTemplate().convertAndSend(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
    }
     
    public void DeleteBatchImpl(List<String> Ids){
    	roleDao.deleteBatchByPrimaryKey(Ids);
    }
    private void DeleteCache(List<String> ids)
    {
        List<RoleDto> list = GetAllList();
        List<RoleDto> dtos = new ArrayList<RoleDto>(ids.size());
        for(RoleDto dto : list)
        	dtos.add(dto);
        if (dtos != null)
        {
        	RunnableCompare<RoleDto> handler=null;
            for(RoleDto dto : dtos)
            {
            	handler = (t) -> { return dto.getId().equals(t.getId()); };
            	cacheService.GetClass(dto);//T GetClass
                cacheService.SortedSetUpdate(RedisConstants._instance+RedisConstants.RoleKey, dto, handler, true);
                DeleteCache(dto.getId());
            }
        }
    }
    private void DeleteCache(String id)
    {
        List<String> keys = new ArrayList<String>(3);
        keys.add(RedisConstants.RoleKey+id);
        keys.add(RedisConstants.RoleMenuKey);
        keys.add(RedisConstants.RoleMenuKey+id);
        for(String rid : keys)
        {
        	cacheService.Remove(rid);//RemoveAllAsync 需要key落在同一个solt上
        }
    }
    
    //新增或更新具体实现
    public String InsertUpdateImpl(RoleDto dto){
    	Role record = dzmapper.map(dto, Role.class);
		String id = record.getId();

    	if(id != null && id.length() != 0)
    		 roleDao.updateByPrimaryKeySelective(record);
    	else 
    	{
    		id = UUID.randomUUID().toString();
    		record.setId(id);
    		 roleDao.insertSelective(record);
    	}
    	return id;
    }
     
    //更新和新增，mq
    public boolean InsertUpdate(RoleDto dto,String currUserId){
		String id = dto.getId();
		RoleDto cuser;
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
            cuser.setCode(dto.getCode());
            cuser.setRemarks(dto.getRemarks()); 
    	}
    	MessageBase user=null;
    	updateMessage(cuser,user);
    	return true;
    }
    //更新或新增发消息
    @SuppressWarnings("unchecked")
	private void updateMessage(RoleDto dto,MessageBase replyMsg){
    	role_update_insertupdate_rpc obj = new role_update_insertupdate_rpc(QueueSerivce.exchangeName,dto);
//		Object reply = (Message<MessageBase>) queueSerivce.getAmqpTemplate().convertSendAndReceive(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
		RunnableQueueSucc<RoleDto,MessageBase> succHandle
		= (x,y) -> {
			x.setId(JSON.parseObject( y.getMessageBodyReturnByte(), String.class)); 
			InsertOrUpdateCache(x);
		};
		try {
			queueSerivce.Request(dto, obj, replyMsg, succHandle, 0);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
    }
    private void InsertOrUpdateCache(RoleDto dto)
    {
        if (dto != null)
        {
        	RunnableCompare<RoleDto> handler = (t) -> { return dto.getId().equals(t.getId()); };
            cacheService.GetClass(dto);//T GetClass
            cacheService.SortedSetUpdate(RedisConstants._instance + RedisConstants.RoleKey, dto, handler, false);
            DeleteCache(dto.getId());
        }
    }
    
    public List<RoleMenuDto> GetRoleMenus(String Id)
    {
        String tkey = RedisConstants.RoleMenuKey + Id;
        RunnableCacheSignel<List<RoleMenuDto>,String> handler = (x) -> 
    	{ 
    		return dzmapper.mapList(roleMenuDao.GetRoleMenus(x), RoleMenuDto.class); 
    	};
    	return cacheService2.Get(handler, tkey, Id);
    }
    //更新role的menus
    public void UpdateRowMenus(String id, List<String> menuIds)
    {
    	if(id.isEmpty()) return;
    	DeleteCache(id);
    	RoleMenuMsg msg = new RoleMenuMsg();
    	msg.Id=id;
    	msg.menuIds = menuIds;
    	role_update_rolemenus_normal obj = new role_update_rolemenus_normal(QueueSerivce.exchangeName, msg);
    	queueSerivce.getAmqpTemplate().convertAndSend(QueueSerivce.exchangeName, obj.getMessageRouter(), obj);
    }
    @Transactional
    public void UpdateRowMenusImpl(String id, List<String> menuIds){
    	List<RoleMenuKey> list = new ArrayList<RoleMenuKey>();
    	RoleMenuKey e = null;
    	for(String uid : menuIds){
    			e=new RoleMenuKey();
    			e.setMenuid(uid);
    			e.setRoleid(id);
    			list.add(e);
    	}
    	roleMenuDao.RemoveRowMenus(id);
    	roleMenuDao.BatchAddRowMenus(list);
    }
    
    public List<RoleMenuDto> GetAllRowMenus()
    {
        String tkey = RedisConstants.RoleMenuKey;
        RunnableCacheSignel<List<RoleMenuDto>,String> handler = (x) -> 
    	{ 
    		return dzmapper.mapList(roleMenuDao.GetAllRoleMenus(), RoleMenuDto.class); 
    	};
    	return cacheService2.Get(handler, tkey, "");
    }
    public List<RoleMenuDto> GetUserRowMenus(List<String> roleIds)
    {
        List<RoleMenuDto> rlist = GetAllRowMenus();
        Iterator<RoleMenuDto> iter = rlist.iterator();  
        while(iter.hasNext()){  
        	RoleMenuDto t = iter.next();  
            if(!roleIds.contains(t.getRoleId())){  
                iter.remove();  
            }
        }
        return rlist;
    }
    public List<String> GetUserRoleMenusUrls(List<String> roleIds)
    {
        List<RoleMenuDto> rlist = GetUserRowMenus(roleIds);
        List<String> slist = new ArrayList<String>(rlist.size());
        for(RoleMenuDto rid : rlist)
        {
        	slist.add(rid.getUrl());
        }
        return slist;
    }
 
}
