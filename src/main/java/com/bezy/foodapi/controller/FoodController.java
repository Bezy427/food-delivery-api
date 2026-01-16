package com.bezy.foodapi.controller;

import com.bezy.foodapi.io.FoodRequest;
import com.bezy.foodapi.io.FoodResponse;
import com.bezy.foodapi.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/foods")
@CrossOrigin("*")
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public FoodResponse addFood(@RequestPart("food") String foodString,
                                          @RequestPart("file")MultipartFile file
    ){
        ObjectMapper mapper = new ObjectMapper();
        FoodRequest request = null;
        try {
            request = mapper.readValue(foodString, FoodRequest.class);
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid JSON format"
            );
        }
        return foodService.addFood(request, file);
    }

    @GetMapping
    public List<FoodResponse> readFoods(){

        return foodService.readFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse readFood(
            @PathVariable String id
    ){
        return foodService.readFood(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(
            @PathVariable String id
    ){
        foodService.deleteFood(id);
    }

}
