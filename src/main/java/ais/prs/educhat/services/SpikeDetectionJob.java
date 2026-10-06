package ais.prs.educhat.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ais.prs.educhat.clients.SpikeDetectionClient;
import ais.prs.educhat.dto.SpikeDetectionRequestDto;
import ais.prs.educhat.dto.SpikeDetectionResponseDto;
import ais.prs.educhat.dto.SpikeStudentCountsDto;
import ais.prs.educhat.dto.SpikeStudentResultDto;
import ais.prs.educhat.models.Escalation;
import ais.prs.educhat.models.Profile;
import ais.prs.educhat.models.enums.TriggerType;
import ais.prs.educhat.repositories.EscalationRepository;
import ais.prs.educhat.repositories.FlagRepository;
import ais.prs.educhat.repositories.ProfileRepository;

@Component
public class SpikeDetectionJob {

    @Autowired
    private FlagRepository flagRepo;

    @Autowired
    private SpikeDetectionClient spikeClient;

    @Autowired
    private ProfileRepository profileRepo;

    @Autowired
    private EscalationRepository escalationRepo;

    // Every day at 23:00 Singapore time, regardless of the server's own timezone
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Singapore")
    public void runDailySpikeDetection() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Singapore"));
        System.out.printf("\nRunning daily spike detection for %s", today);

        List<SpikeStudentCountsDto> students = flagRepo.getHourlyFlagCounts(today);
        if (students.isEmpty()) {
            System.out.println("\nNo students flagged today, skipping spike detection");
            return;
        }

        SpikeDetectionResponseDto response = spikeClient.detect(new SpikeDetectionRequestDto(today, students));
        if (response == null) {
            System.err.println("\nSpike detection unavailable, no results for " + today);
            return;
        }

        System.out.printf("\nSpike detection results for %s: %s", today, response.results());

        // One escalation per student with at least one anomalous hour
        for (SpikeStudentResultDto result : response.results()) {
            if (result.timestamps() == null || result.timestamps().isEmpty()) {
                continue;
            }
            Profile profile = profileRepo.getSpecificProfile(result.id());
            if (profile.getId() == null) {
                System.err.println("\nSpike result for unknown student " + result.id() + ", skipping");
                continue;
            }
            Escalation escalation = new Escalation().createEscalation(profile, TriggerType.ANOMALY_DETECTED, String.join(",", result.timestamps()));
            escalationRepo.addEscalation(escalation);
        }
    }
}
