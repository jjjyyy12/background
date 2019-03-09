package auth.background.repositories.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import auth.background.domain.bean.User;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    User CheckUser(String username,String passowrd);
    
    List<User> GetAllList();
    
    List<User> GetChildrenByDepartment(String departmentid);
    
    List<User> GetListByIds(List<String> list);
    
    int GetChildrenByDepartmentCount(String departmentid);
    
    int deleteBatchByPrimaryKey(List<String> list);
    

}