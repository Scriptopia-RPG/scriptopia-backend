package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserCharacterImg;
import com.scriptopia.demo.dto.usercharacterimg.UserCharacterImgResponse;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.UserCharacterImgRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCharacterImgService {
    private final UserCharacterImgRepository userCharacterImgRepository;
    private final UserRepository userRepository;

    @Value("${image-dir}")
    private String imageDir;

    @Value("${image-url-prefix:/images}")
    private String imageUrlPrefix;

    @Transactional
    public ResponseEntity<?> saveCharacterImg(Long userId, MultipartFile file) {
        String url = storeUserImage(userId, file);
        return ResponseEntity.ok(url);
    }

    @Transactional
    public ResponseEntity<?> saveUserCharacterImg(Long userId, String imageUrl) {
        if(imageUrl == null || imageUrl.isEmpty()) {
            throw new CustomException(ErrorCode.E_400_IMAGE_URL_ERROR);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        UserCharacterImg src = userCharacterImgRepository.findByUserIdAndImgUrl(userId, imageUrl)
                .orElseThrow(() -> new CustomException(ErrorCode.E_400_IMAGE_URL_ERROR));

        user.setProfileImgUrl(src.getImgUrl());
        userRepository.save(user);

        return ResponseEntity.ok(user.getProfileImgUrl());
    }

    public ResponseEntity<?> getUserCharacterImg(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        List<UserCharacterImg> img = userCharacterImgRepository.findAllByUserId(user.getId());

        List<UserCharacterImgResponse> dto = new ArrayList<>();
        for(UserCharacterImg imgItem : img) {
            UserCharacterImgResponse imgdto = new UserCharacterImgResponse();
            imgdto.setImgUrl(imgItem.getImgUrl());
            dto.add(imgdto);
        }

        return ResponseEntity.ok(dto);
    }

    private String storeUserImage(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.E_400_EMPTY_FILE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new CustomException(ErrorCode.E_400_EMPTY_FILE); // 이미지 외 업로드 차단
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        // 파일명/경로 생성
        String ext = getExtension(file.getOriginalFilename(), contentType);
        String saveName = UUID.randomUUID() + ext;

        // 사용자별 하위 폴더(예: {imageDir}/character/{userId}/)
        Path dir = Paths.get(imageDir, "character", String.valueOf(userId))
                .toAbsolutePath().normalize();
        Path dest = dir.resolve(saveName);

        try {
            Files.createDirectories(dir);           // 디렉터리 없으면 생성
            file.transferTo(dest.toFile());         // 파일 저장

            // 정적 매핑 기준 공개 URL 생성: /images/character/{userId}/{uuid}.png
            String publicUrl = String.format("%s/character/%d/%s", imageUrlPrefix, userId, saveName);

            // DB에는 공개 URL 저장(프론트가 그대로 <img src>로 사용)
            UserCharacterImg entity = new UserCharacterImg();
            entity.setUser(user);
            entity.setImgUrl(publicUrl);
            userCharacterImgRepository.save(entity);

            return publicUrl;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.E_500_File_SAVED_FAILED);
        }
    }

    private String getExtension(String originalFilename, String contentType) {
        // 1) 파일명에서 확장자 우선
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 2) 없으면 MIME으로 대체
        String subtype = contentType.substring(contentType.indexOf('/') + 1); // e.g. image/png → png
        return "." + subtype;
    }
}
