package tech.taiiku.controlapi.dto;

public record SeleniumNodeStatus(
        String browser,
        String availability,
        int totalSlots,
        int activeSessions,
        int freeSlots
) {
}