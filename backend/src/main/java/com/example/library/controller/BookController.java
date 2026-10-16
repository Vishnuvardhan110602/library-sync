package com.example.library.controller;

import com.example.library.dto.BookDto;
import com.example.library.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> list(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String status) {
        return bookService.findBooks(search, status);
    }
}