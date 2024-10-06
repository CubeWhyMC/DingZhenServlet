package fuck.manthe.nmsl.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchCheatProfileDTO {
    private String mode; // 排序模式
    private int page; // 页数,从1开始数
    private String search; // 搜索内容,可以是名称或者UUID
    private List<String> tags; // 标签
}
