package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.RedeemCode;

import java.util.List;

public interface RedeemService {
    RedeemCode infoOrNull(String code);
    void addCode(RedeemCode code);

    boolean removeCode(String code);

    List<RedeemCode> list();
}
