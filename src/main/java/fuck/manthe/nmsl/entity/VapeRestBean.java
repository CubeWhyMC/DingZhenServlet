package fuck.manthe.nmsl.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record VapeRestBean<T>(T data, String error, boolean successful) {
    @Contract("_ -> new")
    public static <T> @NotNull VapeRestBean<T> success(T data) {
        return new VapeRestBean<>(data, null, true);
    }

    @Contract(" -> new")
    public static <T> @NotNull VapeRestBean<T> success() {
        return success(null);
    }

    @Contract(" -> new")
    public static <T> @NotNull VapeRestBean<T> failedToFindAccount() {
        return failure("Failed to find account");
    }

    @Contract
    public static <T> @NotNull VapeRestBean<T> failure(String error) {
        return new VapeRestBean<>(null, error, false);
    }

    public String toJson() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
