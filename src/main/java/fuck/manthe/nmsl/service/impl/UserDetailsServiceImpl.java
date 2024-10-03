package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.Admin;
import fuck.manthe.nmsl.entity.UserDetailsImpl;
import fuck.manthe.nmsl.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminService.findByUsername(username);
        if (admin == null) return null;
        return UserDetailsImpl.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .role(admin.getRole())
                .build();
    }
}
