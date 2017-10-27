package auth.background.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

 




import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import auth.background.bean.User;
import auth.background.dto.BatchUserRoleModel;
import auth.background.dto.PagedObj;
import auth.background.dto.ResetPasswordModel;
import auth.background.dto.ResultObj;
import auth.background.dto.UserDto;
import auth.background.dto.UserRoleModel;
import auth.background.service.UserAppService;

@RestController
@RequestMapping("/User")
public class UserController {
    @Resource
    private UserAppService _service;
	
    @RequestMapping("/Get")
    public UserDto Get(@RequestParam("id") String id)
    {
    	return _service.Get(id);
    }
    
    @RequestMapping("/GetChildrenByParent")
    public PagedObj GetChildrenByParent(String departmentId, int startPage, int pageSize){
    	 int rowCount = _service.GetChildrenByDepartmentCount(departmentId);
         List<UserDto> result = _service.GetChildrenByDepartment(departmentId, startPage, pageSize, rowCount);
         PagedObj pageobj = new PagedObj();
         pageobj.setRowCount(rowCount);
         int pageCount=rowCount / pageSize;
         if(rowCount % pageSize >0 )
        	 pageCount++;
         pageobj.setPageCount( pageCount==0 ? 1 : pageCount);
         pageobj.setRows(result);
         return pageobj;
    }
    
    //http://localhost:8080/User/Edit   post：  {"id":"0ad96163-e577-42c9-19f4-08d48b9df64b","createtime":1493099210421,"createuserid":"72e5b5f5-24f1-46e1-8309-08d411e1c631","departmentid":"e20af586-bca7-42bd-efa1-08d411e2b01c","email":"8","isdeleted":0,"lastlogintime":-62135798400000,"logintimes":0,"mobilenumber":"8","name":"8","password":"8","remarks":"8","username":"88"}
    //{ "dto": { "Id":"08d4b7be-d4c4-915a-1eb3-6526503bd369", "DepartmentId":"e20af586-bca7-42bd-efa1-08d411e2b01c", "Name":"12", "UserName": "12", "Email":"12", "MobileNumber":"12", "Remarks":"12", "Password":"12" } }
    @SuppressWarnings("null")
	@RequestMapping(value="/Edit", method = RequestMethod.POST)
    @ResponseBody
    public ResultObj Edit(@RequestBody UserDto dto,HttpServletRequest request){
    	ResultObj res = new ResultObj();
    	Object cu = request.getSession().getAttribute("CurrentUser");
    	if(cu==null||cu.toString().length()==0)
    		request.getSession().setAttribute("CurrentUser", "226b44f4-9afc-4dbd-d2c6-08d40ad7befc");
    	String currUserId = request.getSession().getAttribute("CurrentUser").toString();
    	try{
    		if(_service.InsertUpdate(dto,currUserId) )
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
    
    @RequestMapping(value="/Delete", method = RequestMethod.POST)
    public ResultObj Delete(String id){
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
    
    @RequestMapping(value="/DeleteMuti", method = RequestMethod.POST)
    public ResultObj DeleteBatch(String ids){
    	ResultObj res = new ResultObj();
    	try{
    		String[] idsList = ids.split(",");
    		List<String> uids=null;
    		if(idsList.length>0){
    			uids = Arrays.asList(idsList);
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
    
    @RequestMapping(value="/ResetPassword", method = RequestMethod.POST)
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
    		String[] userIds = rpm.getUserIDs().split("_");
    		String[] roleIds = rpm.getRoleIDs().split("_");
            List<String> uIds = new ArrayList<String>();
            List<String> rIds = new ArrayList<String>();

            for(String rid : roleIds)
            {
                rIds.add(rid);
            }
            for(String uid : userIds)
            {
            	uIds.add(uid);
            }
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
    		
    		String[] roleIds = rpm.getRoleIDs().split("_");
            List<String> uIds = new ArrayList<String>(1);
            uIds.add(rpm.getUserRoleId());
            List<String> rIds = new ArrayList<String>();

            for(String rid : roleIds)
            {
                rIds.add(rid);
            }
 
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
