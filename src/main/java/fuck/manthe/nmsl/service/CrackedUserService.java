package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.CrackedUser;

public interface CrackedUserService {
    boolean isValid(String username, String password);
    void addUser(CrackedUser user);
    void removeUser(CrackedUser user);
}
