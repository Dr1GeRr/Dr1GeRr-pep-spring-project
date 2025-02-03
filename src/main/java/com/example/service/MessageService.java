package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public Optional<Message> createMessage(Message message) {
        // Ensure message text is not blank or too long, and the user exists
        if (message.getMessageText() == null || message.getMessageText().isBlank() || 
            message.getMessageText().length() > 255) {
            return Optional.empty(); // Return empty if the message text is invalid
        }

        if (message.getPostedBy() == null || !accountRepository.existsById(message.getPostedBy())) {
            return Optional.empty(); // Return empty if the user is invalid
        }

        // If all checks are valid, save the message
        return Optional.of(messageRepository.save(message));
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    // Return false if message does not exist, otherwise delete
    public boolean deleteMessage(Integer messageId) {
        if (!messageRepository.existsById(messageId)) {
            return false; // Return false if message not found
        }
        messageRepository.deleteById(messageId);
        return true; // Return true if deletion was successful
    }

    // Check for empty or blank message text and limit it to 255 characters
    public Optional<Integer> updateMessage(Integer messageId, String newText) {
        // If the new text is invalid (empty or blank or too long), reject it
        if (newText == null || newText.trim().isEmpty() || newText.length() > 255) {
            return Optional.empty(); // Reject invalid message text
        }

        // Find the message and update its text if found
        return messageRepository.findById(messageId).map(message -> {
            message.setMessageText(newText);
            messageRepository.save(message);
            return 1; // Return 1 row modified
        });
    }

    public List<Message> getMessagesByAccountId(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}
