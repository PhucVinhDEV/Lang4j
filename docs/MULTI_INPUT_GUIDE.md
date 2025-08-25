# 🚀 Multi Input Guide - Chat Bot Lang4J

## 📋 Tổng quan

Tính năng **Multi Input** cho phép bạn upload và xử lý nhiều loại file khác nhau vào VectorStore để tăng cường khả năng tìm kiếm ngữ nghĩa của chatbot.

## 🎯 Các loại file được hỗ trợ

### ✅ **Document Files**

- **PDF** (`.pdf`) - Trích xuất text từ PDF documents
- **Word** (`.docx`, `.doc`) - Xử lý Microsoft Word documents
- **Text** (`.txt`) - Plain text files
- **HTML** (`.html`, `.htm`) - Web pages và HTML content

### ✅ **Data Files**

- **Excel** (`.xlsx`, `.xls`) - Spreadsheets với column selection
- **CSV** (`.csv`) - Comma-separated values với column specification

### ✅ **Image Files** (OCR)

- **JPG/JPEG** (`.jpg`, `.jpeg`)
- **PNG** (`.png`)
- **BMP** (`.bmp`)
- **TIFF** (`.tiff`)

### ✅ **Web Content**

- **URL Scraping** - Trích xuất content từ web pages
- **Batch URL Processing** - Xử lý nhiều URLs cùng lúc

---

## 🔌 API Endpoints

### 1. **File Upload - General**

```http
POST /api/vector-store/upload
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required)
- metadata: Map<String,String> (optional)
```

**Example:**

```bash
curl -X POST "http://localhost:8080/api/vector-store/upload" \
  -F "file=@document.pdf" \
  -F "metadata[category]=research" \
  -F "metadata[source]=academic"
```

### 2. **CSV Upload với Column Selection**

```http
POST /api/vector-store/upload/csv
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required)
- textColumns: List<String> (default: "0,1,2")
- metadata: Map<String,String> (optional)
```

**Example:**

```bash
curl -X POST "http://localhost:8080/api/vector-store/upload/csv" \
  -F "file=@products.csv" \
  -F "textColumns=0,2,3" \
  -F "metadata[type]=product_catalog"
```

### 3. **Excel Upload với Sheet & Column Selection**

```http
POST /api/vector-store/upload/excel
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required)
- sheetName: String (optional, default: first sheet)
- textColumns: List<String> (default: "A,B,C")
- metadata: Map<String,String> (optional)
```

**Example:**

```bash
curl -X POST "http://localhost:8080/api/vector-store/upload/excel" \
  -F "file=@inventory.xlsx" \
  -F "sheetName=Products" \
  -F "textColumns=A,B,D,E" \
  -F "metadata[department]=inventory"
```

### 4. **Web Content Scraping**

```http
POST /api/vector-store/web-content
Content-Type: application/x-www-form-urlencoded

Parameters:
- url: String (required)
- metadata: Map<String,String> (optional)
```

**Example:**

```bash
curl -X POST "http://localhost:8080/api/vector-store/web-content" \
  -d "url=https://example.com/article" \
  -d "metadata[source]=website" \
  -d "metadata[type]=article"
```

### 5. **Batch Web Content**

```http
POST /api/vector-store/web-content/batch
Content-Type: application/x-www-form-urlencoded

Parameters:
- urls: List<String> (required)
- metadata: Map<String,String> (optional)
```

**Example:**

```bash
curl -X POST "http://localhost:8080/api/vector-store/web-content/batch" \
  -d "urls=https://site1.com,https://site2.com" \
  -d "metadata[batch]=news_articles"
```

---

## 🔍 Advanced Search

### 1. **Search với Similarity Scores**

```http
GET /api/vector-store/search-with-scores?query=laptop&topK=5&minScore=0.8
```

### 2. **Search với Metadata Filtering**

```http
POST /api/vector-store/search-with-filter
Content-Type: application/json

{
  "query": "sản phẩm điện tử",
  "topK": 10,
  "metadataFilter": {
    "file_type": "csv",
    "category": "electronics"
  }
}
```

### 3. **Search theo Document Type**

```http
GET /api/vector-store/search-by-type?query=laptop&topK=5&documentTypes=pdf,xlsx
```

---

## 🛠️ Utility Endpoints

### 1. **Kiểm tra File Types được hỗ trợ**

```http
GET /api/vector-store/supported-types
```

### 2. **Validate File trước khi Upload**

```http
POST /api/vector-store/validate
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required)
```

### 3. **Thống kê VectorStore**

```http
GET /api/vector-store/stats
```

### 4. **Clear All Documents** (⚠️ Cẩn thận)

```http
DELETE /api/vector-store/clear-all
```

---

## 📝 Response Format

Tất cả endpoints đều trả về JSON response với format chuẩn:

```json
{
  "success": true,
  "message": "File processed successfully",
  "fileName": "document.pdf",
  "fileSize": 1024000,
  "segmentsCreated": 15,
  "timestamp": 1703123456789
}
```

### Error Response:

```json
{
  "success": false,
  "message": "Failed to process file: Unsupported file type",
  "error": "IllegalArgumentException",
  "fileName": "document.txt",
  "timestamp": 1703123456789
}
```

---

## 💡 Use Cases

### 1. **Knowledge Base từ PDF Documents**

```bash
# Upload research papers
curl -X POST "http://localhost:8080/api/vector-store/upload" \
  -F "file=@research_paper_1.pdf" \
  -F "metadata[category]=research"

# Search trong knowledge base
curl "http://localhost:8080/api/vector-store/search?query=machine learning algorithms&topK=3"
```

### 2. **Product Catalog từ Excel**

```bash
# Upload product catalog
curl -X POST "http://localhost:8080/api/vector-store/upload/excel" \
  -F "file=@products.xlsx" \
  -F "sheetName=Catalog" \
  -F "textColumns=B,C,D" \
  -F "metadata[type]=product_catalog"

# Search products với filter
curl -X POST "http://localhost:8080/api/vector-store/search-with-filter" \
  -H "Content-Type: application/json" \
  -d '{"query":"smartphone","topK":5,"metadataFilter":{"type":"product_catalog"}}'
```

### 3. **News Articles từ Web**

```bash
# Scrape news articles
curl -X POST "http://localhost:8080/api/vector-store/web-content/batch" \
  -d "urls=https://news1.com,https://news2.com" \
  -d "metadata[category]=news"

# Search trong news với scores
curl "http://localhost:8080/api/vector-store/search-with-scores?query=technology&topK=5&minScore=0.7"
```

### 4. **Image OCR Processing**

```bash
# Upload image với OCR
curl -X POST "http://localhost:8080/api/vector-store/upload" \
  -F "file=@invoice.jpg" \
  -F "metadata[type]=invoice" \
  -F "metadata[ocr]=true"
```

---

## ⚙️ Configuration

### Dependencies đã được thêm vào `pom.xml`:

- **Apache PDFBox** - PDF processing
- **Apache POI** - Excel/Word processing
- **OpenCSV** - CSV processing
- **Tesseract4J** - OCR cho images
- **JSoup** - Web scraping

### Tesseract OCR Setup (Optional)

Để sử dụng OCR cho images, cần cài đặt Tesseract:

**Windows:**

```bash
# Download từ: https://github.com/tesseract-ocr/tesseract
# Hoặc dùng chocolatey:
choco install tesseract
```

**Linux:**

```bash
sudo apt-get install tesseract-ocr
sudo apt-get install tesseract-ocr-vie  # Vietnamese language pack
```

---

## 🎯 Best Practices

### 1. **File Size Limits**

- PDF: < 50MB
- Excel/CSV: < 10MB
- Images: < 5MB
- Text files: < 1MB

### 2. **Metadata Strategy**

```json
{
  "source": "website|upload|api",
  "category": "product|article|document",
  "department": "sales|marketing|research",
  "language": "vi|en",
  "priority": "high|medium|low"
}
```

### 3. **Column Selection cho Excel/CSV**

- **Excel**: Dùng column letters: "A,B,C" hoặc "A,C,E"
- **CSV**: Dùng column indexes: "0,1,2" hoặc "0,2,4"

### 4. **Search Strategy**

- Dùng **basic search** cho queries đơn giản
- Dùng **search-with-scores** khi cần đánh giá độ chính xác
- Dùng **search-with-filter** khi cần lọc theo metadata
- Dùng **search-by-type** khi chỉ muốn tìm trong loại file cụ thể

---

## 🐛 Troubleshooting

### 1. **File Upload Fails**

```bash
# Kiểm tra file type có được hỗ trợ không
curl "http://localhost:8080/api/vector-store/supported-types"

# Validate file trước khi upload
curl -X POST "http://localhost:8080/api/vector-store/validate" -F "file=@myfile.doc"
```

### 2. **OCR không hoạt động**

- Kiểm tra Tesseract đã được cài đặt
- Đảm bảo có Vietnamese language pack nếu cần
- Image quality cần đủ tốt để OCR

### 3. **Excel Column không đúng**

- Kiểm tra column letters/indexes
- Đảm bảo sheet name chính xác (case-sensitive)

---

## 🚀 Next Steps

Sau khi setup thành công Multi Input, bạn có thể:

1. **Tích hợp với AI Assistant** - Các documents sẽ tự động được sử dụng trong RAG pipeline
2. **Setup Batch Processing** - Tự động upload files từ directory
3. **Monitoring & Analytics** - Track usage và performance
4. **Custom Metadata Schema** - Thiết kế metadata structure phù hợp với domain

---

## 📞 Support

Nếu gặp vấn đề, check:

1. Application logs
2. ChromaDB connection status
3. File permissions
4. Dependencies đã được install đúng chưa
