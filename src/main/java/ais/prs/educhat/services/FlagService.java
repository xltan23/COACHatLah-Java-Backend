package ais.prs.educhat.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ais.prs.educhat.dto.ModerationResponse;
import ais.prs.educhat.models.FlagRecord;
import ais.prs.educhat.models.Message;
import ais.prs.educhat.models.enums.FlagCategory;
import ais.prs.educhat.repositories.FlagRepository;
import ais.prs.educhat.repositories.ProfileRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class FlagService {
    
    @Autowired
    private FlagRepository flagRepo;

    @Autowired
    private ProfileRepository profileRepo;

    @Transactional
    public FlagRecord flagRecord(String sender, String recipient, Message message, String llmRephrased,
                                 List<FlagCategory> flagCategories, ModerationResponse scores) {
        System.out.printf("\nCreating flag record for: %s", sender);
        
        // Create a new FlagRecord object and store it in the database
        FlagRecord fr = new FlagRecord(sender, recipient, message.getId(), message.getContent(), llmRephrased, flagCategories, toScoresJson(scores));
        flagRepo.addFlagRecord(fr);

        // One flag per message, however many categories it was flagged for
        profileRepo.incrementFlagCount(sender);

        // Check flag count sender
        int currentCount = profileRepo.getFlagCount(sender);
        if (currentCount >= 10) {
            System.out.printf("\nProfile %s to be reviewed", sender);
            // Additional method to alert disciplinary committee
        }
        return fr;
    }

    public List<FlagRecord> viewFlagRecords(String id) {
        return flagRepo.getFlagRecords(id);
    }

    // Goes via Float.toString so a score stores as 0.99926656, not the widened double 0.9992665648460388
    private JsonObject toScoresJson(ModerationResponse scores) {
        if (scores == null) {
            return null;
        }
        return Json.createObjectBuilder()
            .add("toxic", new BigDecimal(Float.toString(scores.toxic())))
            .add("inappropriate", new BigDecimal(Float.toString(scores.inappropriate())))
            .add("cyberbullying", new BigDecimal(Float.toString(scores.cyberbullying())))
            .build();
    }
}
