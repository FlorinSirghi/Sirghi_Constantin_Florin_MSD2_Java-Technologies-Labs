package com.example.Lab4.controller;

import com.example.Lab4.dto.PreferenceEntryResponse;
import com.example.Lab4.dto.StudentPreferenceRequest;
import com.example.Lab4.dto.StudentPreferenceResponse;
import com.example.Lab4.exception.PreferenceValidationException;
import com.example.Lab4.model.Student;
import com.example.Lab4.model.StudentPreference;
import com.example.Lab4.service.StudentPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class StudentPreferenceController {

    private final StudentPreferenceService preferenceService;

    public StudentPreferenceController(StudentPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @PostMapping("/students/{studentId}")
    public ResponseEntity<StudentPreferenceResponse> createPreferences(@PathVariable Long studentId,
                                                                       @Valid @RequestBody StudentPreferenceRequest request) {
        if (preferenceService.preferencesExist(studentId)) {
            throw new PreferenceValidationException("Preferences already exist for student %d. Use PUT to update.".formatted(studentId));
        }
        List<StudentPreference> saved = preferenceService.replacePreferences(studentId, request.preferences());
        Student student = preferenceService.getStudent(studentId);
        StudentPreferenceResponse response = toResponse(student, saved);
        String etag = buildEtag(saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(location)
                .eTag(etag)
                .body(response);
    }

    @PutMapping("/students/{studentId}")
    public ResponseEntity<StudentPreferenceResponse> updatePreferences(@PathVariable Long studentId,
                                                                       @Valid @RequestBody StudentPreferenceRequest request) {
        if (!preferenceService.preferencesExist(studentId)) {
            throw new PreferenceValidationException("Preferences not found for student %d. Use POST to create.".formatted(studentId));
        }
        List<StudentPreference> saved = preferenceService.replacePreferences(studentId, request.preferences());
        Student student = preferenceService.getStudent(studentId);
        StudentPreferenceResponse response = toResponse(student, saved);
        String etag = buildEtag(saved);
        return ResponseEntity.ok()
                .eTag(etag)
                .body(response);
    }

    @Operation(
            summary = "Retrieve preferences for a student",
            description = "Returns the ordered list of course preferences submitted by the student, supporting JSON and XML representations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Preferences found",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StudentPreferenceResponse.class)),
                                    @Content(mediaType = MediaType.APPLICATION_XML_VALUE, schema = @Schema(implementation = StudentPreferenceResponse.class))
                            }),
                    @ApiResponse(responseCode = "304", description = "Not modified (ETag match)"),
                    @ApiResponse(responseCode = "404", description = "Preferences not found")
            }
    )
    @GetMapping(value = "/students/{studentId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<StudentPreferenceResponse> getPreferences(@PathVariable Long studentId,
                                                                    @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {
        List<StudentPreference> preferences = preferenceService.getPreferences(studentId);
        if (preferences.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String etag = buildEtag(preferences);
        if (matchesEtag(ifNoneMatch, etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .build();
        }
        Student student = preferenceService.getStudent(studentId);
        StudentPreferenceResponse response = toResponse(student, preferences);
        return ResponseEntity.ok()
                .eTag(etag)
                .body(response);
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<Void> deletePreferences(@PathVariable Long studentId) {
        preferenceService.deletePreferences(studentId);
        return ResponseEntity.noContent().build();
    }

    private StudentPreferenceResponse toResponse(Student student, List<StudentPreference> preferences) {
        List<PreferenceEntryResponse> entries = preferences.stream()
                .sorted(Comparator.comparing(StudentPreference::getPriority).thenComparing(pref -> pref.getCourse().getId()))
                .map(pref -> new PreferenceEntryResponse(
                        pref.getCourse().getId(),
                        pref.getCourse().getName(),
                        pref.getPriority(),
                        pref.getTieGroup()
                ))
                .toList();
        return new StudentPreferenceResponse(student.getId(), student.getName(), student.getYear(), entries);
    }

    private String buildEtag(List<StudentPreference> preferences) {
        String raw = preferences.stream()
                .sorted(Comparator.comparing(StudentPreference::getId))
                .map(pref -> pref.getId() + ":" + pref.getVersion() + ":" + pref.getPriority() + ":" + (pref.getTieGroup() == null ? "null" : pref.getTieGroup()))
                .reduce((left, right) -> left + "|" + right)
                .orElse("empty");
        String hash = DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
        return "\"" + hash + "\"";
    }

    private boolean matchesEtag(String headerValue, String etag) {
        if (headerValue == null || headerValue.isBlank()) {
            return false;
        }
        String[] tokens = headerValue.split(",");
        for (String token : tokens) {
            String candidate = token.trim();
            if ("*".equals(candidate) || candidate.equals(etag)) {
                return true;
            }
        }
        return false;
    }
}

