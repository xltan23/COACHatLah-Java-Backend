package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ais.prs.educhat.dto.MessageDto;
import ais.prs.educhat.dto.MessageRequest;
import ais.prs.educhat.dto.MessageResponse;
import ais.prs.educhat.models.Message;
import ais.prs.educhat.services.MessageService;

@RestController
@RequestMapping("/api/message")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageSvc;

    // Postman usage: For testing
    @PostMapping("/post")
    public ResponseEntity<MessageResponse> postMessage(@RequestBody MultiValueMap<String,String> formData) {
        
        MessageResponse msgResp = messageSvc.sendMessage(
            formData.getFirst("sender"),
            formData.getFirst("recipient"),
            formData.getFirst("message")
        );
        return ResponseEntity.ok(msgResp);
    }

    // Send message between 2 users
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest msgReq) {
        
        MessageResponse msgResp = messageSvc.sendMessage(
            msgReq.senderId(), 
            msgReq.recipientId(), 
            msgReq.content()
        );
        return ResponseEntity.ok(msgResp);
    }

    // Retrieve conversation history between 2 users
    @GetMapping("/history")
    public ResponseEntity<List<MessageDto>> retrieveConversation(@RequestParam String uid1, @RequestParam String uid2) {
        
        List<Message> messages = messageSvc.getConversation(uid1, uid2);
        List<MessageDto> messageDtos = messages.stream()
            .map(MessageDto::from)
            .toList();

        return ResponseEntity.ok(messageDtos);
    }
}
