package ais.prs.educhat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ais.prs.educhat.models.FlagRecord;
import ais.prs.educhat.models.Message;
import ais.prs.educhat.models.enums.FlagCategory;
import ais.prs.educhat.repositories.FlagRepository;
import ais.prs.educhat.repositories.ProfileRepository;

@Service
public class FlagService {
    
    @Autowired
    private FlagRepository flagRepo;

    @Autowired
    private ProfileRepository profileRepo;

    @Transactional
    public FlagRecord flagRecord(String sender, String receiver, Message message, String content, String llmRephrased, FlagCategory flagCategory) {
        System.out.printf("\nCreating flag record for: %s", sender);
        FlagRecord fr = new FlagRecord(sender, receiver, message.getId(), content, llmRephrased, flagCategory);
        flagRepo.addFlagRecord(fr);
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
}
