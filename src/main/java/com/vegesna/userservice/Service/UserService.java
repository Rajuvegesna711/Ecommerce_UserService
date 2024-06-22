package com.vegesna.userservice.Service;

import com.vegesna.userservice.DTO.SignInRequestDTO;
import com.vegesna.userservice.DTO.SignOutRequestDTO;
import com.vegesna.userservice.DTO.SignUpRequestDTO;
import com.vegesna.userservice.Models.Token;
import com.vegesna.userservice.Models.User;
import com.vegesna.userservice.Repo.TokenRepo;
import com.vegesna.userservice.Repo.UserRepo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepo userRepo;
    private TokenRepo tokenRepo;

    UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepo userRepo, TokenRepo tokenRepo){
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
    }

    public User signUp(SignUpRequestDTO signUpRequest){
        User user = new User();
        user.setFname(signUpRequest.getFname());
        user.setLname(signUpRequest.getLname());
        user.setEmail(signUpRequest.getEmail());
        user.setIsVerified(false);
        user.setHashedPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));

        return userRepo.save(user);
    }

    public Token signIn(SignInRequestDTO signInRequest){
        Optional<User> OptionalUser = userRepo.findByEmail(signInRequest.getEmail());
        if(OptionalUser.isEmpty()){
            throw new RuntimeException("User is not valid");
        }

        User user = OptionalUser.get();
        if(user.getIsVerified().equals(false)){
            throw new RuntimeException("User is not verified");
        }
        else if(!bCryptPasswordEncoder.matches(signInRequest.getPassword(), user.getHashedPassword())){
            throw new RuntimeException("Password is incorrect");
        }

        Token token = generateToken(user);
        return tokenRepo.save(token);


    }

    private Token generateToken(User user){
        Token token = new Token();

        // Current timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());
        // Convert to LocalDateTime
        LocalDateTime localDateTime = now.toLocalDateTime();
        // Add 30 days
        LocalDateTime futureDate = localDateTime.plusDays(30);

        token.setExpiry(Timestamp.valueOf(futureDate));
        token.setUser(user);
        token.setValid(true);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        return token;
    }

    public void signOut(SignOutRequestDTO signOutRequestDTO){
        Optional<Token> optionalToken = tokenRepo.findByValue(signOutRequestDTO.getValue());
        if(optionalToken.isEmpty()){
            throw new RuntimeException("");
        }

        Token token = optionalToken.get();
        token.setValid(false);
        tokenRepo.save(token);
    }

    public User validateToken(String value){
       Optional<Token> optionalToken = tokenRepo.findByValueAndValidAndExpiryGreaterThan(value,true,new Date());
        if(optionalToken.isEmpty()){
            throw new RuntimeException("Invalid token");
        }
        return optionalToken.get().getUser();

    }

}
