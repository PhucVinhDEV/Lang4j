package com.example.Chat_Bot_Lang4J.Data.Vector;

import com.example.Chat_Bot_Lang4J.Data.Entity.Categories;
import com.example.Chat_Bot_Lang4J.Data.Entity.Product;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Service
public class VectorServiceImpl implements VectorService{


    private final ChromaEmbeddingStore chromaEmbeddingStore;
    private final EmbeddingModel embeddingModel;

    // ✅ FIX: Add @Autowired annotation
    @Autowired
    public VectorServiceImpl(ChromaEmbeddingStore chromaEmbeddingStore, EmbeddingModel embeddingModel) {
        this.chromaEmbeddingStore = chromaEmbeddingStore;
        this.embeddingModel = embeddingModel;
        log.info("✅ VectorServiceImpl initialized successfully");
    }


    @Override
    public void saveProduct(Product product) {
        String content = String.format("""
            Product ID: %d
            Name: %s
            Description: %s
            Price: %.2f
            Stock Quantity: %d
            Category: %s
            """,
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory() != null ? product.getCategory().getName() : "Unknown"
        );
        // 🔥 TẠO EMBEDDING VECTOR từ embedding model
        Embedding embedding = embeddingModel.embed(content).content();

        chromaEmbeddingStore.add(embedding);
    }

    @Override
    public void saveCategory(Categories category) {
        String content = String.format("""
            Category ID: %d
            Name: %s
            Description: %s
            """,
                category.getId(),
                category.getName(),
                category.getDescription()
        );

        Embedding embedding = embeddingModel.embed(content).content();

        chromaEmbeddingStore.add(embedding);
    }


    @Override
    public List<Document> search(String query, int topK) {
        // 🔥 TẠO EMBEDDING cho search query
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // Option 1: Using constructor with parameters
        EmbeddingSearchRequest searchRequest = new EmbeddingSearchRequest(
                queryEmbedding,  // query embedding
                topK,           // max results
                0.7,            // min score
                null            // filter (if needed, otherwise null)
        );


        // Execute search and get results
        EmbeddingSearchResult<TextSegment> searchResult = chromaEmbeddingStore.search(searchRequest);

        return searchResult.matches().stream()
                .map(match -> {
                    TextSegment segment = match.embedded();
                    return Document.from(segment.text(), segment.metadata());
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addDocument(String text, Map<String, String> metadata) {

    }

    @Override
    public void addDocuments(List<Document> documents) {

    }

    @Override
    public int addFile(MultipartFile file, Map<String, String> metadata)  {
        return 0;
    }

    @Override
    public int addFile(String filePath, Map<String, String> metadata)  {
        return 0;
    }

    @Override
    public int addFile(File file, Map<String, String> metadata) {
        return 0;
    }

    private int addFiles(List<MultipartFile> files, Map<String, String> commonMetadata) throws IOException {
        return 0;
    }


    private int addPdfFile(MultipartFile pdfFile, Map<String, String> metadata, boolean splitByPages) throws IOException {
        return 0;
    }


    private int addPdfFromUrl(String pdfUrl, Map<String, String> metadata) throws IOException {
        return 0;
    }


    private int addTextFile(MultipartFile textFile, Map<String, String> metadata, int chunkSize) throws IOException {
        return 0;
    }

    private int addWordFile(MultipartFile wordFile, Map<String, String> metadata) throws IOException {
        return 0;
    }

    @Override
    public int addWebContent(String url, Map<String, String> metadata) throws IOException {
        return 0;
    }

    @Override
    public int addWebContents(List<String> urls, Map<String, String> commonMetadata) throws IOException {
        return 0;
    }

    @Override
    public int addCsvFile(MultipartFile csvFile, Map<String, String> metadata, List<String> textColumns) throws IOException {
        return 0;
    }

    @Override
    public int addExcelFile(MultipartFile excelFile, Map<String, String> metadata, String sheetName, List<String> textColumns) throws IOException {
        return 0;
    }

    @Override
    public int addDirectory(String directoryPath, boolean recursive, List<String> fileExtensions, Map<String, String> commonMetadata) throws IOException {
        return 0;
    }

    @Override
    public List<DocumentWithScore> searchWithScores(String query, int topK, double minScore) {
        return List.of();
    }

    @Override
    public List<Document> searchWithFilter(String query, int topK, Map<String, String> metadataFilter) {
        return List.of();
    }

    @Override
    public List<Document> searchByDocumentType(String query, int topK, List<String> documentTypes) {
        return List.of();
    }

    @Override
    public List<String> getSupportedFileExtensions() {
        return List.of();
    }

    @Override
    public boolean validateFile(MultipartFile file) {
        return false;
    }

    @Override
    public long getDocumentCount() {
        return 0;
    }

    @Override
    public void clearAllDocuments() {

    }

    @Override
    public int deleteDocumentsByFilter(Map<String, String> metadataFilter) {
        return 0;
    }


}
