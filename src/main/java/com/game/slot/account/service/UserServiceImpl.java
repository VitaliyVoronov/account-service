package com.game.slot.account.service;

import com.game.slot.account.domain.UserEntity;
import com.game.slot.account.repository.UserRepository;
import com.game.slot.account.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, 
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserPublicId(UUID.randomUUID().toString());
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        user.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        UserEntity createdUser = userRepository.save(user);
        return modelMapper.map(createdUser, UserDto.class);
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserByEmail(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.getEmail(), user.getEncryptedPassword(), true, true, 
                true, true,new ArrayList<>());
    }
}
