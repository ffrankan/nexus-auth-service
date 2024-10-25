package com.github.frank.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.frank.system.entity.Role;
import com.github.frank.system.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Frank An
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UserDTO(Long id,
                      String username,
                      String email,
                      Set<String> roles,
                      Boolean enabled) {

    public static UserDTO fromUser(Long id, String username, String email) {
        return new UserDTO(id, username, email, Set.of(), null);
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                user.isEnabled()
        );
    }

}
