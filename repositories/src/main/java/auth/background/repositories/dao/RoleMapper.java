package auth.background.repositories.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import auth.background.domain.bean.Role;
import auth.background.domain.bean.User;

@Repository
public interface RoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);
    
    List<Role> GetAllList();
    
    int GetAllListCount();
    
    List<Role> GetListByIds(List<String> list);
    
    int deleteBatchByPrimaryKey(List<String> list);
}