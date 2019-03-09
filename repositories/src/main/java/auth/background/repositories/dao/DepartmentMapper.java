package auth.background.repositories.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import auth.background.domain.bean.Department;

@Repository
public interface DepartmentMapper {
    int deleteByPrimaryKey(String id);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKeyWithBLOBs(Department record);

    int updateByPrimaryKey(Department record);
    
    List<Department> GetAllList();
    int deleteBatchByPrimaryKey(List<String> list);
}