package com.bezy.foodapi.controller;

import com.bezy.foodapi.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private CloudinaryService cloudinaryService;

    public String upload(
            @RequestBody MultipartFile file
    ) throws Exception{
        return cloudinaryService.uploadImage(file);
    }
}
