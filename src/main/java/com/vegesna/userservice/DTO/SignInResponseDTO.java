package com.vegesna.userservice.DTO;

import com.vegesna.userservice.Models.Token;
import com.vegesna.userservice.Models.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignInResponseDTO {
    String tokenValue;
    String status;

    public static SignInResponseDTO toDTO(Token token){
        return SignInResponseDTO.builder().tokenValue(token.getValue()).status("SUCCESS").build();
    }

    public static SignInResponseDTO toDTO(String string){
        return SignInResponseDTO.builder().status("Failed").build();
    }
}
