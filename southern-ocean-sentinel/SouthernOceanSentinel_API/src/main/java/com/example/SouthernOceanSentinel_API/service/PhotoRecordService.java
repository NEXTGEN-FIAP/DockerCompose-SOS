package com.example.SouthernOceanSentinel_API.service;
import com.example.SouthernOceanSentinel_API.model.Location;
import com.example.SouthernOceanSentinel_API.model.PhotoRecord;
import com.example.SouthernOceanSentinel_API.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import com.example.SouthernOceanSentinel_API.repository.PhotoRecordRepository;

@Service
public class PhotoRecordService {
    @Autowired
    private PhotoRecordRepository photoRecordRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public PhotoRecord savePhotoRecord(Long locationId,MultipartFile file, String description, String waterTemp) throws IOException {
        PhotoRecord photoRecord = new PhotoRecord();
        photoRecord.setImageData(file.getBytes());

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID"));
        photoRecord.setLocation(location);
        photoRecord.setDescription(description);
        photoRecord.setWaterTemp(waterTemp);

        return photoRecordRepository.save(photoRecord);
    }

    public PhotoRecord listRecordByIds(Long locationId, Long recordId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID"));
        return location.getRecordById(recordId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAnalisis(PhotoRecord photoRecord) {
        photoRecordRepository.save(photoRecord);
    }
}
