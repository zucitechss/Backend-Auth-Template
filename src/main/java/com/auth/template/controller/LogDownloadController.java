package com.auth.template.controller;

import com.auth.template.serviceImpl.LogDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.io.IOException;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Download Controller", description = "API for downloading application logs")
public class LogDownloadController {

    @Autowired
    private LogDownloadService logDownloadService;

    @Operation(summary = "Download logs", description = "Downloads the application log file as a resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log file downloaded successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error while downloading logs")
    })
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadLogs() throws IOException {
        return logDownloadService.downloadLogs();
    }
}
