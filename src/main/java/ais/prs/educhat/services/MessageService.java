package ais.prs.educhat.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ais.prs.educhat.clients.ModerationClient;
import ais.prs.educhat.clients.OpenAiRephraseClient;
import ais.prs.educhat.dto.MessageDto;
import ais.prs.educhat.dto.MessageResponse;
import ais.prs.educhat.dto.ModerationResponse;
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

    @Autowired
    private ModerationClient moderationClient;

    private static final Duration MESSAGE_EXPIRATION = Duration.ofHours(48); // Set expiration to 48 hours

    // Send message between 2 users, with moderation and flagging
    public MessageResponse sendMessage(String senderId, String recipientId, String content) {

        Message message = new Message(senderId, recipientId, content);
        String key = buildKey(senderId, recipientId);
        // Context retrieval: get the last 10 messages in the conversation for context
        List<Message> context = messageRepo.getRecentContext(key, 10);

        // Establish message check (Python API)
        ModerationResponse scores = moderationClient.score(content);
        if (scores != null) {
            System.out.printf("\nModeration scores -> toxic: %.4f, inappropriate: %.4f, cyberbullying: %.4f\n",
                    scores.toxic(), scores.inappropriate(), scores.cyberbullying());
        } else {
            System.out.println("\nModeration scores unavailable");
        }

        // Determine if message is flagged based on scores and threshold
        List<FlagCategory> flagCategories = flaggedCategories(scores);
        if (!flagCategories.isEmpty()) {
            String formattedContext = formatContext(context, senderId);
            System.out.printf("Context:\n%s", formattedContext);

            String formattedCategories = formatFlagCategories(flagCategories);
            System.out.printf("\nFlagging message as %s", formattedCategories);

            String llmRephrased = openAiClient.rephrase(content, formattedCategories, formattedContext);
            System.out.printf("\nLLM Response: %s", llmRephrased);

            // If flagged, create FlagRecord object and update Profile flag_count
            flagSvc.flagRecord(senderId, recipientId, message, llmRephrased, flagCategories, scores);

            MessageResponse notSent = new MessageResponse(false, null, formattedCategories, llmRephrased);
            return notSent;
        }

        // If not flagged, proceed to send message
        messageRepo.saveMessage(key, message);
        MessageDto messageDto = new MessageDto(message.getId(), senderId, recipientId, content, message.getTimestamp());
        MessageResponse sent = new MessageResponse(true, messageDto, null, null);
        return sent;
    }

    // Conversation retrieval: get all messages between two users, removing expired messages first
    public List<Message> getConversation(String userID1, String userID2) {
        String key = buildKey(userID1, userID2);
        Instant cutoff = Instant.now().minus(MESSAGE_EXPIRATION);
        messageRepo.removeExpiredMessages(key, cutoff);
        return messageRepo.findConversation(key);
    }

    // Standardize key based on IDs of users in conversation
    private String buildKey(String senderId, String recipientId) {
        List<String> userIDs = new LinkedList<>();
        userIDs.add(senderId);
        userIDs.add(recipientId);
        Collections.sort(userIDs); // Sort to ensure consistent key regardless of sender/recipient order
        String key = "CHAT{" + userIDs.get(0) + ":" + userIDs.get(1) + "}"; // Unique key for the conversation
        return key;
    }

    // Format context messages for LLM input, labeling each message as "Sender" or "Recipient"
    private String formatContext(List<Message> context, String sender) {
        StringBuilder sb = new StringBuilder();
        for (Message m : context) {
            String label = m.getSenderId().equals(sender) ? "Sender" : "Recipient";
            sb.append(label).append(":").append(m.getContent()).append("\n");
        }
        return sb.toString();
    }

    // Format flagged categories for LLM input, joining them into a single string
    private String formatFlagCategories(List<FlagCategory> flagCategories) {
        return flagCategories.stream()
                .map(Enum::name)
                .reduce((a, b) -> a + ", " + b)
                .orElse("No categories");
    }

    // Categories whose score is above the threshold, highest score first. No scores (moderation service unavailable) -> none flagged.
    private List<FlagCategory> flaggedCategories(ModerationResponse scores) {
        // Return empty list if scores are null (moderation service unavailable)
        if (scores == null) {
            return List.of();
        }
        // Threshold can be adjusted based on our threshold selection in the moderation service.
        // Threshold for toxic: 0.62, inappropriate: 0.55, cyberbullying: 0.85
        List<FlagCategory> flaggedCategories = new LinkedList<>();
        if (scores.toxic() > 0.62) {
            flaggedCategories.add(FlagCategory.TOXIC);
        }
        if (scores.inappropriate() > 0.55) {
            flaggedCategories.add(FlagCategory.INAPPROPRIATE);
        }
        if (scores.cyberbullying() > 0.85) {
            flaggedCategories.add(FlagCategory.CYBERBULLYING);
        }
        return flaggedCategories;
    }
}
