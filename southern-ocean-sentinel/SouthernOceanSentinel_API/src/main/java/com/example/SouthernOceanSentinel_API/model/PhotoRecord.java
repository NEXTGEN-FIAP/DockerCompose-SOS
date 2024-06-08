package com.example.SouthernOceanSentinel_API.model;

import com.example.SouthernOceanSentinel_API.controller.dto.PhotoRecordDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoRecord {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "date_created")
    private LocalDateTime date;
    @Lob
    private byte[] imageData;
    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;
    private String description;
    private String waterTemp;
    @Nullable
    private String analisisResult;

    @PrePersist
    protected void onCreate() {
        date = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        date = LocalDateTime.now();
    }

    public void addAnalisis(String analisisResult){
        this.analisisResult = analisisResult;
    }

    //DTO Constructor
    public PhotoRecord(PhotoRecordDTO newRecord){
        this.imageData = newRecord.getImageData();
        this.description = newRecord.getDescription();
        this.waterTemp = newRecord.getWaterTemp();
        this.analisisResult = newRecord.getAnalisisResult();
    }
}
