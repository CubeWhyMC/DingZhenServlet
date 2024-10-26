package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;
import fuck.manthe.nmsl.entity.vo.GatewayAuthorizeVO;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface GatewayService {
    /**
     * Check mode is gateway
     */
    boolean isPureGateway();

    boolean isGatewayEnabled();

    boolean canUseGateway();

    Gateway addGateway(Gateway gateway);

    boolean removeGateway(String id);

    Gateway getOne();

    VapeAuthorizeDTO use(Gateway gateway) throws Exception;

    void markColdDown(Gateway gateway, long expireAt);

    boolean isColdDown(Gateway gateway);

    long getColdDown(Gateway gateway);

    List<Gateway> list();

    Gateway findGatewayById(String id);

    Gateway saveGateway(Gateway gateway);

    /**
     * Encrypt a VapeAuthorize info to GatewayAuthorize info
     *
     * @param dto response from the issuer
     */
    GatewayAuthorizeVO processEncrypt(VapeAuthorizeDTO dto) throws Exception;

    /**
     * Decrypt a response to VapeAuthorize info
     *
     * @param gateway the gateway
     * @param vo      response from gateway
     */
    VapeAuthorizeDTO processDecrypt(Gateway gateway, GatewayAuthorizeVO vo) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    /**
     * Verify the encrypt secret
     *
     * @param providedSecret secret key from headers
     * @return is valid
     */
    boolean assertSecret(String providedSecret);

    /**
     * Send a heartbeat packet to gateway server
     *
     * @param gateway gateway
     */
    boolean heartbeat(Gateway gateway);

    boolean isAvailable(Gateway gateway);

    /**
     * Toggle gateway state
     *
     * @param gateway Origin gateway entity
     * @param state   new state
     * @return modified Gateway entity
     */
    Gateway toggle(Gateway gateway, boolean state);
}
