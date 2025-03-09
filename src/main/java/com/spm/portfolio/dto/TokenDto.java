package com.spm.portfolio.dto;

import lombok.ToString;


@ToString
public class TokenDto {

    private String access_token;
    private String userId;

    public String getAccess_token() {
        return access_token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
