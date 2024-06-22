package com.vegesna.userservice.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import java.sql.Timestamp;


@Entity
@Data
public class Token extends BaseModel{
    private Timestamp expiry;
    private boolean active;
    private String value;
    @ManyToOne
    private User user;
}
