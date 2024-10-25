package com.github.frank.auth.dto;

import com.github.frank.system.entity.Role;
import com.github.frank.system.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Frank An
 */
public record UserInfo(Long id,
                       String username,
                       String email,
                       Set<String> roles) {

    public static UserInfo fromUser(User user) {
        return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()));
    }
}
