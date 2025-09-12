package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserSetting;
import com.scriptopia.demo.dto.users.GetSettingsResponse;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserSettingRepository userSettingRepository;

    @Transactional
    public GetSettingsResponse getSettings(String userId){

        UserSetting userSetting = userSettingRepository.findByUserId(Long.valueOf(userId));

        return GetSettingsResponse.builder()
                .theme(userSetting.getTheme())
                .fondSize(userSetting.getFontSize())
                .font(userSetting.getFontType())
                .lineHeight(userSetting.getLineHeight())
                .wordSpacing(userSetting.getWordSpacing())
                .build();

    }

}
