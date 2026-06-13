package com.example.mediai.data.repository;

import com.example.mediai.data.local.dao.ChatMessageDao;
import com.example.mediai.data.model.ChatMessage;
import com.example.mediai.util.Constants;
import com.example.mediai.util.DateTimeUtils;

import java.util.List;

public class AiRepository {
    private ChatMessageDao chatMessageDao;

    public AiRepository(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }

    public long saveMessage(long userId, String role, String content, String provider) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(DateTimeUtils.getCurrentTimestamp());
        message.setProvider(provider != null ? provider : Constants.AI_PROVIDER);
        return chatMessageDao.insert(message);
    }

    public List<ChatMessage> getChatHistory(long userId) {
        return chatMessageDao.findByUserId(userId);
    }

    public List<ChatMessage> getRecentChatHistory(long userId, int limit) {
        return chatMessageDao.findRecentByUserId(userId, limit);
    }

    public void clearChatHistory(long userId) {
        chatMessageDao.deleteByUserId(userId);
    }
}