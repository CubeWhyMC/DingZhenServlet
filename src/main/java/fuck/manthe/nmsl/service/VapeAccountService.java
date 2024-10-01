package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.VapeAccount;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface VapeAccountService {
    VapeAccount getOne();

    boolean isColdDown(VapeAccount account);

    void markColdDown(VapeAccount account);

    void resetColdDown(VapeAccount account);


    boolean addAccount(VapeAccount account);

    boolean removeAccount(String account);

    boolean updatePassword(String username, String newPassword) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    boolean updateHwid(String username, String newHwid);

    List<VapeAccount> listAccounts();

    long getColdDown(VapeAccount account);

    Long nextAvailable();

    /**
     * Fetch token from the real auth server
     *
     * @param account vape account cert
     */
    VapeAuthorizeDTO doAuth(VapeAccount account);

    VapeAccount findByUsername(String username);
}
