package fuck.manthe.nmsl.controller.admin;

import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.dto.MigrateLegacyDTO;
import fuck.manthe.nmsl.service.MigrateService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/server")
public class ServerController {

    @Resource
    MigrateService migrateService;

    /**
     * Migrate from a legacy DingZhenServlet instance
     */
    @PostMapping("migrate/legacy")
    public RestBean<String> migrateFromLegacy(@RequestBody MigrateLegacyDTO dto) {
        migrateService.migrateLegacy(dto.getAddress(), dto.getAdminPassword());
        return RestBean.success("Migrating...");
    }
}
