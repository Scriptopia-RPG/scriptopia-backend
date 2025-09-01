package com.scriptopia.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.config.OAuthProperties;
import com.scriptopia.demo.dto.oauth.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NaverClient {

    private final RestTemplate restTemplate;
    private final OAuthProperties props;
    private final ObjectMapper objectMapper;

    public OAuthUserInfo getUserInfo(String code) {
        // 액세스 토큰 발급
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", props.getNaver().getClientId());
        params.add("client_secret", props.getNaver().getClientSecret());
        params.add("redirect_uri", props.getNaver().getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> tokenRes = restTemplate.postForEntity(tokenUrl, request, Map.class);

        String accessToken = (String) tokenRes.getBody().get("access_token");

        // 사용자 정보 조회
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userReq = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userRes = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                userReq,
                Map.class
        );

        Map<String, Object> body = userRes.getBody();
        Map<String, Object> resp = (Map<String, Object>) body.get("response");

        String id = (String) resp.get("id");
        String email = (String) resp.get("email");
        String name = (String) resp.get("name");
        String profileImage = (String) resp.get("profile_image");

        return OAuthUserInfo.builder()
                .id(id)
                .email(email)
                .name(name)
                .profileImage(profileImage)
                .provider("NAVER")
                .build();
    }
}
