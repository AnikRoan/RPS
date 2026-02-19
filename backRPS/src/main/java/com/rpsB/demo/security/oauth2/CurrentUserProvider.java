package com.rpsB.demo.security.oauth2;

import com.rpsB.demo.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CurrentUserProvider {

    public Long getAuthUserPrincipalId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
        return ((UserPrincipal) Objects.requireNonNull(authentication.getPrincipal())).getId();
    }
}
