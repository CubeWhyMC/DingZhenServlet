package fuck.manthe.nmsl.service;

public interface MaintenanceService {

    boolean isMaintaining();

    void setMaintaining(boolean maintaining);

    long getStartTime();

    int calculateDuration();
}
