package auth.background.dao;

import org.springframework.stereotype.Repository;

import auth.background.bean.RoleMenu;
import auth.background.bean.RoleMenuKey;
@Repository
public interface RoleMenuMapper {
    int deleteByPrimaryKey(RoleMenuKey key);

    int insert(RoleMenu record);

    int insertSelective(RoleMenu record);

    RoleMenu selectByPrimaryKey(RoleMenuKey key);

    int updateByPrimaryKeySelective(RoleMenu record);

    int updateByPrimaryKey(RoleMenu record);
}