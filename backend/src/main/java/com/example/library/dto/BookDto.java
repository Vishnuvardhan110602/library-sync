package com.example.library.dto;

import com.example.library.entity.Book;
import com.example.library.entity.SyncStatus;
import java.time.LocalDateTime;

public record BookDto(Long id, String externalId, String title, String authors,
                      Integer firstPublishYear, SyncStatus syncStatus,
                      LocalDateTime lastCheckedAt, LocalDateTime lastChangedAt) {

    public static BookDto from(Book b) {
        return new BookDto(b.getId(), b.getExternalId(), b.getTitle(), b.getAuthors(),
                b.getFirstPublishYear(), b.getSyncStatus(), b.getLastCheckedAt(), b.getLastChangedAt());
    }
}