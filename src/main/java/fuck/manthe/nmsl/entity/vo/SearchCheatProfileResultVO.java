package fuck.manthe.nmsl.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchCheatProfileResultVO {
    private List<SearchResultContentVO> content;
    private boolean last; // 是否为最后一页
    private int totalPages; // 总共的页数
    private int totalElements; // 查到的数量
    private int size; // 铺满一页的数量
    private int numberOfElements; // 和totalElements一样, 应该是向下兼容
}
