# Chat_Bot_Lang4J

## Giới thiệu

Đây là một dự án chatbot được xây dựng với **Spring Boot** và **LangChain4j**. Mục tiêu chính của dự án là triển khai một hệ thống **Retrieval-Augmented Generation (RAG)** kết hợp với một **Agent Text-to-SQL**.

Hệ thống cho phép chatbot trả lời các câu hỏi của người dùng bằng cách:

1.  Truy vấn trực tiếp cơ sở dữ liệu **PostgreSQL** cá nhân thông qua việc chuyển đổi ngôn ngữ tự nhiên thành câu lệnh SQL.
2.  Tìm kiếm thông tin sản phẩm đã được vector hóa và lưu trữ trong **ChromaDB** để trả lời các câu hỏi mang tính ngữ nghĩa.

## Luồng hoạt động

Dự án hoạt động dựa trên hai luồng chính: luồng nhập dữ liệu và luồng truy vấn của chatbot.

### 1. Luồng nhập dữ liệu (Data Ingestion - RAG)

Luồng này chịu trách nhiệm chuẩn bị dữ liệu cho việc tìm kiếm ngữ nghĩa.

1.  **Đọc dữ liệu**: Dữ liệu sản phẩm được đọc từ cơ sở dữ liệu **PostgreSQL**.
2.  **Tạo Embeddings**: Sử dụng một **Embedding Model (text-multilingual-embedding-002)**, thông tin sản phẩm (ví dụ: mô tả, tên) được chuyển đổi thành các vector embedding.
3.  **Lưu trữ Vector**: Các vector embedding này được lưu trữ trong cơ sở dữ liệu vector **ChromaDB**, sẵn sàng cho việc tìm kiếm.

### 2. Luồng truy vấn của Chatbot

Khi người dùng gửi một câu hỏi, Agent của chatbot sẽ xử lý như sau:

1.  **Phân tích câu hỏi phân tích câu hỏi của người dùng để xác định ý định.
2.  **Lựa chọn công cụ (Tool Selection)**:

- **Text-to-SQL**: Nếu câu hỏi yêu cầu dữ liệu có cấu trúc (ví dụ: "giá sản phẩm X là bao nhiêu?", "còn bao nhiêu sản phẩm trong kho?"), Agent sẽ sử dụng **Embedding Model (text-multilingual-embedding-002)** để tạo một câu lệnh SQL tương ứng.
- **RAG (ChromaDB Search)**: Nếu câu hỏi mang tính ngữ nghĩa hoặc yêu cầu mô tả (ví dụ: "gợi ý sản phẩm phù hợp cho người mới bắt đầu"), Agent sẽ thực hiện tìm kiếm trong **ChromaDB** để lấy ra các tài liệu liên quan.

3.  **Thực thi và tổng hợp**:

- Câu lệnh SQL được thực thi trên **PostgreSQL** để lấy dữ liệu chính xác.
- Kết quả tìm kiếm từ **ChromaDB** được lấy ra.

4.  **Tạo câu trả lời**: Dữ liệu thu thập được (từ SQL hoặc ChromaDB) cùng với câu hỏi ban đầu sẽ được gửi đến **Gemini model**. Gemini sẽ tổng hợp thông tin và tạo ra một câu trả lời tự nhiên, mạch lạc cho người dùng.

#### Tùy chỉnh luồng RAG (Retrieval Customization)

Đúng vậy, luồng RAG có thể được tùy chỉnh linh hoạt:

- **Số lượng kết quả (Top K)**: Bạn có thể chỉ định số lượng tài liệu hàng đầu (`top_k`) cần truy xuất từ ChromaDB. Đây là tham số cơ bản để cân bằng giữa lượng thông tin và độ nhiễu.
- **Ngưỡng tương đồng tối thiểu (Minimum Score)**: Bạn có thể đặt một ngưỡng điểm tương đồng (`min_score`) để lọc ra những kết quả không đủ liên quan.
- **Tái xếp hạng (Re-ranking)**: Đối với các hệ thống phức tạp hơn, sau khi truy xuất, một mô hình `cross-encoder` (re-ranker) có thể được sử dụng để đánh giá lại và sắp xếp lại các tài liệu, giúp đưa ra kết quả chính xác nhất lên đầu.


## Tính năng chính

- **Agent thông minh**: Sử dụng một Agent có khả năng lựa chọn giữa các công cụ khác nhau (Text-to-SQL, RAG) để trả lời câu hỏi một cách tối ưu.
- **Text-to-SQL**: Tự động chuyển đổi câu hỏi bằng ngôn ngữ tự nhiên thành các truy vấn SQL để lấy dữ liệu trực tiếp từ PostgreSQL.
- **Retrieval-Augmented Generation (RAG)**: Làm giàu kiến thức của chatbot bằng cách cho phép nó truy cập và tìm kiếm trên dữ liệu sản phẩm đã được nhúng trong ChromaDB.
- **Tích hợp đa mô hình**: Sử dụng **Gemini** cho việc xử lý ngôn ngữ và **Embedding Models** để tạo vector.

## Công nghệ sử dụng

- **Backend**: Spring Boot 3.4.5
- **Ngôn ngữ**: Java 17
- **AI/LLM**:
  - LangChain4j 1.0.0
  - Google Gemini (thông qua Vertex AI)
  - Embedding Model (text-multilingual-embedding-002)
- **Cơ sở dữ liệu**:
  - PostgreSQL
  - ChromaDB (sử dụng Testcontainers)
- **Build Tool**: Maven
- **Khác**: Lombok

## Yêu cầu hệ thống

Trước khi bắt đầu, hãy đảm bảo bạn đã cài đặt các công cụ sau:

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) hoặc phiên bản mới hơn
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/products/docker-desktop/) (để chạy ChromaDB)
- Một instance của [PostgreSQL](https://www.postgresql.org/download/) đang chạy.

## Cấu hình

1.  **Cơ sở dữ liệu PostgreSQL**:
    Mở file `src/main/resources/application.properties` và cấu hình thông tin kết nối đến PostgreSQL của bạn:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

2.  **Cơ sở dữ liệu Chroma(Vector)**:
    Cấu hình cho ChromaDB được quản lý tự động thông qua Testcontainers. Bạn chỉ cần đảm bảo Docker đang chạy trên máy của mình.

3.  **Google Vertex AI (Gemini)**:
    Dự án này sử dụng `langchain4j-vertex-ai-gemini`. Bạn cần phải cấu hình xác thực với Google Cloud. Hãy đảm bảo rằng bạn đã tạo một project trên Google Cloud và đã bật Vertex AI API.

    Cách đơn giản nhất để xác thực là thông qua Google Cloud CLI:

    ```bash
    gcloud auth application-default login
    ```

    Thư viện sẽ tự động nhận diện thông tin xác thực của bạn.
