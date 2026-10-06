package ais.prs.educhat.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ais.prs.educhat.dto.EscalationDto;
import ais.prs.educhat.repositories.EscalationRepository;

@Service 
public class EscalationService {
    
    @Autowired 
    private EscalationRepository escalationRepo;

    public List<EscalationDto> getEscalations() {
        return escalationRepo.getEscalations().stream()
                .map(EscalationDto::from)
                .collect(Collectors.toList());
    }
}
