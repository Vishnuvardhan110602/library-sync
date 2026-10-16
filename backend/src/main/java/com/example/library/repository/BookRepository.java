package com.example.library.repository;

import com.example.library.entity.Book;
import com.example.library.entity.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByExternalIdIn(Collection<String> externalIds);

    long countBySyncStatus(SyncStatus status);
}