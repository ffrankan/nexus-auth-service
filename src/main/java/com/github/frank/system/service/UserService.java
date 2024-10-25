package com.github.frank.system.service;

import com.github.frank.common.exception.BusinessException;
import com.github.frank.common.exception.ErrorCode;
import com.github.frank.system.dto.UserDTO;
import com.github.frank.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Frank An
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String name) {
        return userRepository.findByUsername(name)
                .map(user -> UserDTO.fromUser(user.getId(), user.getUsername(), user.getEmail()))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }


    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
