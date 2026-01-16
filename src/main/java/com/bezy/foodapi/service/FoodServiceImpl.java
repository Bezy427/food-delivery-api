package com.bezy.foodapi.service;

import com.bezy.foodapi.entity.FoodEntity;
import com.bezy.foodapi.io.FoodRequest;
import com.bezy.foodapi.io.FoodResponse;
import com.bezy.foodapi.repositories.FoodRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FoodServiceImpl implements FoodService {

    private final Cloudinary cloudinary;
    private final FoodRepository foodRepository;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            String uniqueFilename = UUID.randomUUID().toString();

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "foodies",
                            "public_id", uniqueFilename,
                            "resource_type", "image"
                    ));

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload error");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity entity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        entity.setImageUrl(imageUrl);
        entity = foodRepository.save(entity);
        return convertToResponse(entity);
    }

    @Override
    public List<FoodResponse> readFoods() {
        return foodRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FoodResponse readFood(String id) {
        FoodEntity existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food id not found: " + id));
        return convertToResponse(existingFood);
    }

    @Override
    public void deleteFood(String id) {
        // Fetch the entity directly
        FoodEntity food = foodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found with id: " + id));

        String imageUrl = food.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String[] parts = imageUrl.split("/");
            String fileNameWithExtension = parts[parts.length - 1];
            String publicId = fileNameWithExtension.contains(".") ?
                    fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf(".")) :
                    fileNameWithExtension;

            try {
                cloudinary.uploader().destroy("foodies/" + publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting image from Cloudinary");
            }
        } else {
            System.out.println("No image to delete for food id: " + id);
        }

        // Delete the food entity from the database
        foodRepository.delete(food);
    }


    // Helper methods
    private FoodEntity convertToEntity(FoodRequest request) {
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity entity) {
        return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
