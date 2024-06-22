package com.vegesna.userservice.Repo;

import com.vegesna.userservice.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepo extends JpaRepository<Token, Long> {
    Optional<Token> findByValue(String value);
    Token save(Token token);


    Optional<Token> findByValueAndActiveAndExpiryGreaterThan(String value, Boolean isValid, Date CurrentDate);

}
