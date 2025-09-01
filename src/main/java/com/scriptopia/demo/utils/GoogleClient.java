package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Provider;
import com.scriptopia.demo.dto.oauth.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleClient {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuthUserInfo getUserInfo(String code) {

        String tokenUrl = "https://oauth2.googleapis.com/token";

        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResponse =
                restTemplate.postForEntity(tokenUrl, params, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> userInfoResponse =
                restTemplate.exchange(
                        "https://www.googleapis.com/oauth2/v2/userinfo",
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

        Map<String, Object> userInfo = userInfoResponse.getBody();


        return OAuthUserInfo.builder()
                .id((String) userInfo.get("id"))
                .email((String) userInfo.get("email"))
                .name((String) userInfo.get("name"))
                .profileImage((String) userInfo.get("picture"))
                .provider(Provider.GOOGLE.toString())
                .build();
    }
}
