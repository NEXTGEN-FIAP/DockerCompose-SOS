package com.example.SouthernOceanSentinel_API.controller;

import com.example.SouthernOceanSentinel_API.controller.dto.LocationDTO;
import com.example.SouthernOceanSentinel_API.controller.dto.PhotoRecordDTO;
import com.example.SouthernOceanSentinel_API.model.Location;
import com.example.SouthernOceanSentinel_API.model.PhotoRecord;
import com.example.SouthernOceanSentinel_API.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/locations")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("{locationId}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long locationId) {
        Optional<Location> optionalLocation = locationService.listLocationById(locationId);
        if (optionalLocation.isPresent()) {
            Location location = optionalLocation.get();
            Link link = linkTo(LocationController.class).slash(location.getId()).withSelfRel();
            location.add(link);
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/record/{locationId}")
    public ResponseEntity<List<PhotoRecord>> getRecordsByLocationId(@PathVariable Long locationId){
        return ResponseEntity.ok(locationService.listRecords(locationId));
    }

    @PostMapping
    public ResponseEntity<Location> postNewLocation(@RequestBody LocationDTO newLocation) {
        Location savedLocation = locationService.saveNewLocation(newLocation);
        return new ResponseEntity<>(savedLocation, HttpStatus.CREATED);
    }

    @PostMapping("/record/{locationId}")
    public ResponseEntity<PhotoRecord> postNewRecord(@RequestBody PhotoRecordDTO newRecord, @PathVariable Long locationId){
        return ResponseEntity.ok(locationService.saveNewRecord(newRecord, locationId));
    }
}
