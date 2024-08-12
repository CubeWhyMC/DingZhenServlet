package fuck.manthe.nmsl.service.impl;

import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.repository.RedeemRepository;
import fuck.manthe.nmsl.service.RedeemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RedeemServiceImpl implements RedeemService {
    @Autowired
    RedeemRepository redeemRepository;

    @Override
    @Transactional
    public RedeemCode infoOrNull(String codeString) {
        Optional<RedeemCode> redeemCode = redeemRepository.findByCode(codeString);
        return redeemCode.orElse(null);
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

    @Override
    public List<RedeemCode> list() {
        return redeemRepository.findAll();
    }
}
