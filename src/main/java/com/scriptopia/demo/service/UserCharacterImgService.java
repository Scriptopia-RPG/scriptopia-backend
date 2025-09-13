package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserCharacterImg;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.UserCharacterImgRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCharacterImgService {
    private final UserCharacterImgRepository userCharacterImgRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> saveCharacterImg(Long userId, MultipartFile file) {
        if(file.isEmpty()) {
            throw new CustomException(ErrorCode.E_400_EMPTY_FILE);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        try {
            String tmpDir = System.getProperty("java.io.tmpdir");

            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String saveName = UUID.randomUUID() + ext;

            File savefile = new File(tmpDir, saveName);
            file.transferTo(savefile);

            UserCharacterImg userCharacterImg = new UserCharacterImg();
            userCharacterImg.setUser(user);
            userCharacterImg.setImgUrl(savefile.getAbsolutePath());

            userCharacterImgRepository.save(userCharacterImg);

            return ResponseEntity.ok(userCharacterImg.getImgUrl());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.E_500_File_SAVED_FAILED);
        }
    }
}
