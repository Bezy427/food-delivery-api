package com.bezy.foodapi.service;

import com.bezy.foodapi.io.FoodRequest;
import com.bezy.foodapi.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {


    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse> readFoods();

    FoodResponse readFood(String id);

    void deleteFood(String id);
}
