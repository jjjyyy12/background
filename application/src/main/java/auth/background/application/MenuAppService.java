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

import auth.background.domain.bean.Menu;

import auth.background.domain.bean.User;

import auth.background.utils.config.RedisConstants;
import auth.background.repositories.dao.MenuMapper;
import auth.background.domain.dto.DepartmentDto;
import auth.background.domain.dto.MenuDto;
import auth.background.domain.dto.MessageBase;

import auth.background.domain.dto.UserDto;
import auth.background.domain.dto.msg.menu_delete_deletemenu_normal;
import auth.background.domain.dto.msg.menu_update_insertupdate_rpc;
import auth.background.application.common.CacheService;
import auth.background.application.common.QueueSerivce;
import auth.background.application.runnable.RunnableCacheList;
import auth.background.application.runnable.RunnableCacheSignel;
import auth.background.application.runnable.RunnableCompare;
import auth.background.application.runnable.RunnableQueueSucc;
import auth.background.utils.util.BeanMapper;
import auth.background.utils.util.PageHelper;

@Service
public class MenuAppService {
    @Resource
    private MenuMapper menuDao;
 
	@Resource
    private BeanMapper dzmapper;
	@Resource
    private CacheService<MenuDto> cacheService;
 
	@Resource
	private QueueSerivce<MenuDto> queueSerivce;
	
	//查询menu
    public MenuDto Get(String id){
    	if(id==null||id.length()<=0) return null;
    	String key = RedisConstants._instance+RedisConstants.MenuKey+id;
    	RunnableCacheSignel<MenuDto,String> handler = (x) -> 
    	{ 
    		return dzmapper.map(menuDao.selectByPrimaryKey(x), MenuDto.class); 
    	};
		cacheService.GetClass(new MenuDto());
    	return cacheService.Get(handler, key, id);
    }

    public List<MenuDto> GetAllList()
    {
    	String key = RedisConstants._instance+RedisConstants.MenuKey;
    	RunnableCacheList<MenuDto> handler = () -> 
    	{ 
    		List<Menu> llist = menuDao.GetAllList();
    		return dzmapper.mapList(llist,  MenuDto.class); 
    	};
		cacheService.GetClass(new MenuDto());
    	return cacheService.GetSortList(handler, key, 0, 0,-1);
    }
 
    public List<MenuDto> GetListPaged(int startPage, int pageSize){
    	List<MenuDto> list = GetAllList();
    	PageHelper<MenuDto> ph= new PageHelper<MenuDto>();
    	return ph.paged(list, startPage, pageSize, list.size());
    }
    public List<MenuDto> GetMenusByParent(String parentId,int startPage, int pageSize){
    	List<MenuDto> list = GetAllList();
    	List<MenuDto> rlist = new ArrayList<MenuDto>();
    	for (int i = 0,j = list.size(); i < j; i++){
    		MenuDto item = list.get(i);
    		if(parentId.equals(item.getId()))
    			rlist.add(item);
    	}
     	PageHelper<MenuDto> ph= new PageHelper<MenuDto>();
    	return ph.paged(rlist, startPage, pageSize, list.size());
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
    	menu_delete_deletemenu_normal delobj = new menu_delete_deletemenu_normal(QueueSerivce.exchangeName, ids);
    	queueSerivce.getAmqpTemplate().convertAndSend(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
    }
     
    public void DeleteBatchImpl(List<String> Ids){
    	menuDao.deleteBatchByPrimaryKey(Ids);
    }
    private void DeleteCache(List<String> ids)
    {
        List<MenuDto> list = GetAllList();
        List<MenuDto> dtos = new ArrayList<MenuDto>(ids.size());
        for(MenuDto dto : list)
        	dtos.add(dto);
        if (dtos != null)
        {
        	RunnableCompare<MenuDto> handler=null;
            for(MenuDto dto : dtos)
            {
            	handler = (t) -> { return dto.getId().equals(t.getId()); };
            	cacheService.GetClass(dto);//T GetClass
                cacheService.SortedSetUpdate(RedisConstants._instance+RedisConstants.MenuKey, dto, handler, true);
                DeleteCache(dto.getId());
            }
        }
    }
    private void DeleteCache(String id)
    {
        List<String> keys = new ArrayList<String>(2);
        keys.add(RedisConstants.MenuKey+id);
        keys.add(RedisConstants.MenuKey);
        for(String rid : keys)
        {
        	cacheService.Remove(rid);//RemoveAllAsync 需要key落在同一个solt上
        }
    }
    
    //新增或更新具体实现
    public String InsertUpdateImpl(MenuDto dto){
    	Menu record = dzmapper.map(dto, Menu.class);
		String id = record.getId();

    	if(id != null && id.length() != 0)
    		menuDao.updateByPrimaryKeySelective(record);
    	else 
    	{
    		id = UUID.randomUUID().toString();
    		record.setId(id);
    		menuDao.insertSelective(record);
    	}
    	return id;
    }
     
    //更新和新增，mq
    public boolean InsertUpdate(MenuDto dto,String currUserId){
		String id = dto.getId();
		MenuDto cuser;
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
	private void updateMessage(MenuDto dto,MessageBase replyMsg){
    	menu_update_insertupdate_rpc obj = new menu_update_insertupdate_rpc(QueueSerivce.exchangeName,dto);
//		Object reply = (Message<MessageBase>) queueSerivce.getAmqpTemplate().convertSendAndReceive(QueueSerivce.exchangeName, delobj.getMessageRouter(), delobj);
		RunnableQueueSucc<MenuDto,MessageBase> succHandle
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
    private void InsertOrUpdateCache(MenuDto dto)
    {
        if (dto != null)
        {
        	RunnableCompare<MenuDto> handler = (t) -> { return dto.getId().equals(t.getId()); };
            cacheService.GetClass(dto);//T GetClass
            cacheService.SortedSetUpdate(RedisConstants._instance + RedisConstants.MenuKey, dto, handler, false);
            DeleteCache(dto.getId());
        }
    }
    
 
}
