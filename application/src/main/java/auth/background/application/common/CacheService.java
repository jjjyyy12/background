package auth.background.application.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Tuple;

import com.alibaba.fastjson.JSON;
import auth.background.utils.config.RedisTemplateSelf;
import auth.background.application.runnable.RunnableCacheCount;
import auth.background.application.runnable.RunnableCacheList;
import auth.background.application.runnable.RunnableCacheSignel;
import auth.background.application.runnable.RunnableCompare;
import auth.background.utils.util.BeanMapper;
import java.lang.reflect.ParameterizedType;
@Service
public class CacheService<T> {
	@Resource
    private RedisTemplateSelf myRedisTemplate;
	@Resource
    private BeanMapper dzmapper;
	Class<? super T> rawType;
    Class<T> clazz;
    @SuppressWarnings("unchecked")
	public void GetClass( T t) {
    		this.clazz = (Class<T>) t.getClass();
    }
	public void GetClass2() {
    	if(rawType == null){
            Class <?> cla = getClass();
            rawType = (Class <T>) ( cla.getGenericSuperclass());
        }
	}

    //包装chche的getlist方法，先取缓存再取db
	@SuppressWarnings("unchecked")
	public List<T> GetSortList(RunnableCacheList<T> handler,String key, int startPage, int pageSize,int count){
		List<T> dlist;
		int max= startPage*pageSize;
		if(max>=count) max=count;
		if(startPage==0) max=-1;
//    	Set<String> zlist = null;
		Set<Tuple> zzlist = null;
    	if(count>-1)
			zzlist = myRedisTemplate.zrangeByScoreWithScores(key,startPage,max);
    	else
			zzlist = myRedisTemplate.zrangeByScoreWithScores(key, "-inf", "+inf");
    	if(zzlist.isEmpty()){
    		dlist = handler.getList();	 
        	if(!dlist.isEmpty()){
            	Map<String, Double> scoreMembers = new HashMap<String, Double>();
				String t = "";
          	  	for(int i=0, j=dlist.size();i<j;i++){
          	  		t = JSON.toJSONString(dlist.get(i));
          	  		scoreMembers.put(t,  (double)i+1);
          	  	}
          	  	myRedisTemplate.zadd(key, scoreMembers); 
          	  	myRedisTemplate.expire(key,60);
        	}
    	}
    	else{
    		dlist = new ArrayList<T>();
			for (Tuple tuple : zzlist) {
				dlist.add((T) JSON.parseObject(tuple.getElement(),this.clazz));
			}
//    		dlist = JSON.parseObject(zzlist.toString(),dlist.getClass());
//    		for(String dto : zlist){
//    			dlist.add((T) JSON.parseObject(dto,dlist.getClass()));
//    		}
    	}
		return dlist;
	}
	
	//根据id得到缓存对象，没有去db取
	public <M> T Get(RunnableCacheSignel<T,M> handler, String key,M id){
		String value = myRedisTemplate.get( key);
		if(value == null || value.length() <= 0) {
			return handler.get(id);
		}
		return (T)JSON.parseObject(value, this.clazz);
	}
	
	//更新缓存
	@SuppressWarnings("unchecked")
	public boolean SortedSetUpdate(String key,T inobj, RunnableCompare<T> handler,boolean delFlag){
		Set<Tuple> zlist = myRedisTemplate.zrangeByScoreWithScores( key, "+inf","-inf");
		boolean isNew = true;
		for(Tuple zitem : zlist)
        {
			String elem = zitem.getElement();
            if (handler.Compare((T)JSON.parseObject(elem, this.clazz)))
            {
            	isNew = false;
                myRedisTemplate.zrem(key, zitem.getElement());
                if(!delFlag)
                	myRedisTemplate.zadd(key,zitem.getScore(),JSON.toJSONString(inobj));
            }
        }
		 if (isNew)//insert
         {
         	myRedisTemplate.zadd(key,1,JSON.toJSONString(inobj));
         }
        return true;
	}
	
	//getcount包装
	public int zcard(RunnableCacheCount handler,String okey){
    	long cnt = myRedisTemplate.zcard(okey);
    	if(cnt==0)
    		return handler.getCount();
    	else 
    		return (int)cnt;
	}
	
	public void Remove(String key){
		myRedisTemplate.delete(key);
	}
}
