package com.example.library.service;

import com.example.library.dto.BookDto;
import com.example.library.dto.StatsDto;
import com.example.library.dto.SyncRunDto;
import com.example.library.entity.Book;
import com.example.library.entity.SyncStatus;
import com.example.library.repository.BookRepository;
import com.example.library.repository.SyncRunRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final SyncRunRepository syncRunRepository;
    private final SyncService syncService;

    public BookService(BookRepository bookRepository, SyncRunRepository syncRunRepository, SyncService syncService) {
        this.bookRepository = bookRepository;
        this.syncRunRepository = syncRunRepository;
        this.syncService = syncService;
    }

    public List<BookDto> findBooks(String search, String status) {
        Specification<Book> spec = (root, query, cb) -> cb.conjunction();

        if (status != null && !status.isBlank()) {
            SyncStatus parsed;
            try {
                parsed = SyncStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid status '" + status + "'. Use NEW, UPDATED or UNCHANGED.");
            }
            spec = spec.and((root, query, cb) -> cb.equal(root.get("syncStatus"), parsed));
        }
        if (search != null && !search.isBlank()) {
            String like = "%" + search.trim().toLowerCase() + "%";   // sent as a bound parameter (safe)
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("authors")), like)));
        }
        return bookRepository.findAll(spec, PageRequest.of(0, 500, Sort.by("title").ascending()))
                .getContent().stream().map(BookDto::from).toList();
    }

    public List<SyncRunDto> recentRuns(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        return syncRunRepository.findAll(PageRequest.of(0, safeLimit, Sort.by("startedAt").descending()))
                .getContent().stream().map(SyncRunDto::from).toList();
    }

    /** Totals always come from the database, never from the filtered list. */
    public StatsDto stats() {
        return new StatsDto(
                bookRepository.count(),
                bookRepository.countBySyncStatus(SyncStatus.NEW),
                bookRepository.countBySyncStatus(SyncStatus.UPDATED),
                bookRepository.countBySyncStatus(SyncStatus.UNCHANGED),
                syncService.isRunning());
    }
}