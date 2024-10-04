package fuck.manthe.nmsl.entity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class OnlineConfigDTO {
    @Builder.Default
    private boolean autoLogin = true;

    @Builder.Default
    private Map<String, String> friendStates = new HashMap<>();

    private boolean partyShowTarget;

    private List<String> pingKeybind;

    private boolean shareInventory;

    private List<String> showInventoryKeybind;

    private boolean showSelf;

    private boolean showServer;

    private boolean showUsername;

    private double inventorySwitchMode;
}
