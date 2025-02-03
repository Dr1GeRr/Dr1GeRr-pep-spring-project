package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;   // <-- import Map
import java.util.Optional;

@RestController
@RequestMapping
public class SocialMediaController {

    private final AccountService accountService;
    private final MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        return accountService.register(account)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account loginRequest) {
        return accountService.login(loginRequest.getUsername(), loginRequest.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return messageService.createMessage(message)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        return messageService.getMessageById(messageId)
                .map(ResponseEntity::ok)
                // Return 200 OK with empty body if not found (you could use .notFound().build() if you prefer 404)
                .orElse(ResponseEntity.ok().build());
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        boolean deleted = messageService.deleteMessage(messageId);

        if (deleted) {
            // Return 200 OK with '1' if found and deleted
            return ResponseEntity.ok(1);
        }
        // Return 200 OK with empty body if not found (again, you could use 404 if you prefer)
        return ResponseEntity.ok().build();
    }

    // -----------------------------------------------------------------------
    // PATCH endpoint updated to accept JSON with "messageText" field.
    // -----------------------------------------------------------------------
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(
        @PathVariable Integer messageId,
        @RequestBody Map<String, Object> body
    ) {
        // Safely extract "messageText" from the JSON map
        String newText = (String) body.get("messageText");

        // Check if messageText is missing, null, or just whitespace
        if (newText == null || newText.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // If you want, you can also check length here (the service checks it too).
        // if (newText.length() > 255) {
        //    return ResponseEntity.badRequest().build();
        // }

        // Attempt the update; if Optional is empty, return 400
        return messageService.updateMessage(messageId, newText)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages);
    }
}
