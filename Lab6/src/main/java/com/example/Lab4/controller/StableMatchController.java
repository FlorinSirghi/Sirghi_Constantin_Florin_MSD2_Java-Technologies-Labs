package com.example.Lab4.controller;

import com.example.Lab4.dto.StableMatchIntegrationDTO;
import com.example.Lab4.service.StableMatchIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stable-match")
@RequiredArgsConstructor
@Slf4j
public class StableMatchController {
    
    private final StableMatchIntegrationService stableMatchIntegrationService;
    
    @PostMapping("/packs/{packId}")
    @Operation(summary = "Trigger matching for a specific pack",
               description = "Initiates the stable matching process for all optional courses in a pack")
    @ApiResponse(responseCode = "200", description = "Matching process initiated successfully")
    public ResponseEntity<StableMatchIntegrationDTO.MatchResult> matchForPack(@PathVariable Long packId) {
        log.info("Triggering matching for pack: {}", packId);
        try {
            CompletableFuture<StableMatchIntegrationDTO.MatchResult> future = 
                    stableMatchIntegrationService.matchForPack(packId);
            StableMatchIntegrationDTO.MatchResult result = future.get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error triggering matching for pack {}: {}", packId, e.getMessage(), e);
            StableMatchIntegrationDTO.MatchResult errorResult = new StableMatchIntegrationDTO.MatchResult();
            errorResult.setPackId(packId);
            errorResult.setSuccess(false);
            errorResult.setMessage("Error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }
    
    @PostMapping("/packs")
    @Operation(summary = "Trigger matching for all packs",
               description = "Initiates the stable matching process for all packs of optional courses")
    @ApiResponse(responseCode = "200", description = "Matching process initiated for all packs")
    public ResponseEntity<Map<Long, StableMatchIntegrationDTO.MatchResult>> matchForAllPacks() {
        log.info("Triggering matching for all packs");
        try {
            CompletableFuture<Map<Long, StableMatchIntegrationDTO.MatchResult>> future = 
                    stableMatchIntegrationService.matchForAllPacks();
            Map<Long, StableMatchIntegrationDTO.MatchResult> results = future.get();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error triggering matching for all packs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}




