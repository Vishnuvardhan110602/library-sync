package com.example.library.dto;

import com.example.library.entity.RunStatus;
import com.example.library.entity.SyncRun;
import java.time.LocalDateTime;

public record SyncRunDto(Long id, String triggeredBy, RunStatus status,
                         LocalDateTime startedAt, LocalDateTime finishedAt,
                         int fetchedCount, int newCount, int updatedCount, int unchangedCount,
                         String errorMessage) {

    public static SyncRunDto from(SyncRun r) {
        return new SyncRunDto(r.getId(), r.getTriggeredBy(), r.getStatus(), r.getStartedAt(),
                r.getFinishedAt(), r.getFetchedCount(), r.getNewCount(), r.getUpdatedCount(),
                r.getUnchangedCount(), r.getErrorMessage());
    }
}