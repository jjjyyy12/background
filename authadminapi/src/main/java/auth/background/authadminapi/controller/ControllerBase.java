package auth.background.authadminapi.controller;

import java.util.ArrayList;
import java.util.List;

import auth.background.domain.dto.PagedObj;

public abstract class ControllerBase {
	 protected List<String> GetList(String ids,String sp){
         String[] idArray = ids.split(sp);
         List<String> Ids = new ArrayList<String>();
         int j = idArray.length;
         for (int i = 0; i < j; i++)
             Ids.add(idArray[i]);
         return Ids;
     }
	 
	 protected PagedObj Paged(List<?> result,int rowCount, int startPage, int pageSize){
		 PagedObj pageobj = new PagedObj();
         pageobj.setRowCount(rowCount);
         int pageCount=rowCount / pageSize;
         if(rowCount % pageSize >0 )
        	 pageCount++;
         pageobj.setPageCount( pageCount==0 ? 1 : pageCount);
         pageobj.setRows(result);
         return pageobj;
	 }
}
