package com.ideacollab.service;

import com.ideacollab.dto.UserDto;
import com.ideacollab.exception.ResourceNotFoundException;
import com.ideacollab.model.User;
import com.ideacollab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto getCurrentUser() {
        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        return convertToDto(userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public UserDto updateUserProfile(UserDto userDto) {
        User currentUser = getCurrentUserEntity();
        currentUser.setEmail(userDto.getEmail());
        return convertToDto(userRepository.save(currentUser));
    }

    private User getCurrentUserEntity() {
        String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        return dto;
    }
}