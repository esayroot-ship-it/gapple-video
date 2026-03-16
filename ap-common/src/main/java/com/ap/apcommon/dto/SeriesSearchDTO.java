package com.ap.apcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeriesSearchDTO implements Serializable {
    private String id;
    private String title;
    private String description;
    private String coverUrl;
    private String titlePinyin;
}
