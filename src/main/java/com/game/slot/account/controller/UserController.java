package com.game.slot.account.controller;

import com.game.slot.account.controller.model.CreateUserRequestModel;
import com.game.slot.account.controller.model.CreateUserResponseModel;
import com.game.slot.account.service.UserService;
import com.game.slot.account.shared.dto.UserDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/user")
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userRequestDto) {
        UserDto createdUser = userService.createUser(modelMapper.map(userRequestDto, UserDto.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdUser, CreateUserResponseModel.class));
    }
}
