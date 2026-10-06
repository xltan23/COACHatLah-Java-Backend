package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ais.prs.educhat.dto.EscalationDto;
import ais.prs.educhat.dto.PostResponse;
import ais.prs.educhat.dto.ReviewDto;
import ais.prs.educhat.services.EscalationService;

@RestController 
@RequestMapping ("/api/escalations")
@CrossOrigin (origins = "*")
public class EscalationController {
    
    @Autowired 
    private EscalationService escalationSvc;

    @GetMapping("/get")
    public ResponseEntity<List<EscalationDto>> getEscalations() {
        System.out.println("\nReceived request for escalations");
        return ResponseEntity.ok(escalationSvc.getEscalations());
    }

    @PostMapping("/review") 
    public ResponseEntity<PostResponse> insertReview(@RequestBody ReviewDto reviewDto) {
        System.out.printf("\nReview by %s for escalation %d", reviewDto.reviewedBy(), reviewDto.escalationId());
        boolean inserted = escalationSvc.insertReview(reviewDto);
        if (inserted) {
            return ResponseEntity.ok(new PostResponse("Review inserted successfully"));
        } else {
            return ResponseEntity.status(500).body(new PostResponse("Failed to insert review"));
        }
    }
}
