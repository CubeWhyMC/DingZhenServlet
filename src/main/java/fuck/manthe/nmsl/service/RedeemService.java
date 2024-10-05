package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.User;

import java.util.List;

public interface RedeemService {
    RedeemCode infoOrNull(String code);

    void addCode(RedeemCode code);

    boolean useCode(String code, User user);

    boolean removeCode(String code);

    List<RedeemCode> list();

    List<RedeemCode> listAvailable();

    List<RedeemCode> listSold();

    void deleteAllByRedeemerUsername(String username);

    void deleteAllByRedeemer(User user);
}
