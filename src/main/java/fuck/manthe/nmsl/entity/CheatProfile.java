package fuck.manthe.nmsl.entity;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class CheatProfile {
    @Id
    private String id; // aka profileId

    @DBRef
    private User owner;

    private String uuid; // view for everyone
    private String name; // profile name
    private String vapeVersion;

    private JSONObject data;

    private String created; // created time, e.g. 2024-08-12T02:43:00.692+00:00
    private String lastUpdated; // modify time, e.g. 2024-08-13T09:51:45.259+00:00

    private Object metadata = null; // idk
}
