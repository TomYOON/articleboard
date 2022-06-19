package com.example.articleboard.util;

import com.example.articleboard.domain.Role;
import org.springframework.security.core.Authentication;

public class AuthUtils {
    public static boolean hasAdminRole(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Role.ADMIN.toString()));
    }
}
