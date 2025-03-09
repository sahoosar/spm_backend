package com.spm.portfolio.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_roles")
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    private String userId;
    private Long roleId;

    /*public UserRole(String userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }*/

    // Getters and Setters
}
