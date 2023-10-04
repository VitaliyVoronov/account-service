package com.game.slot.account.service;

import com.game.slot.account.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto createUser(UserDto userDto);
    UserDto getUserDetailsByEmail(String email);
    
}
