package auth.background.service;


import java.util.ArrayList;

import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import auth.background.bean.Department;
import auth.background.config.RedisConstants;
import auth.background.dao.DepartmentMapper;

import auth.background.dto.DepartmentDto;

import auth.background.dto.MessageBase;

import auth.background.dto.department_delete_deletedepartment_normal;
import auth.background.dto.department_update_insertupdate_rpc;

import auth.background.util.BeanMapper;
import auth.background.util.PageHelper;

@Service
public class DepartmentAppService {
    @Resource
    private DepartmentMapper departmentDao;
 
	@Resource
    private BeanMapper dzmapper;
	@Resource
    private CacheService<DepartmentDto> cacheService;
 
	@Resource
	private QueueSerivce<DepartmentDto> queueSerivce;
	
	//查询
    public DepartmentDto Get(String id){
    	if(id==null||id.length()<=0) return null;
    	String key = RedisConstants._instance+RedisConstants.DepartmentKey+id;
    	RunnableCacheSignel<DepartmentDto,String> handler = (x) -> 
    	{ 
    		return dzmapper.map(departmentDao.selectByPrimaryKey(x), DepartmentDto.class); 
    	};
    	return cacheService.Get(handler, key, id);
    }

    public List<DepartmentDto> GetAllList()
    {
    	String key = RedisConstants._instance+RedisConstants.DepartmentKey;
    	RunnableCacheList<DepartmentDto> handler = () -> 
    	{ 
    		List<Department> llist = departmentDao.GetAllList();
    		return dzmapper.mapList(llist,  DepartmentDto.class); 
    	};
    	return cacheService.GetSortList(handler, key, 0, 0,-1);
    }
 
    public List<DepartmentDto> GetListPaged(int startPage, int pageSize){
    	List<DepartmentDto> list = GetAllList();
    	PageHelper<DepartmentDto> ph= new PageHelper<DepartmentDto>();
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
    	department_delete_deletedepartment_normal delobj = new department_delete_deletedepartment_normal(QueueSerivce.exchangeName, ids);
    	queueSerivce.getAmqpTemplate().convertAndSend(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
    }
     
    public void DeleteBatchImpl(List<String> Ids){
    	departmentDao.deleteBatchByPrimaryKey(Ids);
    }
    private void DeleteCache(List<String> ids)
    {
        List<DepartmentDto> list = GetAllList();
        List<DepartmentDto> dtos = new ArrayList<DepartmentDto>(ids.size());
        for(DepartmentDto dto : list)
        	dtos.add(dto);
        if (dtos != null)
        {
        	RunnableCompare<DepartmentDto> handler=null;
            for(DepartmentDto dto : dtos)
            {
            	handler = (t) -> { return dto.getId().equals(t.getId()); };
            	cacheService.GetClass(dto);//T GetClass
                cacheService.SortedSetUpdate(RedisConstants._instance+RedisConstants.DepartmentKey, dto, handler, true);
                DeleteCache(dto.getId());
            }
        }
    }
    private void DeleteCache(String id)
    {
        List<String> keys = new ArrayList<String>(2);
        keys.add(RedisConstants.DepartmentKey+id);
        keys.add(RedisConstants.DepartmentKey);
        for(String rid : keys)
        {
        	cacheService.Remove(rid);//RemoveAllAsync 需要key落在同一个solt上
        }
    }
    
    //新增或更新具体实现
    public String InsertUpdateImpl(DepartmentDto dto){
    	Department record = dzmapper.map(dto, Department.class);
		String id = record.getId();

    	if(id != null && id.length() != 0)
    		departmentDao.updateByPrimaryKeySelective(record);
    	else 
    	{
    		id = UUID.randomUUID().toString();
    		record.setId(id);
    		departmentDao.insertSelective(record);
    	}
    	return id;
    }
     
    //更新和新增，mq
    public boolean InsertUpdate(DepartmentDto dto,String currUserId){
		String id = dto.getId();
		DepartmentDto cuser;
    	if(id != null && id.length() != 0)
    	{
    		cuser = dto;
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
	private void updateMessage(DepartmentDto dto,MessageBase replyMsg){
    	department_update_insertupdate_rpc obj = new department_update_insertupdate_rpc(QueueSerivce.exchangeName,dto);
//		Object reply = (Message<MessageBase>) queueSerivce.getAmqpTemplate().convertSendAndReceive(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
		RunnableQueueSucc<DepartmentDto,MessageBase> succHandle
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
    private void InsertOrUpdateCache(DepartmentDto dto)
    {
        if (dto != null)
        {
        	RunnableCompare<DepartmentDto> handler = (t) -> { return dto.getId().equals(t.getId()); };
            cacheService.GetClass(dto);//T GetClass
            cacheService.SortedSetUpdate(RedisConstants._instance + RedisConstants.DepartmentKey, dto, handler, false);
            DeleteCache(dto.getId());
        }
    }
    
 
}
