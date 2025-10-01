package com.docflow.repository;

import com.docflow.model.Document;
import com.docflow.model.Document.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    
    List<Document> findByStatus(DocumentStatus status);
    
    List<Document> findByOrderByCreatedAtDesc();
    
    List<Document> findByStatusOrderByCreatedAtDesc(DocumentStatus status);
}
