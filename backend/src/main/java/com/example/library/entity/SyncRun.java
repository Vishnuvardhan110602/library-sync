package com.example.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "sync_runs")
public class SyncRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "triggered_by", nullable = false, length = 20)
    private String triggeredBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RunStatus status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "fetched_count", nullable = false)
    private int fetchedCount;

    @Column(name = "new_count", nullable = false)
    private int newCount;

    @Column(name = "updated_count", nullable = false)
    private int updatedCount;

    @Column(name = "unchanged_count", nullable = false)
    private int unchangedCount;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    protected SyncRun() { }

    public SyncRun(String triggeredBy) {
        this.triggeredBy = triggeredBy;
        this.status = RunStatus.RUNNING;
        this.startedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void markSuccess(int fetched, int created, int updated, int unchanged) {
        this.status = RunStatus.SUCCESS;
        this.fetchedCount = fetched;
        this.newCount = created;
        this.updatedCount = updated;
        this.unchangedCount = unchanged;
        this.finishedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void markFailed(String message) {
        this.status = RunStatus.FAILED;
        this.errorMessage = message != null && message.length() > 1000 ? message.substring(0, 1000) : message;
        this.finishedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public String getTriggeredBy() { return triggeredBy; }
    public RunStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public int getFetchedCount() { return fetchedCount; }
    public int getNewCount() { return newCount; }
    public int getUpdatedCount() { return updatedCount; }
    public int getUnchangedCount() { return unchangedCount; }
    public String getErrorMessage() { return errorMessage; }
}