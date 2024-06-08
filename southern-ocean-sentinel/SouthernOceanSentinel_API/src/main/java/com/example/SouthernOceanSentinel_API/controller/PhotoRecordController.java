package com.example.SouthernOceanSentinel_API.controller;

import com.example.SouthernOceanSentinel_API.controller.dto.PhotoRecordDTO;
import com.example.SouthernOceanSentinel_API.model.PhotoRecord;
import com.example.SouthernOceanSentinel_API.service.PhotoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/records")
public class PhotoRecordController {
    @Autowired
    private PhotoRecordService photoRecordService;

    @PostMapping("{locationId}")
    public ResponseEntity<PhotoRecord> createPhotoRecord(
            @PathVariable Long locationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam("waterTemp") String waterTemp) {

        try {
            PhotoRecord photoRecord = photoRecordService.savePhotoRecord(locationId ,file, description, waterTemp);
            return ResponseEntity.ok(photoRecord);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
