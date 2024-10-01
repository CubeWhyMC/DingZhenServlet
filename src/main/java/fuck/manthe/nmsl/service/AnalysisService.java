package fuck.manthe.nmsl.service;

public interface AnalysisService {
    void launchInvoked(String username);

    void authRequested(String username);

    void userRegistered();

    int getTodayLaunch();

    int getTotalLaunch();

    int getTotalLaunch(String username);

    int getTodayRegister();

    void reset();

    long getLastLaunch(String username);
}
