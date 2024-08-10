package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.repository.RedeemRepository;
import fuck.manthe.nmsl.service.RedeemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedeemServiceImpl implements RedeemService {
    @Autowired
    RedeemRepository redeemRepository;

    @Override
    public RedeemCode redeem(String codeString) {
        Optional<RedeemCode> redeemCode = redeemRepository.findByCode(codeString);
        if (redeemCode.isEmpty()) {
            return null;
        }
        redeemRepository.deleteByCode(codeString);
        return redeemCode.get();
    }

    @Override
    public void addCode(RedeemCode code) {
        redeemRepository.save(code);
    }

    @Override
    @Transactional
    public boolean removeCode(String code) {
        if (redeemRepository.findByCode(code).isEmpty()) return false;
        redeemRepository.deleteByCode(code);
        return true;
    }
}
