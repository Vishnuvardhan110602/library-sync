package com.example.library.controller;

import com.example.library.dto.SyncRunDto;
import com.example.library.service.BookService;
import com.example.library.service.SyncService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SyncController {

    private final SyncService syncService;
    private final BookService bookService;

    public SyncController(SyncService syncService, BookService bookService) {
        this.syncService = syncService;
        this.bookService = bookService;
    }

    @PostMapping("/sync")
    public SyncRunDto syncNow() {
        return syncService.runSync("MANUAL");
    }

    @GetMapping("/sync-runs")
    public List<SyncRunDto> history(@RequestParam(defaultValue = "10") int limit) {
        return bookService.recentRuns(limit);
    }
}