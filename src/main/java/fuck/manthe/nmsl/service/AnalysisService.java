package fuck.manthe.nmsl.service;

public interface AnalysisService {
    void launchInvoked(String username);
    void authRequested(String username);
    void userRegistered();

    long getTodayLaunch();

    long getTotalLaunch();

    long getTotalLaunch(String username);

    long getTodayRegister();

    void reset();

    long getLastLaunch(String username);
}
