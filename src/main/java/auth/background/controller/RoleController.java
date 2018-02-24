package auth.background.controller;

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

import auth.background.dto.DepartmentDto;
import auth.background.dto.MenuDto;
import auth.background.dto.PagedObj;
import auth.background.dto.ResultObj;
import auth.background.dto.RoleDto;
import auth.background.dto.RoleMenuDto;
import auth.background.dto.RoleMenuModel;
import auth.background.dto.TreeCheckBoxModel;
import auth.background.dto.TreeModel;
import auth.background.service.DepartmentAppService;
import auth.background.service.MenuAppService;
import auth.background.service.RoleAppService;

@RestController
@RequestMapping("/Role")
public class RoleController extends ControllerBase {
    @Resource
    private RoleAppService _service;
    @Resource
    private MenuAppService _menuAppService;
    
    @RequestMapping("/Get")
    public RoleDto Get(@RequestParam("id") String id)
    {
    	return _service.Get(id);
    }
    /// <summary>
    /// 获取角色对应的功能列表
    /// </summary>
    /// <param name="id">roleid</param>
    /// <returns></returns>
    // GET api/v1/[controller]/GetMenuTreeData/id
    @RequestMapping("/GetMenuTreeData/{id}")
    public List<TreeCheckBoxModel> GetMenuTreeData(@PathVariable("id") String id)
    {
    	List<MenuDto> dtos = _menuAppService.GetAllList();
    	List<RoleMenuDto> rdtos = _service.GetRoleMenus(id);
        List<TreeCheckBoxModel> treeModels = new ArrayList<TreeCheckBoxModel>();
        for (MenuDto dto : dtos)
        {
        	String checked="0";
        	for(RoleMenuDto rdto : rdtos){
        		if(dto.getId().equals(rdto.getMenuId() )){
        			checked = "1";
        		}
        	}
        	TreeCheckBoxModel item = new TreeCheckBoxModel();
        	item.setId(dto.getId());
        	item.setText(dto.getName());
        	item.setParent(dto.getParentid().length() == 0 ? "#" : dto.getParentid() );
        	item.setChecked(checked);
            treeModels.add(item);
        }
        return  treeModels;
    }
    /// <summary>
    /// 获取权限列表
    /// </summary>
    /// <returns></returns>
    // GET api/v1/[controller]/GetListPaged[?pageSize=3&pageIndex=10]
    @RequestMapping("/GetListPaged")
    public PagedObj GetListPaged(int startPage, int pageSize){
         List<RoleDto> result = _service.GetListPaged(startPage, pageSize);
         int rowCount = result.size();
         return Paged(result,rowCount,startPage,pageSize);
    }
    
    //http://localhost:8080/User/Edit   post：  {"id":"0ad96163-e577-42c9-19f4-08d48b9df64b","createtime":1493099210421,"createuserid":"72e5b5f5-24f1-46e1-8309-08d411e1c631","departmentid":"e20af586-bca7-42bd-efa1-08d411e2b01c","email":"8","isdeleted":0,"lastlogintime":-62135798400000,"logintimes":0,"mobilenumber":"8","name":"8","password":"8","remarks":"8","username":"88"}
    //{ "dto": { "Id":"08d4b7be-d4c4-915a-1eb3-6526503bd369", "DepartmentId":"e20af586-bca7-42bd-efa1-08d411e2b01c", "Name":"12", "UserName": "12", "Email":"12", "MobileNumber":"12", "Remarks":"12", "Password":"12" } }
    @SuppressWarnings("null")
	@RequestMapping(value="/Create", method = RequestMethod.POST)
    @ResponseBody
    public ResultObj Create(@RequestBody RoleDto dto,HttpServletRequest request){
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
       public ResultObj Edit(@RequestBody RoleDto dto,HttpServletRequest request){
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
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
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
    
    /// <summary>
    /// 更新rolemenu
    /// </summary>
    /// <param name="rpm"></param>
    /// <returns></returns>
    @RequestMapping(value="/RoleMenu", method = RequestMethod.PUT)
    public ResultObj RoleMenu( RoleMenuModel rpm)
    {
    	ResultObj res = new ResultObj();
        try
        {
             
            List<String> dto = GetList(rpm.getMenuIDs(),"_");
            _service.UpdateRowMenus(rpm.getRoleMenuId(), dto);
      
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
    
    /// <summary>
    /// 得到左侧菜单列表
    /// </summary>
    /// <param name="ids">roleids</param>
    /// <returns></returns>
    // Get api/Role/GetRowMenuForLeftMenu/5,6

    @RequestMapping(value="/GetRowMenuForLeftMenu/{ids}")
    public List<RoleMenuDto> GetRowMenuForLeftMenu(@PathVariable("ids") String ids)
    {
        List<String> Ids = GetList(ids,",");
        return  _service.GetUserRowMenus(Ids);
    }
    
    /// <summary>
    /// 得到用户菜urls
    /// </summary>
    /// <param name="ids">roleids</param>
    /// <returns></returns>
    // Get api/Role/GetUserRowMenusUrls/5,6
    @RequestMapping(value="/GetUserRowMenusUrls/{ids}")
    public List<String> GetUserRoleMenusUrls(@PathVariable("ids") String ids)
    {
        List<String> Ids = GetList(ids, ",");
        return _service.GetUserRoleMenusUrls(Ids);
    }
}
