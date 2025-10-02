package com.scriptopia.demo.config;


public class SecurityWhitelist {
    public static final String[] AUTH_WHITELIST = {
            "/",

            "/error",

            "/auth/logout",
            "/auth/login",
            "/auth/register",
            "/auth/email/**",
            "/auth/password/reset/**",

            "/oauth/**",

            "/v3/api-docs/**",
            "/swagger-ui/**",

            "/shops/pia/items"




    };

    public static final String[] PUBLIC_GETS = {
            "/trades",
            "/shared-games/**"
    };
}
