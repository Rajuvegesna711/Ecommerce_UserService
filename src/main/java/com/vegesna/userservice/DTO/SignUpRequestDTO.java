package com.vegesna.userservice.DTO;

import lombok.Data;


@Data
public class SignUpRequestDTO {
    private String fname;
    private String lname;
    private String email;
    private String password;
}
