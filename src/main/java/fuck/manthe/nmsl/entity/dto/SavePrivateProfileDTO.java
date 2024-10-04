package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SavePrivateProfileDTO {
    private List<String> deletedProfiles;
    private List<UpdatePrivateProfileDTO> updatedProfiles;
}
