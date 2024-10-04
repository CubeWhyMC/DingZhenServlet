package fuck.manthe.nmsl.entity;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class OnlineConfig implements BaseData {
    public static OnlineConfig DEFAULT = OnlineConfig.builder()
            .autoLogin(true)
            .friendStates(new HashMap<>())
            .inventorySwitchMode(0)
            .partyShowTarget(true)
            .pingKeybind(new ArrayList<>())
            .shareInventory(true)
            .showSelf(true)
            .showServer(true)
            .showUsername(true)
            .build();

    private boolean autoLogin;

    private Map<String, String> friendStates;
    private boolean partyShowTarget;
    private List<String> pingKeybind;
    private boolean shareInventory;

    private List<String> showInventoryKeybind;

    private boolean showSelf;
    private boolean showServer;
    private boolean showUsername;

    private double inventorySwitchMode;
}
