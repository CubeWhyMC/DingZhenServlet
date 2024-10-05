package fuck.manthe.nmsl.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnlineConfigVO {
    @Builder.Default
    boolean autoLogin = true;

    @Builder.Default
    Map<String, String> friendStates = new HashMap<>();

    boolean partyShowTarget;

    List<String> pingKeybind;

    boolean shareInventory;

    List<String> showInventoryKeybind;

    boolean showSelf;

    boolean showServer;

    boolean showUsername;

    double inventorySwitchMode;
}
