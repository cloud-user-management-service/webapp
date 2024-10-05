package com.myweb.webapp.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.myweb.webapp.entity.User;

import lombok.Data;

import java.util.Collection;

@Data
public class CustomUserDetails implements UserDetails {
    private User user;  // save the user object from the database 
    private String password;

    public CustomUserDetails(User user, String password) {
        this.user = user;
        this.password = password;
    }

  	// Returns the authorities granted to the user.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    @Override
    public String getPassword() {  // get the password of the user
        return password;
    }
    @Override
    public String getUsername() {  // get the username of the user
        return user.getEmail();
    }
    @Override
    public boolean isAccountNonExpired() {   // check if the account is not expired
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {   // check if the account is not locked
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {  // check if the credentials are not expired
        return true;
    }


}
