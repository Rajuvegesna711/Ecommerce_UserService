package com.vegesna.userservice.Controller;

import com.vegesna.userservice.DTO.*;
import com.vegesna.userservice.Models.Token;
import com.vegesna.userservice.Models.User;
import com.vegesna.userservice.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/signup")
    public UserDTO signUp(@RequestBody SignUpRequestDTO signUpRequest){
        User user = userService.signUp(signUpRequest);
        return UserDTO.toDTO(user);
    }

    @PostMapping("/signin")
    public SignInResponseDTO signIn(@RequestBody SignInRequestDTO signInRequest){
        Token token = userService.signIn(signInRequest);
        if(token!=null){
            return SignInResponseDTO.toDTO(token);
        }
        return SignInResponseDTO.toDTO("Failed");
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(@RequestBody SignOutRequestDTO signOutRequestDTO){
        try {
            userService.signOut(signOutRequestDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/validate/{token}")
    public UserDTO validateToken(@PathVariable String token) {
        User user = userService.validateToken(token);
        return UserDTO.toDTO(user);
    }

    public ResponseEntity<Void> isVerified(@PathVariable String email){
        return null;
    }

    @GetMapping("/account/validate")
    public ResponseEntity<?> validateUser(@RequestParam String encEmail) throws Exception {
        return ResponseEntity.ok(userService.accountValidate(encEmail));
    }


    public void profile(){

    }

    public void updateprofile(){

    }

    public void resetPassword(){

    }
}
