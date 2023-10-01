package com.game.slot.account.shared.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String userPublicId;
    private String encryptedPassword;
    
}
