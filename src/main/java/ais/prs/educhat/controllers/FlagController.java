package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ais.prs.educhat.models.FlagRecord;
import ais.prs.educhat.services.FlagService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@RestController
@RequestMapping("/api/flagrecords")
@CrossOrigin(origins = "*")
public class FlagController {
    
    @Autowired
    private FlagService flagSvc;

    // For disciplinary committee to review
    @GetMapping("/{id}")
    public ResponseEntity<String> getFlagRecordsByID(@PathVariable String id){
        
        System.out.println("\nReceived request for Flag Records of: " + id);
        List<FlagRecord> flagRecords = flagSvc.viewFlagRecords(id);
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (FlagRecord flagRecord : flagRecords) {
            jab.add(flagRecord.toJson());
        } 
             
        return ResponseEntity.ok(jab.build().toString());
    }
}
