package ais.prs.educhat.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import ais.prs.educhat.dto.MessageRequest;
import ais.prs.educhat.dto.MessageResponse;
import ais.prs.educhat.services.MessageService;

// Live delivery channel. History still comes from MessageController (REST + Redis).
@Controller
public class ChatSocketController {

    @Autowired
    private MessageService messageSvc;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Client sends to /app/chat.send. senderId comes from the STOMP Principal,
    // never from the payload, so a client cannot post as someone else.
    @MessageMapping("/chat.send")
    public void send(@Payload MessageRequest req, Principal principal) {
        String senderId = principal.getName();

        MessageResponse result = messageSvc.sendMessage(senderId, req.recipientId(), req.content());

        if (result.delivered()) {
            // Deliver to recipient and echo to sender so their UI can confirm.
            messagingTemplate.convertAndSendToUser(req.recipientId(), "/queue/messages", result);
            messagingTemplate.convertAndSendToUser(senderId, "/queue/messages", result);
        } else {
            // Flagged: recipient gets nothing. Sender gets the category + rephrase suggestion.
            messagingTemplate.convertAndSendToUser(senderId, "/queue/moderation", result);
        }
    }
}
