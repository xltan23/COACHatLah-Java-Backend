package ais.prs.educhat.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ais.prs.educhat.clients.OpenAiRephraseClient;
import ais.prs.educhat.dto.MessageResponse;
import ais.prs.educhat.models.Message;
import ais.prs.educhat.models.enums.FlagCategory;
import ais.prs.educhat.repositories.MessageRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private FlagService flagSvc;

    @Autowired
    private OpenAiRephraseClient openAiClient;

    private static final Duration MESSAGE_EXPIRATION = Duration.ofHours(48); // Set expiration to 48 hours

    public MessageResponse sendMessage(String sender, String recipient, String content) {
        Message message = new Message(sender, recipient, content);
        String key = buildKey(sender, recipient);

        List<Message> context = messageRepo.getRecentContext(key, 10);
        String formattedContext = formatContext(context, sender);
        System.out.printf("Context:\n%s", formattedContext);

        // Establish message check (Python API) 
        boolean flagged = false;
        FlagCategory flagCategory = FlagCategory.CYBERBULLYING;

        if (flagged) {
            System.out.println("\nFlagging message...");
            String llmRephrased = openAiClient.rephrase(content, flagCategory, formattedContext);
            System.out.printf("\nLLM Response: " + llmRephrased);
            // If flagged, create FlagRecord object and update Profile flag_count
            flagSvc.flagRecord(sender, recipient, message, content, llmRephrased, flagCategory);

            MessageResponse notSent = new MessageResponse(false, null, flagCategory.name(), llmRephrased);
            return notSent;
        } 

        // If not flagged, proceed to send message
        messageRepo.saveMessage(key, message);
        MessageResponse sent = new MessageResponse(true, message, null, null);
        return sent;
    }

    public List<Message> getConversation(String userID1, String userID2) {
        String key = buildKey(userID1, userID2);
        Instant cutoff = Instant.now().minus(MESSAGE_EXPIRATION);
        messageRepo.removeExpiredMessages(key, cutoff);
        return messageRepo.findConversation(key);
    }

    // Standardize key based on IDs of users in conversation
    private String buildKey(String sender, String recipient) {
        List<String> userIDs = new LinkedList<>();
        userIDs.add(sender);
        userIDs.add(recipient);
        Collections.sort(userIDs); // Sort to ensure consistent key regardless of sender/recipient order
        String key = "CHAT{" + userIDs.get(0) + ":" + userIDs.get(1) + "}"; // Unique key for the conversation
        return key;
    }

    // Format context: 
    private String formatContext(List<Message> context, String sender) {
        StringBuilder sb = new StringBuilder();
        for (Message m : context) {
            String label = m.getSenderId().equals(sender) ? "Sender" : "Recipient";
            sb.append(label).append(":").append(m.getContent()).append("\n");
        }
        return sb.toString();
    }
}
