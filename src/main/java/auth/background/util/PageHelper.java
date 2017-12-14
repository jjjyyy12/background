package auth.background.util;

import java.util.List;

public class PageHelper<T> {
	public List<T> paged(List<T> rlist,int startPage, int pageSize,int count){
    	startPage = startPage<=0 ? 1 : startPage;
    	pageSize = pageSize<=0 ? 1 : pageSize;
    	int beg = (startPage - 1) * pageSize;
    	int end = count < pageSize*startPage ? count : ((startPage - 1) * pageSize + pageSize);
     	if(beg>count) beg=count;
    	if(beg>end) end=beg;
    	return rlist.subList(beg==0?0:beg-1,end-1);
	}
}
