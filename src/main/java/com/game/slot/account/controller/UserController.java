package com.game.slot.account.controller;

import com.game.slot.account.controller.model.CreateUserRequestModel;
import com.game.slot.account.controller.model.CreateUserResponseModel;
import com.game.slot.account.service.UserService;
import com.game.slot.account.shared.dto.UserDto;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Environment environment;

    public UserController(UserService userService, ModelMapper modelMapper, Environment environment) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.environment = environment;
    }

    @GetMapping("/status")
    public ResponseEntity status() {
        return ResponseEntity.status(HttpStatus.OK).body("Working on port # " +  environment.getProperty("local.server.port"));
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userRequestDto) {
        UserDto createdUser = userService.createUser(modelMapper.map(userRequestDto, UserDto.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(createdUser, CreateUserResponseModel.class));
    }
}
