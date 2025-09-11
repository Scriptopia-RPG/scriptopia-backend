package com.scriptopia.demo.config;


public class SecurityWhitelist {
    public static final String[] AUTH_WHITELIST = {
            "/auth/logout",
            "/auth/login",
            "/auth/register",
            "/auth/email/**",
            "/auth/password/reset/**",


    };

    public static final String[] PUBLIC_GETS = {
            "/trades"
    };
}
