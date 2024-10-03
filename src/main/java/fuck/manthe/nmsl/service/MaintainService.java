package fuck.manthe.nmsl.service;

public interface MaintainService {

    boolean isMaintaining();

    void setMaintaining(boolean maintaining);

    long getStartTime();

    int calculateDuration();
}
