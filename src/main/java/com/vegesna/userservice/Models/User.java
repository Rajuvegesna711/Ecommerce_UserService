package com.vegesna.userservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import java.util.List;


@Entity
@Data
public class User extends BaseModel{
    private String fname;
    private String lname;
    private String email;
    private String hashedPassword;

    @ManyToMany
    private List<Roles> roles;
    private Boolean isVerified;






}
