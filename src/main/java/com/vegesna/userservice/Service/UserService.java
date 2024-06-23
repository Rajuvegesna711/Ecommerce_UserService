package com.vegesna.userservice.Service;

import com.vegesna.userservice.DTO.SignInRequestDTO;
import com.vegesna.userservice.DTO.SignOutRequestDTO;
import com.vegesna.userservice.DTO.SignUpRequestDTO;
import com.vegesna.userservice.Models.Token;
import com.vegesna.userservice.Models.User;
import com.vegesna.userservice.Repo.TokenRepo;
import com.vegesna.userservice.Repo.UserRepo;
import com.vegesna.userservice.Utils.EncDec;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserService.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepo userRepo;
    private TokenRepo tokenRepo;
    private JavaMailSender mailSender;

    UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepo userRepo, TokenRepo tokenRepo, JavaMailSender mailSender){
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.mailSender = mailSender;
    }

    public User signUp(SignUpRequestDTO signUpRequest){
        User user = new User();
        user.setFname(signUpRequest.getFname());
        user.setLname(signUpRequest.getLname());
        user.setEmail(signUpRequest.getEmail());
        user.setIsVerified(false);
        user.setHashedPassword(bCryptPasswordEncoder.encode(signUpRequest.getPassword()));
        User savedUser = userRepo.save(user);
        new Thread(() -> {
            try {
//                String Email = EncDec.encrypt(savedUser.getEmail());
                String Email = savedUser.getEmail();
                String fromEmail = "prashanthreddyyo@gmail.com";
                this.sendVerificationEmail(fromEmail, savedUser.getEmail(), Email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return savedUser;
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
        token.setIsActive(true);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        return token;
    }

    public void signOut(SignOutRequestDTO signOutRequestDTO){
        Optional<Token> optionalToken = tokenRepo.findByValue(signOutRequestDTO.getValue());
        if(optionalToken.isEmpty()){
            throw new RuntimeException("........");
        }

        Token token = optionalToken.get();
        token.setIsActive(false);
        tokenRepo.save(token);
    }

    public User validateToken(String value){
        Optional<Token> optionalToken = tokenRepo.findByValueAndIsActiveAndExpiryGreaterThan(value,true,new Date());
        if(optionalToken.isEmpty()){
            throw new RuntimeException("Invalid token");
        }
        return optionalToken.get().getUser();

    }

    public void sendVerificationEmail(String from, String to, String encEmail) {
        String subject = "Account Verification";
        String verificationUrl = "http://localhost:8081/user/account/validate?encEmail=" + encEmail;
        String message = "<p>Dear User,</p>"
                + "<p>Please click the link below to verify your account:</p>"
                + "<a href=\"" + verificationUrl + "\">Verify your account</a>"
                + "<p>Thank you!</p>";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
//            e.printStackTrace();
                log.error("error in sendVerificationEMail is ",e);
        }
    }

    public Boolean accountValidate(String Email) throws Exception {
//        String email = EncDec.decrypt(Email);
        Optional<User> user = userRepo.findByEmail(Email);
        if(user.isPresent()){
            user.get().setIsVerified(true);
            userRepo.save(user.get());
            return true;
        }//fetch user entry from email and update status as verified

        return false;
    }
}
