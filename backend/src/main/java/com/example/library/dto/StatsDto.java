package com.example.library.dto;

public record StatsDto(long total, long newCount, long updatedCount, long unchangedCount, boolean syncRunning) {
}