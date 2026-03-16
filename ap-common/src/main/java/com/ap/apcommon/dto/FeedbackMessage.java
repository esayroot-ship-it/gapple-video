package com.ap.apcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackMessage implements Serializable {
    private Long userId;
    private String content;
    private String type;
    private LocalDateTime createdAt;
}
