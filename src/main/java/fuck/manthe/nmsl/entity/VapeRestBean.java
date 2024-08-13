package fuck.manthe.nmsl.entity;

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
}
