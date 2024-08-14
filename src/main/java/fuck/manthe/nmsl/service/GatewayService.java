package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface GatewayService {
    /**
     * Check mode is gateway
     */
    boolean isPureGateway();
    boolean canUseGateway();

    void addGateway(Gateway gateway);

    boolean removeGateway(long id);

    Gateway getOne();

    VapeAuthorizeDTO use(Gateway gateway) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    void markColdDown(Gateway gateway, long expireAt);

    boolean isColdDown(Gateway gateway);

    long getColdDown(Gateway gateway);

    long getRemoteColdDown(Gateway gateway) throws IOException;

    List<Gateway> list();
}
