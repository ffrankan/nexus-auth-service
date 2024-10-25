package com.github.frank.auth.dto;

/**
 * @author Frank An
 */
public record LoginResponse(String token, UserInfo userInfo) {

}
