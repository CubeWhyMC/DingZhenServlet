package fuck.manthe.nmsl.service;

public interface AnalysisService {
    void launchInvoked(String username);
    void authRequested(String username);
    void userRegistered();

    Integer getTodayLaunch();
    Integer getTotalLaunch();
    Integer getTotalLaunch(String username);
}
