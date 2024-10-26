package fuck.manthe.nmsl.service;

public interface AnalysisService {
    void launchInvoked(String username);

    void authRequested(String username);

    void userRedeemed();

    int getTodayLaunch();

    int getTotalLaunch();

    int getTotalLaunch(String username);

    int getTodayRedeem();

    void reset();

    long getLastLaunch(String username);

    long getGatewayHeartbeat();

    long gatewayHeartbeat();
}
