package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.RedeemCode;

public interface RedeemService {
    RedeemCode infoOrNull(String code);
    void addCode(RedeemCode code);

    boolean removeCode(String code);
}
