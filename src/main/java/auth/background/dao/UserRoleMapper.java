package auth.background.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import auth.background.bean.UserRoleKey;
@Repository
public interface UserRoleMapper {
    int deleteByPrimaryKey(UserRoleKey key);

    int insert(UserRoleKey record);

    int insertSelective(UserRoleKey record);
    
    List<UserRoleKey> GetUserRoles(String userId);
    
    int BatchDeleteUserRoles(List<String> list);
    
    int addUserRoleBatch(List<UserRoleKey> list);
}