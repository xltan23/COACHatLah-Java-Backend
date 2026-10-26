package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ais.prs.educhat.dto.FlagRecordDto;
import ais.prs.educhat.services.FlagService;

@RestController
@RequestMapping("/api/flagrecords")
@CrossOrigin(origins = "*")
public class FlagController {
    
    @Autowired
    private FlagService flagSvc;

    // For disciplinary committee to review
    @GetMapping("/{id}")
    public ResponseEntity<List<FlagRecordDto>> getFlagRecordsByID(@PathVariable String id){
        
        System.out.println("\nReceived request for Flag Records of: " + id);
        List<FlagRecordDto> flagRecords = flagSvc.viewFlagRecords(id);
             
        return ResponseEntity.ok(flagRecords);
    }
}
