package com.game.slot.account.service;

import com.game.slot.account.domain.User;
import com.game.slot.account.repository.UserRepository;
import com.game.slot.account.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserPublicId(UUID.randomUUID().toString());
        User user = modelMapper.map(userDto, User.class);
        user.setEncryptedPassword("test encrypted pass");
        User createdUser = userRepository.save(user);
        return modelMapper.map(createdUser, UserDto.class);
    }
}
