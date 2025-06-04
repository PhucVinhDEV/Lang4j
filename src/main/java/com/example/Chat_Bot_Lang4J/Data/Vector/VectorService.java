package com.example.Chat_Bot_Lang4J.Data.Vector;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface VectorService {
    void saveProduct(Product product);
    void saveCategory(Categories category);
    List<Document>  search(String query, int topK);


    // Document management methods
    void addDocument(String text, Map<String, String> metadata);
    void addDocuments(List<Document> documents);

    int addFile(MultipartFile file, Map<String, String> metadata);
    int addFile(String filePath, Map<String, String> metadata);
    int addFile(File file, Map<String, String> metadata);


    // Web content methods
    /**
     * Add content from web URL
     * @param url Web page URL
     * @param metadata Additional metadata
     * @return Number of text segments created
     */
    int addWebContent(String url, Map<String, String> metadata) throws IOException;

    /**
     * Add content from multiple URLs
     * @param urls List of web page URLs
     * @param commonMetadata Common metadata for all URLs
     * @return Total number of text segments created
     */
    int addWebContents(List<String> urls, Map<String, String> commonMetadata) throws IOException;

    // CSV/Excel methods
    /**
     * Add CSV file as searchable content
     * @param csvFile CSV file
     * @param metadata Additional metadata
     * @param textColumns Columns to use for text content
     * @return Number of text segments created
     */
    int addCsvFile(MultipartFile csvFile, Map<String, String> metadata, List<String> textColumns) throws IOException;

    /**
     * Add Excel file as searchable content
     * @param excelFile Excel file
     * @param metadata Additional metadata
     * @param sheetName Sheet name to process (null = first sheet)
     * @param textColumns Columns to use for text content
     * @return Number of text segments created
     */
    int addExcelFile(MultipartFile excelFile, Map<String, String> metadata, String sheetName, List<String> textColumns) throws IOException;

    // Directory processing methods
    /**
     * Add all files from a directory
     * @param directoryPath Path to directory
     * @param recursive Whether to process subdirectories
     * @param fileExtensions Allowed file extensions (null = all supported)
     * @param commonMetadata Common metadata for all files
     * @return Total number of text segments created
     */
    int addDirectory(String directoryPath, boolean recursive, List<String> fileExtensions, Map<String, String> commonMetadata) throws IOException;

    // Advanced search methods
    /**
     * Search with similarity scores
     * @param query Search query
     * @param topK Number of results
     * @param minScore Minimum similarity score
     * @return List of documents with scores
     */
    List<DocumentWithScore> searchWithScores(String query, int topK, double minScore);

    /**
     * Search with metadata filtering
     * @param query Search query
     * @param topK Number of results
     * @param metadataFilter Metadata filters
     * @return List of matching documents
     */
    List<Document> searchWithFilter(String query, int topK, Map<String, String> metadataFilter);

    /**
     * Semantic search across specific document types
     * @param query Search query
     * @param topK Number of results
     * @param documentTypes Types of documents to search (e.g., "pdf", "product", "category")
     * @return List of matching documents
     */
    List<Document> searchByDocumentType(String query, int topK, List<String> documentTypes);

    // Utility methods
    /**
     * Get supported file extensions
     * @return List of supported file extensions
     */
    List<String> getSupportedFileExtensions();

    /**
     * Validate file before processing
     * @param file File to validate
     * @return True if file is supported and valid
     */
    boolean validateFile(MultipartFile file);

    /**
     * Get document count in vector store
     * @return Total number of documents
     */
    long getDocumentCount();

    /**
     * Clear all documents from vector store
     */
    void clearAllDocuments();

    /**
     * Delete documents by metadata filter
     * @param metadataFilter Filter criteria for deletion
     * @return Number of documents deleted
     */
    int deleteDocumentsByFilter(Map<String, String> metadataFilter);

    // Helper class for search results with scores
    class DocumentWithScore {
        private final Document document;
        private final double score;

        public DocumentWithScore(Document document, double score) {
            this.document = document;
            this.score = score;
        }

        public Document getDocument() { return document; }
        public double getScore() { return score; }
        public String getText() { return document.text(); }
        public Map<String, Object> getMetadata() { return document.metadata().toMap(); }
    }
}

