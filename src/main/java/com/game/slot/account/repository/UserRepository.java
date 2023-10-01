package com.game.slot.account.repository;

import com.game.slot.account.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
