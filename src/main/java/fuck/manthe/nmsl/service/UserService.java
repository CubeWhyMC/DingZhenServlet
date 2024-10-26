package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.User;

import java.util.List;

public interface UserService {
    boolean isValid(String username, String password);

    User register(String username, String password, long expireAt);

    User register(String username, String password, long expireAt, boolean isAdmin);

    User createDefaultAdmin(String password);

    void removeUser(String username);

    void removeUser(User user);

    boolean renew(String username, int days);

    void renew(User user, int days);

    void renewAll(int days);

    boolean hasExpired(String username);

    boolean resetPassword(String username, String newPassword);

    List<User> list();

    long count();

    void removeExpired();

    User findByUsername(String username);

    User save(User user);

    boolean existByUsername(String username);
}
