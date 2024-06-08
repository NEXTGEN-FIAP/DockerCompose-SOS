package com.example.SouthernOceanSentinel_API.controller;

import com.example.SouthernOceanSentinel_API.model.PhotoRecord;
import com.example.SouthernOceanSentinel_API.service.PhotoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

@RestController
@RequestMapping("/imageAnalisis")
public class ImageAnalisisController {

    @Autowired
    private PhotoRecordService photoRecordService;

    @PostMapping("{locationId}/{recordId}")
    public ResponseEntity<String> imageAnalysis(@PathVariable Long locationId, @PathVariable Long recordId) {
        try {
            PhotoRecord photoRecord = photoRecordService.listRecordByIds(locationId, recordId);
            byte[] image = photoRecord.getImageData();
            String base64Image = Base64.getEncoder().encodeToString(image);

            //Image analisis logic
            String urlRoboflowApi = "http://127.0.0.1:8000/roboflow-model";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("image", base64Image);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            //Send the request to the external API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(urlRoboflowApi, HttpMethod.POST, requestEntity, String.class);

            //Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.getBody());

            //Get the "prediction" object
            JSONObject prediction = jsonResponse.getJSONObject("prediction");

            //Get the "predictions" array
            JSONArray predictions = prediction.getJSONArray("predictions");

            //Get the first prediction object
            JSONObject firstPrediction = predictions.getJSONObject(0);

            //Get the "class" key
            String classKey = firstPrediction.getString("class");

            photoRecord.addAnalisis(classKey);
            photoRecordService.saveAnalisis(photoRecord);

            return ResponseEntity.ok(classKey);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error");
        }
    }

}
