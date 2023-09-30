package com.game.slot.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
    
    @GetMapping("/status")
    public HttpStatus status() {
        return HttpStatus.OK;
    }
}
