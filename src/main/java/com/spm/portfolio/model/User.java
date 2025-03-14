package com.spm.portfolio.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Table("users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    private String userId;

    private String userName;
    private String userEmail;

    private String phoneNumber;
    private String password;
    private boolean active;
    private LocalDateTime createdAt;

    @Transient  // This field will NOT be stored in the database
    private boolean isNew;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER"); // Simple authority
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
    @Override
    public String getPassword()
    {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }


}