package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.CrackedUser;

public interface CrackedUserService {
    boolean isValid(String username, String password);
    boolean addUser(CrackedUser user);
    void removeUser(String username);
}
