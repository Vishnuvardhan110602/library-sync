package com.example.library.service;

import com.example.library.client.OpenLibraryClient;
import com.example.library.dto.ExternalBook;
import com.example.library.dto.SyncRunDto;
import com.example.library.entity.Book;
import com.example.library.entity.SyncRun;
import com.example.library.entity.SyncStatus;
import com.example.library.repository.BookRepository;
import com.example.library.repository.SyncRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    // true while a sync is in progress - this prevents overlapping syncs
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final BookRepository bookRepository;
    private final SyncRunRepository syncRunRepository;
    private final OpenLibraryClient client;
    private final TransactionTemplate transactionTemplate;

    public SyncService(BookRepository bookRepository, SyncRunRepository syncRunRepository,
                       OpenLibraryClient client, PlatformTransactionManager txManager) {
        this.bookRepository = bookRepository;
        this.syncRunRepository = syncRunRepository;
        this.client = client;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    public boolean isRunning() {
        return running.get();
    }

    /** Runs one synchronization. Throws SyncAlreadyRunningException if another one is active. */
    public SyncRunDto runSync(String triggeredBy) {
        if (!running.compareAndSet(false, true)) {
            throw new SyncAlreadyRunningException();
        }
        try {
            SyncRun run = syncRunRepository.save(new SyncRun(triggeredBy));
            try {
                // 1) Call the external API OUTSIDE a transaction (it can be slow)
                List<ExternalBook> fetched = client.fetchBooks();

                // 2) Save all changes in ONE transaction: all succeed or all roll back
                int[] c = transactionTemplate.execute(status -> applyChanges(fetched));

                run.markSuccess(fetched.size(), c[0], c[1], c[2]);
                log.info("Sync finished: fetched={}, new={}, updated={}, unchanged={}",
                        fetched.size(), c[0], c[1], c[2]);
            } catch (Exception e) {
                log.error("Sync failed", e);
                run.markFailed(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            }
            return SyncRunDto.from(syncRunRepository.save(run));
        } finally {
            running.set(false); // always release the lock
        }
    }

    /** Compares each fetched book with the database. Returns {new, updated, unchanged}. */
    private int[] applyChanges(List<ExternalBook> fetched) {
        // Remove duplicate ids inside the API response
        Map<String, ExternalBook> unique = new LinkedHashMap<>();
        for (ExternalBook b : fetched) {
            unique.putIfAbsent(b.externalId(), b);
        }

        // One query for all existing books (avoids one query per book)
        Map<String, Book> existing = bookRepository.findByExternalIdIn(unique.keySet()).stream()
                .collect(Collectors.toMap(Book::getExternalId, Function.identity()));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<Book> toSave = new ArrayList<>();
        int created = 0, updated = 0, unchanged = 0;

        for (ExternalBook ext : unique.values()) {
            String hash = HashUtil.bookHash(ext.title(), ext.authors(), ext.firstPublishYear());
            Book book = existing.get(ext.externalId());

            if (book == null) {                                   // NEW
                book = new Book(ext.externalId(), now);
                book.applyData(ext.title(), ext.authors(), ext.firstPublishYear(), hash);
                book.setSyncStatus(SyncStatus.NEW);
                created++;
            } else if (!book.getDataHash().equals(hash)) {        // UPDATED
                book.applyData(ext.title(), ext.authors(), ext.firstPublishYear(), hash);
                book.setSyncStatus(SyncStatus.UPDATED);
                book.setLastChangedAt(now);
                updated++;
            } else {                                              // UNCHANGED
                book.setSyncStatus(SyncStatus.UNCHANGED);
                unchanged++;
            }
            book.setLastCheckedAt(now);
            toSave.add(book);
        }
        bookRepository.saveAll(toSave);
        return new int[]{created, updated, unchanged};
    }
}