package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserPiaItem;
import com.scriptopia.demo.domain.UserSetting;
import com.scriptopia.demo.dto.items.ItemDTO;
import com.scriptopia.demo.dto.users.PiaItemDTO;
import com.scriptopia.demo.dto.users.UserAssetsResponse;
import com.scriptopia.demo.dto.users.UserSettingsDTO;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.mapper.ItemMapper;
import com.scriptopia.demo.repository.UserPiaItemRepository;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.repository.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserSettingRepository userSettingRepository;
    private final UserRepository userRepository;
    private final UserPiaItemRepository userPiaItemRepository;
    private final ItemMapper itemMapper;

    @Transactional
    public List<ItemDTO> getGameItems(String userId){

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND)
        );

        return itemMapper.mapUser(user);
    }


    @Transactional
    public UserSettingsDTO getUserSettings(String userId){

        UserSetting userSetting = userSettingRepository.findByUserId(Long.valueOf(userId)).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_SETTING_NOT_FOUND)
        );

        return UserSettingsDTO.builder()
                .theme(userSetting.getTheme())
                .fontSize(userSetting.getFontSize())
                .font(userSetting.getFontType())
                .lineHeight(userSetting.getLineHeight())
                .wordSpacing(userSetting.getWordSpacing())
                .build();

    }

    @Transactional
    public void updateUserSettings(String userId, UserSettingsDTO request){
        UserSetting userSetting = userSettingRepository.findByUserId(Long.valueOf(userId)).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_SETTING_NOT_FOUND)
        );

        userSetting.setTheme(request.getTheme());
        userSetting.setFontSize(request.getFontSize());
        userSetting.setFontType(request.getFont());
        userSetting.setLineHeight(request.getLineHeight());
        userSetting.setWordSpacing(request.getWordSpacing());
        userSetting.setUpdatedAt(LocalDateTime.now());


    }

    @Transactional
    public UserAssetsResponse getUserAssets(String userId){
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND)
        );

        return UserAssetsResponse.builder()
                .pia(user.getPia())
                .build();

    }

    @Transactional
    public List<PiaItemDTO>  getPiaItems(String userId){
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND)
        );
        List<PiaItemDTO> piaItems = new ArrayList<>();
        for (UserPiaItem userPiaItem : userPiaItemRepository.findByUserId(Long.valueOf(userId))) {
            piaItems.add(
                    PiaItemDTO.builder()
                            .name(userPiaItem.getPiaItem().getName())
                            .quantity(userPiaItem.getQuantity())
                            .build()
            );
        }

        return piaItems;
    }



}
