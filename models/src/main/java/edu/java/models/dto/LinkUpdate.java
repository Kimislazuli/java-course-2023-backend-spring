package edu.java.models.dto;

public record LinkUpdate(Long id, String url, String description, Long[] tgChatIds) {
}
