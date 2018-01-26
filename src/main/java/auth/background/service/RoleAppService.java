package auth.background.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import auth.background.dao.RoleMapper;
import auth.background.dto.RoleDto;
import auth.background.util.BeanMapper;

@Service
public class RoleAppService {
    @Resource
    private RoleMapper roleDao;
	@Resource
    private BeanMapper dzmapper;
	@Resource
    private CacheService<RoleDto> cacheService;
}
