package ais.prs.educhat.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ais.prs.educhat.dto.MessageResponse;
import ais.prs.educhat.models.Message;
import ais.prs.educhat.services.MessageService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@Controller
@RequestMapping("/message")
public class MessageController {
    
    @Autowired
    private MessageService messageSvc;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> saveMessage(@RequestBody MultiValueMap<String, String> formData) {
        
        MessageResponse mr = messageSvc.sendMessage(
                                formData.getFirst("sender"), 
                                formData.getFirst("receiver"),
                                formData.getFirst("message")
                            );
        return ResponseEntity.ok(mr);
    }

    @GetMapping("/retrieve")
    public ResponseEntity<String> retrieveConversation(@RequestBody MultiValueMap<String, String> formData) {
        List<Message> conversation = messageSvc.getConversation(formData.getFirst("userID1"),formData.getFirst("userID2"));
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Message message : conversation) {
            jab.add(message.toJson());
        }
        return ResponseEntity.ok(jab.build().toString());
    }
}
