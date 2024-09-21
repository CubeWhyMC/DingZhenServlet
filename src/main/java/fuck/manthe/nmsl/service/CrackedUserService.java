package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.CrackedUser;

import java.util.List;

public interface CrackedUserService {
    boolean isValid(String username, String password);

    boolean addUser(CrackedUser user);

    void removeUser(String username);

    void removeUser(CrackedUser user);

    boolean renewUser(String username, int day);

    boolean hasExpired(String username);

    boolean resetPassword(String username, String newPassword);

    List<CrackedUser> list();

    long count();

    void removeExpired();

    CrackedUser findByUsername(String username);
}
