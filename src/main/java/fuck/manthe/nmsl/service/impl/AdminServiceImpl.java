package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.Admin;
import fuck.manthe.nmsl.repository.AdminRepository;
import fuck.manthe.nmsl.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    AdminRepository adminRepository;

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }
}
