package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SavePrivateProfileVO {
    private List<String> deletedProfiles;
    private List<UpdatePrivateProfileVO> updatedProfiles;
}
