package com.example.Chat_Bot_Lang4J.controller;

import com.example.Chat_Bot_Lang4J.model.AddDocumentRequest;
import com.example.Chat_Bot_Lang4J.model.SearchRequest;
import com.example.Chat_Bot_Lang4J.Data.Vector.VectorService;

import dev.langchain4j.data.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/vector-store")
@CrossOrigin(origins = "*")
public class VectorStoreController {

    @Autowired
    private VectorService vectorStoreService;

    /**
     * Add document to vector store
     * POST /api/vector-store/documents
     */
    @PostMapping("/documents")
    public ResponseEntity<Map<String, Object>> addDocument(@RequestBody AddDocumentRequest request) {
        try {
            vectorStoreService.addDocument(request.getText(), request.getMetadata());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Document added successfully");
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add document: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Search documents in vector store
     * GET /api/vector-store/search?query=...&topK=...
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        try {
            List<Document> documents = vectorStoreService.search(query, topK);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("query", query);
            response.put("topK", topK);
            response.put("resultCount", documents.size());
            response.put("documents", documents);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Search failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            response.put("query", query);

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Search with detailed results (including similarity scores)
     * POST /api/vector-store/search-detailed
     */
    @PostMapping("/search-detailed")
    public ResponseEntity<Map<String, Object>> searchDetailed(@RequestBody SearchRequest request) {
        try {
            // Assuming you have a method that returns results with scores
            List<Document> documents = vectorStoreService.search(
                    request.getQuery(),
                    request.getTopK() != null ? request.getTopK() : 5
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("query", request.getQuery());
            response.put("topK", request.getTopK());
            response.put("minScore", request.getMinScore());
            response.put("resultCount", documents.size());
            response.put("documents", documents);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Detailed search failed: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(500).body(response);
        }
    }

//    /**
//     * Add multiple documents at once
//     * POST /api/vector-store/documents/batch
//     */
//    @PostMapping("/documents/batch")
//    public ResponseEntity<Map<String, Object>> addDocumentsBatch(@RequestBody BatchAddRequest request) {
//        try {
//            int addedCount = 0;
//            for (AddDocumentRequest docRequest : request.getDocuments()) {
//                vectorStoreService.addDocument(docRequest.getText(), docRequest.getMetadata());
//                addedCount++;
//            }
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Batch documents added successfully");
//            response.put("totalAdded", addedCount);
//            response.put("timestamp", System.currentTimeMillis());
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", "Batch add failed: " + e.getMessage());
//            response.put("error", e.getClass().getSimpleName());
//
//            return ResponseEntity.status(500).body(response);
//        }
//    }


}
