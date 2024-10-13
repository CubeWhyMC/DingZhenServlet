package fuck.manthe.nmsl.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import fuck.manthe.nmsl.entity.RedeemCode;
import fuck.manthe.nmsl.entity.RestBean;
import fuck.manthe.nmsl.entity.User;
import fuck.manthe.nmsl.entity.dto.migrate.MigrateCrackedUserDTO;
import fuck.manthe.nmsl.entity.dto.migrate.MigrateRedeemCodeDTO;
import fuck.manthe.nmsl.service.MigrateService;
import fuck.manthe.nmsl.service.RedeemService;
import fuck.manthe.nmsl.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Log4j2
@Service
public class MigrateServiceImpl implements MigrateService {
    @Resource
    OkHttpClient okHttpClient;

    @Resource
    RedeemService redeemService;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    UserService userService;

    @Override
    public void migrateLegacy(String address, String adminPassword) {
        okHttpClient.newCall(new Request.Builder()
                .get()
                .url(String.format("%s/admin/user/list", address))
                .header("X-Admin-Password", adminPassword)
                .header("Content-Type", "application/json")
                .build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    log.info("Migrating users...");
                    List<MigrateCrackedUserDTO> users = JSONObject.parseObject(response.body().string(), new TypeReference<>() {
                    });
                    for (MigrateCrackedUserDTO user : users) {
                        if (userService.existByUsername(user.getUsername())) {
                            log.warn("Skipped import user {} (exist)", user.getUsername());
                            continue;
                        }
                        userService.save(User.builder()
                                .role("USER") // legacy servlet doesn't have that
                                .username(user.getUsername())
                                .expire(user.getExpire())
                                .password(passwordEncoder.encode(user.getUsername())) // the API doesn't provide users' passwords
                                .build());
                        log.info("Migrated user {} successfully", user.getUsername());
                    }
                    log.info("All users were migrated successfully.");
                } else {
                    log.error("Failed to receive users, response body is null");
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("Failed to request to users api, is admin password right?");
            }
        });

        okHttpClient.newCall(new Request.Builder()
                .get()
                .url(String.format("%s/admin/redeem/list", address))
                .header("X-Admin-Password", adminPassword)
                .header("Content-Type", "application/json")
                .build()).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.body() != null) {
                    log.info("Migrating redeem codes...");
                    RestBean<JSONArray> obj = JSONObject.parseObject(response.body().string(), new TypeReference<>() {
                    });
                    List<MigrateRedeemCodeDTO> codes = obj.data().toList(MigrateRedeemCodeDTO.class);
                    for (MigrateRedeemCodeDTO code : codes) {
                        redeemService.addCode(RedeemCode.builder()
                                .code(code.getCode())
                                .redeemer(userService.findByUsername(code.getRedeemer()))
                                .reseller(code.getReseller())
                                .available(code.isAvailable())
                                .date(code.getDate())
                                .build());
                    }
                    log.info("All redeem codes were migrated successfully.");
                } else {
                    log.error("Failed to receive redeem codes, response body is null");
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("Failed to request to redeem api, is admin password right?");
                log.error(e.getMessage(), e);
            }
        });
    }
}
