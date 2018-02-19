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

import auth.background.dto.BatchUserRoleModel;
import auth.background.dto.DepartmentDto;
import auth.background.dto.PagedObj;
import auth.background.dto.ResetPasswordModel;
import auth.background.dto.ResultObj;
import auth.background.dto.TreeModel;
import auth.background.dto.UserDto;
import auth.background.dto.UserRoleModel;
import auth.background.service.DepartmentAppService;
import auth.background.service.UserAppService;

@RestController
@RequestMapping("/User")
public class UserController extends ControllerBase {
    @Resource
    private UserAppService _service;
    @Resource
    private DepartmentAppService _departmentService;
    
    @RequestMapping("/GetTreeData")
    public List<TreeModel> GetTreeData()
    {
    	List<DepartmentDto> dtos = _departmentService.GetAllList();
        List<TreeModel> treeModels = new ArrayList<TreeModel>();
        for (DepartmentDto dto : dtos)
        {
        	TreeModel item = new TreeModel();
        	item.setId(dto.getId());
        	item.setText(dto.getName());
        	item.setParent(dto.getParentid().length() == 0 ? "#" : dto.getParentid() );
            treeModels.add(item);
        }
        return  treeModels;
    }
    
    @RequestMapping("/{id}")
    public UserDto Get(@PathVariable("id") String id)
    {
    	return _service.Get(id);
    }
    
    @RequestMapping("/GetChildrenByParent/{departmentId}")
    public PagedObj GetChildrenByParent(@PathVariable("departmentId") String departmentId,@RequestParam("startPage") int startPage,@RequestParam("pageSize") int pageSize){
    	 int rowCount = _service.GetChildrenByDepartmentCount(departmentId);
         List<UserDto> result = _service.GetChildrenByDepartment(departmentId, startPage, pageSize, rowCount);
         return Paged(result,rowCount,startPage,pageSize);
    }
    
    //http://localhost:8080/User/Edit   post：  {"id":"0ad96163-e577-42c9-19f4-08d48b9df64b","createtime":1493099210421,"createuserid":"72e5b5f5-24f1-46e1-8309-08d411e1c631","departmentid":"e20af586-bca7-42bd-efa1-08d411e2b01c","email":"8","isdeleted":0,"lastlogintime":-62135798400000,"logintimes":0,"mobilenumber":"8","name":"8","password":"8","remarks":"8","username":"88"}
    //{ "dto": { "Id":"08d4b7be-d4c4-915a-1eb3-6526503bd369", "DepartmentId":"e20af586-bca7-42bd-efa1-08d411e2b01c", "Name":"12", "UserName": "12", "Email":"12", "MobileNumber":"12", "Remarks":"12", "Password":"12" } }
    @SuppressWarnings("null")
	@RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResultObj Create(@RequestBody UserDto dto,HttpServletRequest request){
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
    			res.setMessage("no data to create");
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
	@RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public ResultObj Edit(@RequestBody UserDto dto,HttpServletRequest request){
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
    
    @RequestMapping(value="/ResetPassword", method = RequestMethod.PUT)
    @ResponseBody
    public ResultObj ResetPassword(@RequestBody ResetPasswordModel rpm){
    	ResultObj res = new ResultObj();
    	try{
    		String resMsg= _service.ResetPassword(rpm);
    		if("Success".equals(resMsg)){
    			res.setResult("Success");
    		}
    		else{
    			res.setResult("Faild");
        		res.setMessage(resMsg);
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
    
    @RequestMapping(value="/BatchUserRole", method = RequestMethod.POST)
    @ResponseBody
    public ResultObj BatchUserRole(@RequestBody BatchUserRoleModel rpm){
    	ResultObj res = new ResultObj();
    	try{
            List<String> uIds =  GetList(rpm.getUserIDs(),"_");
            List<String> rIds = GetList(rpm.getRoleIDs(),"_");
            _service.BatchUpdateUserRoles(uIds,rIds);
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
    @RequestMapping(value="/UserRole", method = RequestMethod.POST)
    @ResponseBody
    public ResultObj UserRole(@RequestBody UserRoleModel rpm){
    	ResultObj res = new ResultObj();
    	try{
    		if(rpm==null)
    			throw new Exception("UserID is null");
    		
            List<String> uIds = new ArrayList<String>(1);
            uIds.add(rpm.getUserRoleId());
            List<String> rIds = GetList(rpm.getRoleIDs(),"_");
            _service.BatchUpdateUserRoles(uIds,rIds);
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
    
}
