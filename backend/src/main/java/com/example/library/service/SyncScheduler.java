package com.example.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SyncScheduler.class);
    private final SyncService syncService;

    public SyncScheduler(SyncService syncService) {
        this.syncService = syncService;
    }

    @Scheduled(fixedRateString = "${sync.interval-ms}", initialDelayString = "${sync.initial-delay-ms}")
    public void scheduledSync() {
        try {
            syncService.runSync("SCHEDULED");
        } catch (SyncAlreadyRunningException e) {
            log.info("Scheduled sync skipped: another sync is still running.");
        } catch (Exception e) {
            log.error("Scheduled sync crashed", e);
        }
    }
}