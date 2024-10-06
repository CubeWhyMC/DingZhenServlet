package fuck.manthe.nmsl.util;

import okhttp3.Dns;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class EnhancedDns implements Dns {
    @Value("${share.issuer.ip}")
    String issuerIp;

    @NotNull
    @Override
    public List<InetAddress> lookup(@NotNull String host) throws UnknownHostException {
        if ((host.equals("vape.gg") || host.equals("www.vape.gg")) && !issuerIp.equals("auto")) {
            return List.of(InetAddress.getByAddress(host, InetAddress.getByName(issuerIp).getAddress()));
        }
        return SYSTEM.lookup(host); // use default
    }
}
