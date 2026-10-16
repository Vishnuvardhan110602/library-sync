package com.example.library.controller;

import com.example.library.dto.StatsDto;
import com.example.library.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final BookService bookService;

    public StatsController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public StatsDto stats() {
        return bookService.stats();
    }
}