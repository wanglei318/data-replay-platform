package com.example.controller;

import com.example.service.DataService;
import com.example.vo.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
@CrossOrigin
public class DataController {
    private final DataService dataService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileId = dataService.processAndStoreFile(file);
        return ResponseEntity.ok(fileId);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<DataResponse> getData(
            @PathVariable String fileId,
            @RequestParam(required = false, defaultValue = "1") int samplingRate) {
        DataResponse response = dataService.getDataWithSampling(fileId, samplingRate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileId}/info")
    public ResponseEntity<DataResponse> getDataInfo(@PathVariable String fileId) {
        DataResponse response = dataService.getDataInfo(fileId);
        return ResponseEntity.ok(response);
    }
}