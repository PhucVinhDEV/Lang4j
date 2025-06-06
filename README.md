# Chat Bot Lang4J

<div align="center">

**Intelligent Chatbot with RAG and Text-to-SQL Capabilities**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![LangChain4j](https://img.shields.io/badge/LangChain4j-1.0.0-FF6B35?style=flat-square&logo=chainlink&logoColor=white)](https://github.com/langchain4j/langchain4j)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)

</div>

---

## Giới thiệu

Dự án chatbot được xây dựng với **Spring Boot** và **LangChain4j**, triển khai hệ thống **Retrieval-Augmented Generation (RAG)** kết hợp với **Agent Text-to-SQL**. Chatbot có khả năng:

- **Text-to-SQL**: Truy vấn trực tiếp PostgreSQL thông qua chuyển đổi ngôn ngữ tự nhiên thành SQL
- **RAG Search**: Tìm kiếm ngữ nghĩa trong ChromaDB để trả lời câu hỏi mô tả và gợi ý

---

## Kiến trúc

```mermaid
graph TB
    A[User Query] --> B[Gemini Agent]
    
    B --> C{Query Analysis}
    
    C -->|Structured Data<br/>Price, Stock, Count| D[Text-to-SQL]
    C -->|Semantic Search<br/>Recommendations, Descriptions| E[RAG Pipeline]
    
    D --> F[Gemini Model]
    F --> G[SQL Query Generation]
    G --> H[PostgreSQL]
    H --> I[Structured Data]
    
    E --> J[Embedding Model<br/>text-multilingual-embedding-002]
    J --> K[ChromaDB Vector Search]
    K --> L[Relevant Documents]
    
    I --> M[Gemini Response Generator]
    L --> M
    
    M --> N[Natural Language Response]
    
    style A fill:#e1f5fe
    style N fill:#e8f5e8
    style C fill:#fff3e0
    style D fill:#fce4ec
    style E fill:#f3e5f5
    style M fill:#e0f2f1
```

---

## Luồng hoạt động

### 1. Data Ingestion (RAG Pipeline)

```
PostgreSQL → Embedding Model → ChromaDB Vector Store
```

1. **Đọc dữ liệu**: Lấy thông tin sản phẩm từ PostgreSQL
2. **Tạo Embeddings**: Sử dụng `text-multilingual-embedding-002` để vector hóa
3. **Lưu trữ Vector**: Lưu vào ChromaDB cho semantic search

### 2. Query Processing

```
User Question → Query Analysis → Tool Selection → Data Retrieval → Response Generation
```

| Tool | Khi nào sử dụng | Ví dụ |
|------|----------------|-------|
| **Text-to-SQL** | Dữ liệu có cấu trúc, số liệu | "Giá iPhone 15 là bao nhiêu?" |
| **RAG Search** | Câu hỏi ngữ nghĩa, gợi ý | "Smartphone nào phù hợp cho sinh viên?" |

### 3. RAG Customization

- **Top K**: Số lượng documents truy xuất
- **Minimum Score**: Ngưỡng similarity threshold
- **Re-ranking**: Cross-encoder để cải thiện độ chính xác

---

## Tính năng chính

- **Agent thông minh**: Tự động lựa chọn công cụ phù hợp (Text-to-SQL, RAG)
- **Text-to-SQL**: Chuyển đổi ngôn ngữ tự nhiên thành SQL queries
- **RAG**: Semantic search trên knowledge base được vector hóa
- **Multi-model**: Tích hợp Gemini + Embedding models

---

## Tech Stack

<table>
<tr>
<td><strong>Backend</strong></td>
<td>Spring Boot 3.4.5, Java 17, Maven</td>
</tr>
<tr>
<td><strong>AI/LLM</strong></td>
<td>LangChain4j 1.0.0, Google Gemini (Vertex AI), text-multilingual-embedding-002</td>
</tr>
<tr>
<td><strong>Database</strong></td>
<td>PostgreSQL, ChromaDB (Testcontainers)</td>
</tr>
<tr>
<td><strong>Tools</strong></td>
<td>Docker, Lombok</td>
</tr>
</table>

---

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| **Java JDK** | 17+ | [Download](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) |
| **Maven** | 3.8+ | [Download](https://maven.apache.org/download.cgi) |
| **Docker** | Latest | [Download](https://www.docker.com/products/docker-desktop/) |
| **PostgreSQL** | 13+ | [Download](https://www.postgresql.org/download/) |

---

## Configuration

### 1. PostgreSQL Database

Cấu hình trong `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# JPA Settings
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 2. ChromaDB (Vector Database)

ChromaDB được quản lý tự động thông qua **Testcontainers**. Chỉ cần đảm bảo Docker đang chạy.

### 3. Google Vertex AI Setup

#### 🔧 **Bước 1: Tạo Google Cloud Project**

```bash
# Tạo project mới
gcloud projects create your-project-id --name="Chatbot Project"

# Set project làm default
gcloud config set project your-project-id

# Kiểm tra project hiện tại
gcloud config get-value project
```

#### 🔧 **Bước 2: Enable Required APIs**

```bash
# Enable Vertex AI API
gcloud services enable aiplatform.googleapis.com

# Enable Compute Engine API (required)
gcloud services enable compute.googleapis.com

# Kiểm tra APIs đã enable
gcloud services list --enabled --filter="aiplatform.googleapis.com OR compute.googleapis.com"
```

#### 🔧 **Bước 3: Authentication Setup**

**Option A: Application Default Credentials (Recommended for Development)**

```bash
# Login với user account
gcloud auth application-default login

# Verify authentication
gcloud auth application-default print-access-token
```

**Option B: Service Account (Recommended for Production)**

```bash
# Tạo service account
gcloud iam service-accounts create chatbot-service-account \
    --description="Service account for chatbot application" \
    --display-name="Chatbot Service Account"

# Gán quyền Vertex AI User
gcloud projects add-iam-policy-binding your-project-id \
    --member="serviceAccount:chatbot-service-account@your-project-id.iam.gserviceaccount.com" \
    --role="roles/aiplatform.user"

# Tạo và download key file
gcloud iam service-accounts keys create ~/chatbot-service-key.json \
    --iam-account=chatbot-service-account@your-project-id.iam.gserviceaccount.com

# Set environment variable
export GOOGLE_APPLICATION_CREDENTIALS="$HOME/chatbot-service-key.json"



#### 📊 **Vertex AI Pricing** (Reference)

| Model | Input (per 1K tokens) | Output (per 1K tokens) |
|-------|----------------------|------------------------|
| Gemini Pro | $0.000125 | $0.000375 |
| text-multilingual-embedding-002 | $0.0001 | - |





## Troubleshooting

### Common Issues

**🔴 Vertex AI Authentication Error**
```bash
# Re-authenticate
gcloud auth application-default revoke
gcloud auth application-default login
```

**🔴 Project ID Not Found**
```bash
# Verify project exists and is accessible
gcloud projects describe your-project-id
```

**🔴 API Not Enabled**
```bash
# Check enabled APIs
gcloud services list --enabled --filter="aiplatform"
```

---

## License

MIT License - see [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with ❤️ using Spring Boot, LangChain4j, and Google Vertex AI**

[Documentation](docs/) • [Issues](https://github.com/your-repo/issues) • [Contributing](CONTRIBUTING.md)

</div>
