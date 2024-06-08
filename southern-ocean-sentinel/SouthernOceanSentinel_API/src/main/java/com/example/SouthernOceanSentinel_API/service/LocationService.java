package com.example.SouthernOceanSentinel_API.service;

import com.example.SouthernOceanSentinel_API.controller.dto.LocationDTO;
import com.example.SouthernOceanSentinel_API.controller.dto.PhotoRecordDTO;
import com.example.SouthernOceanSentinel_API.model.Location;
import com.example.SouthernOceanSentinel_API.model.PhotoRecord;
import com.example.SouthernOceanSentinel_API.repository.LocationRepository;
import com.example.SouthernOceanSentinel_API.repository.PhotoRecordRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private PhotoRecordRepository photoRecordRepository;


    public Optional<Location> listLocationById(Long id) {
        try {
            Optional<Location> location = locationRepository.findById(id);
            if (location.isPresent()) {
                return location;
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<PhotoRecord> listRecords(Long locationId) {
        try {
            Optional<Location> optionalLocation = locationRepository.findById(locationId);
            if (optionalLocation.isPresent()){
                Location requestedLocation = optionalLocation.get();
                return requestedLocation.getRecords();
            }
        } catch (Exception e) {
            Optional.empty();
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Location saveNewLocation(LocationDTO newLocation) {
        Location location = new Location();
        location.setName(newLocation.getName());
        location.setCountry(newLocation.getCountry());
        location.setCoordinates(newLocation.getCoordinates());
        return locationRepository.save(location);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PhotoRecord saveNewRecord(@NonNull PhotoRecordDTO newRecordDTO, Long id) {
        Optional<Location> optionalLocation = locationRepository.findById(id);
        if (optionalLocation.isPresent()){
            Location location = optionalLocation.get();

            PhotoRecord newPhotoRecord = new PhotoRecord(newRecordDTO);
            photoRecordRepository.save(newPhotoRecord);

            location.addPhotoRecord(newPhotoRecord);
            locationRepository.save(location);

            return newPhotoRecord;
        } else {
            throw new IllegalArgumentException("Location not found by ID");
        }
    }
}
