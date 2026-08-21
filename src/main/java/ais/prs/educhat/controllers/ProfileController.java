package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ais.prs.educhat.models.Profile;
import ais.prs.educhat.services.ProfileService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    
    @Autowired
    private ProfileService profileSvc;

    // Admin Feature: Add a new profile
    @PostMapping("/add")
    public ResponseEntity<String> addProfile(@RequestBody MultiValueMap<String, String> formData) {
        System.out.println("\nReceived form data: " + formData);
        boolean inserted = profileSvc.addProfile(
            formData.getFirst("name"),
            formData.getFirst("gender"),
            formData.getFirst("role"),
            formData.getFirst("school"),
            formData.getFirst("classCohort"),
            formData.getFirst("cca")
        );

        if (inserted) {
            return ResponseEntity.ok("Profile added successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add profile.");
        }
    }

    // Admin Feature: Get class information
    @GetMapping("/getClass")
    public ResponseEntity<String> getClass(@RequestBody MultiValueMap<String, String> formData) {
        System.out.println("\nReceived form data: " + formData);
        List<Profile> profiles = profileSvc.getProfilesByClass(
            formData.getFirst("school"), 
            formData.getFirst("classCohort")
        );
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Profile profile : profiles) {
            jab.add(profile.toJson());
        }

        return ResponseEntity.ok(jab.build().toString());
    }

    // User Feature: Get class profiles by ID (Get access to all members of same class)
    @GetMapping("/getClass/{id}")
    public ResponseEntity<String> getClassById(@PathVariable String id) {
        System.out.println("\nReceived request for class with ID: " + id);
        List<Profile> profiles = profileSvc.getClassProfilesById(id, "CLASS"); 
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Profile profile : profiles) {
            jab.add(profile.toJson());
        }

        return ResponseEntity.ok(jab.build().toString());
    }

    // User Feature: Get CCA profiles by ID (Get access to all members of same CCA)
    @GetMapping("/getCCA/{id}")
    public ResponseEntity<String> getCCAById(@PathVariable String id) {
        System.out.println("\nReceived request for CCA with ID: " + id);
        List<Profile> profiles = profileSvc.getCCAProfilesById(id, "CCA"); 
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Profile profile : profiles) {
            jab.add(profile.toJson());
        }

        return ResponseEntity.ok(jab.build().toString());
    }
}
