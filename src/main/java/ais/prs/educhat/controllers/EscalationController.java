package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ais.prs.educhat.dto.EscalationDto;
import ais.prs.educhat.services.EscalationService;

@RestController 
@RequestMapping ("/api/escalations")
@CrossOrigin (origins = "*")
public class EscalationController {
    
    @Autowired 
    private EscalationService escalationSvc;

    @GetMapping 
    public ResponseEntity<List<EscalationDto>> getEscalations() {
        System.out.println("\nReceived request for escalations");
        return ResponseEntity.ok(escalationSvc.getEscalations());
    }
}
