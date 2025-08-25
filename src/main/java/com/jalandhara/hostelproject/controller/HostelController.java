package com.jalandhara.hostelproject.controller;

import com.jalandhara.hostelproject.exception.ResourceNotFoundException;
import com.jalandhara.hostelproject.requestBean.*;
import com.jalandhara.hostelproject.responseBean.*;
import com.jalandhara.hostelproject.service.HostelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/hostel")
public class HostelController {

    @Autowired
    private HostelService hostelService;

    // Post Mapping
    @PostMapping("/save/person")
    public ResponseEntity<?> savePerson(@Valid @RequestBody PersonRequestBean request) {
        try {
            return ResponseEntity.ok(hostelService.savePerson(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "error", "Invalid Person Data",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "timestamp", LocalDateTime.now(),
                    "error", "Unable to save person",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/save/room")
    public ResponseEntity<?> saveRoom(@Valid @RequestBody RoomRequestBean request) {
        try {
            return ResponseEntity.ok(hostelService.saveRoom(request));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to save room",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/save/job")
    public ResponseEntity<?> saveJob(@Valid @RequestBody JobRequestBean request) {
        try {
            return ResponseEntity.ok(hostelService.saveJob(request));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to save job",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/save/wifi")
    public ResponseEntity<?> saveWifi(@Valid @RequestBody WifiRequestBean request) {
        try {
            return ResponseEntity.ok(hostelService.saveWifi(request));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to save wifi",
                    "message", ex.getMessage()
            ));
        }
    }

    @PostMapping("/save/hostel")
    public ResponseEntity<?> saveHostelWithAllMappings(@Valid @RequestBody HostelRequestBean request) {
        try {
            return ResponseEntity.ok(hostelService.saveHostelWithAllMappings(request));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to save hostel with mappings",
                    "message", ex.getMessage()
            ));
        }
    }

    // Get Concept
    @GetMapping("/fetch/{type}")
    public ResponseEntity<?> getAll(
            @PathVariable String type,
            @RequestParam int page,
            @RequestParam int size
            //,@RequestParam(defaultValue = "name") String sortBy
    ) {
        try {
            Page<?> pageResult = hostelService.getAll(type, page, size);
            Map<String, Object> response = new HashMap<>();
            response.put("content", pageResult.getContent());
            response.put("currentPage", pageResult.getNumber());
            response.put("totalItems", pageResult.getTotalElements());
            response.put("totalPages", pageResult.getTotalPages());
            response.put("isFirstPage", pageResult.isFirst());
            response.put("isLastPage", pageResult.isLast());
            response.put("size", pageResult.getSize());
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Resource not found",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to fetch data",
                    "message", ex.getMessage()
            ));
        }
    }

    // Get by ID
    @GetMapping("/fetch/{type}/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id, @PathVariable String type) {
        try {
            return ResponseEntity.ok(hostelService.getById(id, type));
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Resource not found",
                    "message", ex.getMessage()
            ));
        }
    }

    // Delete by ID
    @DeleteMapping("/remove/{type}/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id, @PathVariable String type) {
        try {
            return ResponseEntity.ok(hostelService.deleteById(id, type));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to delete",
                    "message", ex.getMessage()
            ));
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllData() {
        try {
            return ResponseEntity.ok(hostelService.deleteAllData());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to delete all data",
                    "message", ex.getMessage()
            ));
        }
    }

    // Update
    @PutMapping("/update/{personId}")
    public ResponseEntity<?> updateHostel(@PathVariable UUID personId, @Valid @RequestBody HostelRequestBean request) {
        try {
            HostelResponseBean updated = hostelService.updateHostel(personId, request);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Person not found",
                    "message", ex.getMessage()
            ));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to update hostel",
                    "message", ex.getMessage()
            ));
        }
    }

    // Paging
    @GetMapping("/hostel/paging")
    public ResponseEntity<?> getHostelPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "name") String sort) {
        try {
            Page<HostelResponseBean> pageHostel = hostelService.getPaginatedHostel(page, size, sort);
            Map<String, Object> response = new HashMap<>();
            response.put("data", pageHostel.getContent());
            response.put("currentPage", pageHostel.getNumber());
            response.put("totalItems", pageHostel.getTotalElements());
            response.put("totalPages", pageHostel.getTotalPages());
            response.put("isLastPage", pageHostel.isLast());
            response.put("isFirstPage", pageHostel.isFirst());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to fetch hostel pages",
                    "message", ex.getMessage()
            ));
        }
    }

    // Filter concept
    @GetMapping("/person/nameAndAgeFilter")
    public ResponseEntity<?> getPersonsByAgeAndName(
            @RequestParam Integer minAge,
            @RequestParam List<String> startLetters) {
        try {
            return ResponseEntity.ok(hostelService.getPersonsByAgeAndName(minAge, startLetters));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to fetch filtered persons",
                    "message", ex.getMessage()
            ));
        }
    }

    @GetMapping("/room/ageAndFloorNoFilter")
    public ResponseEntity<?> getFilteredRooms(
            @RequestParam Integer minAge,
            @RequestParam Integer floorNo) {
        try {
            return ResponseEntity.ok(hostelService.getFilteredRooms(minAge, floorNo));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unable to fetch filtered rooms",
                    "message", ex.getMessage()
            ));
        }
    }

}
