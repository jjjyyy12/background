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
import auth.background.dto.RoleDto;
import auth.background.dto.TreeCheckBoxModel;
import auth.background.dto.TreeModel;
import auth.background.dto.UserDto;
import auth.background.dto.UserRoleModel;
import auth.background.service.DepartmentAppService;
import auth.background.service.RoleAppService;
import auth.background.service.UserAppService;

@RestController
@RequestMapping("/User")
public class AuthorityController extends ControllerBase {
    @Resource
    private UserAppService _service;
    @Resource
    private RoleAppService _roleService;
    
    @RequestMapping("/GetBatchRoleTreeData")
    public List<TreeCheckBoxModel> GetBatchRoleTreeData()
    {
    	List<RoleDto> dtos = _roleService.GetAllList();
        List<TreeCheckBoxModel> treeModels = new ArrayList<TreeCheckBoxModel>();
        for (RoleDto dto : dtos)
        {
        	TreeCheckBoxModel item = new TreeCheckBoxModel();
        	item.setId(dto.getId());
        	item.setText(dto.getName());
        	item.setParent("#");
        	item.setChecked("0");
            treeModels.add(item);
        }
        return  treeModels;
    }
    
    
    @RequestMapping(value="/Batch", method = RequestMethod.PUT)
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
    @RequestMapping( method = RequestMethod.PUT)
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
