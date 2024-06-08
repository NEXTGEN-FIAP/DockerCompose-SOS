package com.example.SouthernOceanSentinel_API.controller.dto;

import com.example.SouthernOceanSentinel_API.model.Location;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class PhotoRecordDTO {
    @NonNull
    @Lob
    private byte[] imageData;
    @NonNull
    private String description;
    @NonNull
    private String waterTemp;
    @Nullable
    private String analisisResult;
}
