package com.christ.erp.services.common;

import com.christ.utility.lib.vault.VaultUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;

    @Bean
    public DatabaseConfig loadDetails() {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setUrl("jdbc:mysql://10.5.13.52:3306/ADM");
        databaseConfig.setUsername("Dev_Erp");
        databaseConfig.setPassword("Tut06!l$2019");

        /* databaseConfig.setUrl(VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG,"security.erp.admin.db.url").getValue());
         databaseConfig.setUsername(VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG,"security.erp.admin.db.username").getValue());
         databaseConfig.setPassword(VaultUtils.instance.get(VaultUtils.CONFIG_SECTION_APP_CONFIG,"security.erp.admin.db.password").getValue());*/
        return databaseConfig;
    }
}
