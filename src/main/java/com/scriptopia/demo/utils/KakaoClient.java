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
public class KakaoClient {

    private final RestTemplate restTemplate;
    private final OAuthProperties props;
    private final ObjectMapper objectMapper;

    public OAuthUserInfo getUserInfo(String code) {
        // 액세스 토큰 발급
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", props.getKakao().getClientId());
        params.add("client_secret", props.getKakao().getClientSecret());
        params.add("redirect_uri", props.getKakao().getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> tokenRes = restTemplate.postForEntity(tokenUrl, request, Map.class);

        String accessToken = (String) tokenRes.getBody().get("access_token");

        // 2. 사용자 정보 조회
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userReq = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userRes = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                userReq,
                Map.class
        );

        Map<String, Object> body = userRes.getBody();
        String id = String.valueOf(body.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");

        return OAuthUserInfo.builder()
                .id(id)
                .email(email)
                .name(nickname)
                .profileImage(profileImage)
                .provider("KAKAO")
                .build();
    }
}

