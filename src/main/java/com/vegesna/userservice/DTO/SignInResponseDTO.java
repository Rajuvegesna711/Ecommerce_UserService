package com.vegesna.userservice.DTO;

import com.vegesna.userservice.Models.Token;
import com.vegesna.userservice.Models.User;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
public class SignInResponseDTO {
    Timestamp expiry;
    String tokenValue;
    String status;

    public static SignInResponseDTO toDTO(Token token){
        return SignInResponseDTO.builder().expiry(token.getExpiry()).tokenValue(token.getValue()).status("SUCCESS").build();
    }

    public static SignInResponseDTO toDTO(String string){
        return SignInResponseDTO.builder().status("Failed").build();
    }
}
