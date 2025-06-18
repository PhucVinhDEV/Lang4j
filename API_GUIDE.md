# 🚀 Chat Bot Lang4J - API Guide

## 📋 Tổng quan hệ thống

Hệ thống Chat Bot Lang4J đã được cải thiện với:

- ✅ **Database Integration**: PostgreSQL với 2 entities (Categories, Product)
- ✅ **Vector Store**: ChromaDB cho tìm kiếm ngữ nghĩa
- ✅ **AI Assistant**: Gemini model với prompt template thông minh
- ✅ **RESTful APIs**: Đầy đủ endpoints để quản lý dữ liệu

## 🗄️ Database Schema

### Categories Table

```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);
```

### Product Table

```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE PRECISION NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

## 🔌 API Endpoints

### 1. AI Assistant APIs

#### Chat với AI Assistant

```http
POST /api/assistant/chat
Content-Type: application/json

{
    "message": "Tôi muốn tìm sản phẩm điện tử dưới 20 triệu"
}
```

### 2. Data Management APIs

#### Khởi tạo dữ liệu mẫu

```http
POST /api/data/init-sample-data
```

#### Lấy tất cả categories

```http
GET /api/data/categories
```

#### Lấy tất cả products

```http
GET /api/data/products
```

## 🚀 Khởi chạy hệ thống

1. **Cài đặt dependencies:**

```bash
mvn clean install
```

2. **Khởi động PostgreSQL:**

```bash
# Tạo database
createdb shopee_db

# Hoặc sử dụng file schema.sql
psql -U bitznomad -d shopee_db -f src/main/resources/schema.sql
```

3. **Khởi động ứng dụng:**

```bash
mvn spring-boot:run
```

4. **Khởi tạo dữ liệu mẫu:**

```bash
curl -X POST http://localhost:8080/api/data/init-sample-data
```
