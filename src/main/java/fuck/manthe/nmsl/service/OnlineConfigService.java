package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.User;

public interface OnlineConfigService {
    void cache(String token, String username);

    User findByToken(String token);
}
