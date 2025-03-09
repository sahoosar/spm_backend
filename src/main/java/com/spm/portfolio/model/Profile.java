package com.spm.portfolio.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("profiles")
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String userId;  // Foreign Key from `users`

   /* public Profile(String firstName, String lastName, String address, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.userId = userId;
    }*/

    // Getters and Setters
}

