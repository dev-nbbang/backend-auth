package com.dev.nbbang.auth.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommonResponse {
    private boolean status;
    private String message;

    public static CommonResponse create(boolean status, String message) {
        return CommonResponse.builder()
                .status(status)
                .message(message)
                .build();
    }
}
