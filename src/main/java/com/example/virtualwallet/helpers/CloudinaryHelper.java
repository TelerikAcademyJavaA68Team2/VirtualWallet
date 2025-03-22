package com.example.virtualwallet.helpers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CloudinaryHelper {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadUserProfilePhoto(MultipartFile multipartFile) throws IOException {

        Cloudinary cloudinary = getCloudinary();

        Map<String, Object> uploadResult = cloudinary.uploader()
                .upload(multipartFile.getBytes(), ObjectUtils.emptyMap());

        //        user.setPhoto(photoUrl);
//        userService.save(user);
        return uploadResult.get("url").toString();
    }
}
