package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.Admin;

public interface AdminService {
    Admin findByUsername(String username);
}
