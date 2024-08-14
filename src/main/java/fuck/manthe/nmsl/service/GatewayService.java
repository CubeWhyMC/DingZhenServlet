package fuck.manthe.nmsl.service;

import fuck.manthe.nmsl.entity.Gateway;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.GatewayDTO;
import fuck.manthe.nmsl.entity.dto.VapeAuthorizeDTO;

import java.io.IOException;
import java.util.List;

public interface GatewayService {
    /**
     * Check mode is gateway
     */
    boolean isPureGateway();

    void addGateway(Gateway gateway);

    boolean removeGateway(long id);

    Gateway getOne();

    VapeAuthorizeDTO use(Gateway gateway) throws IOException;

    void markColdDown(Gateway gateway, long expireAt);

    boolean isColdDown(Gateway gateway);

    long getColdDown(Gateway gateway);

    long getRemoteColdDown(Gateway gateway) throws IOException;

    List<Gateway> list();
}
