package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResultContentVO {
    private int profileId;
    private OwnerVO owner;
    private String name;
    private int version;
    @Builder.Default
    private int likes = 0;
    @Builder.Default
    private int dislikes = 0;
    private List<String> tags;
    private String shareCode;
}
