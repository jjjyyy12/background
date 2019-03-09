package auth.background.repositories.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import auth.background.domain.bean.Menu;

@Repository
public interface MenuMapper {
    int deleteByPrimaryKey(String id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);
    
    List<Menu> GetAllList();
    int deleteBatchByPrimaryKey(List<String> list);
}