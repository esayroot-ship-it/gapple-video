package com.ap.apcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoSearchDTO implements Serializable {
    private String id; // Meilisearch 要求 ID 为 String
    private String title;
    private String description;
    private String coverUrl;
    private String titlePinyin;
}
