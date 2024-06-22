package com.vegesna.userservice.DTO;

import com.vegesna.userservice.Models.Roles;
import com.vegesna.userservice.Models.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class UserDTO {
    private String fname;
    private String lname;
    private String email;
    private List<Roles> roles;

    public static UserDTO toDTO(User user){
        return UserDTO.builder().fname(user.getFname()).lname(user.getLname()).
                email(user.getEmail()).roles(user.getRoles()).build();
    }

}
