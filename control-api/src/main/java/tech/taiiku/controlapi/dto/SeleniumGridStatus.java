package tech.taiiku.controlapi.dto;

import java.util.List;

public record SeleniumGridStatus(
        boolean ready,
        String message,
        int totalNodes,
        int totalSlots,
        int activeSessions,
        int freeSlots,
        List<SeleniumNodeStatus> nodes
) {
}