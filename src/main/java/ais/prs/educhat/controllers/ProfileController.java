package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ais.prs.educhat.models.Profile;
import ais.prs.educhat.services.ProfileService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
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
        JsonObjectBuilder job = Json.createObjectBuilder();
        if (!inserted) {
            JsonObject errMsg = job.add("message", "Error adding profile.").build();
            return ResponseEntity.status(500).body(errMsg.toString());
        }
        JsonObject jo = job.add("message", "Profile added successfully.").build();
        return ResponseEntity.ok(jo.toString());
    }

    // Admin Feature: Get class/cca information - to be refined
    @GetMapping("/school-class")
    public ResponseEntity<List<Profile>> getClass(@RequestBody MultiValueMap<String, String> formData) {
        System.out.println("\nReceived form data: " + formData);
        List<Profile> profiles = profileSvc.getProfilesByClass(
            formData.getFirst("school"), 
            formData.getFirst("classCohort")
        );

        return ResponseEntity.ok(profiles);
    }

    // User Feature: Get class profiles by ID (Get access to all members of same class)
    @GetMapping("/getClass/{id}")
    public ResponseEntity<List<Profile>> getClassById(@PathVariable String id) {
        System.out.println("\nReceived request for class with ID: " + id);
        List<Profile> profiles = profileSvc.getClassProfilesById(id, "CLASS"); 

        return ResponseEntity.ok(profiles);
    }

    // User Feature: Get CCA profiles by ID (Get access to all members of same CCA)
    @GetMapping("/getCCA/{id}")
    public ResponseEntity<List<Profile>> getCCAById(@PathVariable String id) {
        System.out.println("\nReceived request for CCA with ID: " + id);
        List<Profile> profiles = profileSvc.getCCAProfilesById(id, "CCA"); 

        return ResponseEntity.ok(profiles);
    }
}
