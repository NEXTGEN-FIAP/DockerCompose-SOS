package com.example.SouthernOceanSentinel_API.controller.dto;


import lombok.Data;
import lombok.NonNull;

@Data
public class LocationDTO {
    @NonNull
    private String name;
    @NonNull
    private String country;
    @NonNull
    private String coordinates;
}
