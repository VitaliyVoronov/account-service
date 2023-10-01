package com.game.slot.account.controller.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserResponseModel {

    private String firstName;
    private String lastName;
    private String email;
    private String userPublicId;
    
}
