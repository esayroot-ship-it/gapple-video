package com.ap.apservice.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.model.Settings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeiliSearchConfig {
    
    @Value("${meilisearch.host:http://localhost:7700}")
    private String hostUrl;

    @Value("${meilisearch.api-key:masterKey}")
    private String apiKey;

    @Bean
    public Client meiliClient() {
        Client client = new Client(new Config(hostUrl, apiKey));
        
        // 使用 Settings 对象配置索引
        try {
            // 尝试创建索引并指定主键为 "id"
            try { client.createIndex("videos", "id"); } catch (Exception e) { /* 已存在则忽略 */ }
            try { client.createIndex("series", "id"); } catch (Exception e) { /* 已存在则忽略 */ }

            Settings settings = new Settings();
            settings.setSearchableAttributes(new String[]{"title", "titlePinyin", "description"});
            
            client.index("videos").updateSettings(settings);
            client.index("series").updateSettings(settings);
            System.out.println("Meilisearch indexes initialized.");
        } catch (Exception e) {
            System.err.println("Meilisearch init error: " + e.getMessage());
        }
        
        return client;
    }
}
