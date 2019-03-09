package auth.background.repositories.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import auth.background.domain.bean.RoleMenu;
import auth.background.domain.bean.RoleMenuKey;
@Repository
public interface RoleMenuMapper {
    int deleteByPrimaryKey(RoleMenuKey key);

    int insert(RoleMenu record);

    int insertSelective(RoleMenu record);

    RoleMenu selectByPrimaryKey(RoleMenuKey key);

    int updateByPrimaryKeySelective(RoleMenu record);

    int updateByPrimaryKey(RoleMenu record);
    
    //获取角色下的菜单
    List<RoleMenu> GetRoleMenus(String roleid);
    //获取当前用户的菜单
    List<RoleMenu> GetUserRoleMenus(String userid);
    
    List<RoleMenu> GetAllRoleMenus();
    //删除角色下菜单
    void RemoveRowMenus(String roleid);
    //批量添加角色下菜单
    void BatchAddRowMenus(List<RoleMenuKey> list);
}