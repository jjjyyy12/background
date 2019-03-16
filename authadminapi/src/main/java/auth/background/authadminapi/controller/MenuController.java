package auth.background.authadminapi.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import auth.background.domain.dto.MenuDto;
import auth.background.domain.dto.PagedObj;
import auth.background.domain.dto.ResultObj;
import auth.background.domain.dto.TreeModel;
import auth.background.application.MenuAppService;

@RestController
@RequestMapping("/Menu")
public class MenuController extends ControllerBase {
    @Resource
    private MenuAppService _service;
	
    @RequestMapping("/Get")
    public MenuDto Get(@RequestParam("id") String id)
    {
    	return _service.Get(id);
    }
    @RequestMapping("/GetTreeData")
    public List<TreeModel> GetTreeData()
    {
    	List<MenuDto> dtos = _service.GetAllList();
        List<TreeModel> treeModels = new ArrayList<TreeModel>();
        for (MenuDto dto : dtos)
        {
        	TreeModel item = new TreeModel();
        	item.setId(dto.getId());
        	item.setText(dto.getName());
        	item.setParent(dto.getParentid().length() == 0 ? "#" : dto.getParentid() );
            treeModels.add(item);
        }
        return  treeModels;
    }
    /// <summary>
    /// 获取子级功能列表
    /// </summary>
    /// <param name="parentId">父id</param>
    /// <param name="startPage">开始页数</param>
    /// <param name="pageSize">每页记录</param>
    /// <returns></returns>
    // GET api/v1/[controller]/GetMenusByParent/1[?pageSize=3&pageIndex=10]
    @RequestMapping("/GetMenusByParent/{parentId}")
    public PagedObj GetMenusByParent(@PathVariable("parentId") String parentId, int startPage, int pageSize){
         List<MenuDto> result = _service.GetMenusByParent(parentId, startPage, pageSize);
         int rowCount = result.size();
         return Paged(result,rowCount,startPage,pageSize);
    }
    
    //http://localhost:8080/User/Edit   post：  {"id":"0ad96163-e577-42c9-19f4-08d48b9df64b","createtime":1493099210421,"createuserid":"72e5b5f5-24f1-46e1-8309-08d411e1c631","departmentid":"e20af586-bca7-42bd-efa1-08d411e2b01c","email":"8","isdeleted":0,"lastlogintime":-62135798400000,"logintimes":0,"mobilenumber":"8","name":"8","password":"8","remarks":"8","username":"88"}
    //{ "dto": { "Id":"08d4b7be-d4c4-915a-1eb3-6526503bd369", "DepartmentId":"e20af586-bca7-42bd-efa1-08d411e2b01c", "Name":"12", "UserName": "12", "Email":"12", "MobileNumber":"12", "Remarks":"12", "Password":"12" } }
    @SuppressWarnings("null")
	@RequestMapping(value="/Create", method = RequestMethod.POST)
    @ResponseBody
    public ResultObj Create(@RequestBody MenuDto dto,HttpServletRequest request){
    	ResultObj res = new ResultObj();
    	Object cu = request.getSession().getAttribute("CurrentUser");
    	if(cu==null||cu.toString().length()==0)
    		request.getSession().setAttribute("CurrentUser", "226b44f4-9afc-4dbd-d2c6-08d40ad7befc");
    	String currUserId = request.getSession().getAttribute("CurrentUser").toString();
    	try{
    		if(_service.InsertUpdate(dto,currUserId))
    			res.setResult("Success");
    		else{
    			res.setResult("Faild");
    			res.setMessage("no data to edit");
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		res.setResult("Faild");
    		res.setMessage(ex.getMessage());
    	}
    	return res;
    }
    @SuppressWarnings("null")
	@RequestMapping(value="/Edit", method = RequestMethod.PUT)
    @ResponseBody
    public ResultObj Edit(@RequestBody MenuDto dto,HttpServletRequest request){
    	ResultObj res = new ResultObj();
    	try{
    		if(_service.InsertUpdate(dto,null))
    			res.setResult("Success");
    		else{
    			res.setResult("Faild");
    			res.setMessage("no data to edit");
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		res.setResult("Faild");
    		res.setMessage(ex.getMessage());
    	}
    	return res;
    }
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResultObj Delete(@PathVariable("id") String id){
    	ResultObj res = new ResultObj();
    	try{
    		_service.Delete(id);
    		res.setResult("Success");
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		res.setResult("Faild");
    		res.setMessage(ex.getMessage());
    	}
    	return res;
    }
    
    @RequestMapping(value="/DeleteMuti/{ids}", method = RequestMethod.DELETE)
    public ResultObj DeleteBatch(@PathVariable("ids") String ids){
    	ResultObj res = new ResultObj();
    	try{
    		List<String> uids = GetList(ids,",");
    		if(uids.size() > 0){
    			_service.DeleteBatch(uids);
    			res.setResult("Success");
    		}
    		else{
    			res.setResult("Failed");
    			res.setMessage("no data to delete");
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		res.setResult("Faild");
    		res.setMessage(ex.getMessage());
    	}
    	return res;
    }
    
    
}
