package fuck.manthe.nmsl.entity;

public enum ErrorCode {
    SERVER, // 服务器设置
    ACCOUNT, // 账户
    GLOBAL_COLD_DOWN, // 公共冷却
    PRE_ACCOUNT_COLD_DOWN, // 每个Vape账户的冷却
    QUEUE; // 排队

    public String formatError(String message) {
        return this.name().substring(0, 1).toUpperCase() + "-" + message;
    }
}
