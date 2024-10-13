package fuck.manthe.nmsl.service;

public interface MigrateService {
    /**
     * Migrate data from a legacy instance
     *
     * @param address       API address
     * @param adminPassword access password (legacy)
     */
    void migrateLegacy(String address, String adminPassword);
}
