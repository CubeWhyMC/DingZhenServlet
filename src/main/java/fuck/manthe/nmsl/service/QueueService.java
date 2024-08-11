package fuck.manthe.nmsl.service;

import java.util.List;

public interface QueueService {
    List<String> query();
    void quit(String username);
    boolean join(String username);
    boolean isInQueue(String username);
    boolean isNext(String username);
}
