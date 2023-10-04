package com.game.slot.account.repository;

import com.game.slot.account.domain.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    
    UserEntity findUserByEmail(String email);
}
