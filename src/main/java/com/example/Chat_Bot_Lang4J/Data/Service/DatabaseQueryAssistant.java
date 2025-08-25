package com.example.Chat_Bot_Lang4J.Data.Service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.Chat_Bot_Lang4J.Data.Tool.DatabaseTools;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseQueryAssistant {

    private final DatabaseTools databaseTools;
    private final ChatModel chatModel;

    // Create AI service với tools
    private DatabaseQueryAI createQueryAI() {
        return AiServices.builder(DatabaseQueryAI.class)
                .chatModel(chatModel)
                .tools(databaseTools)
                .build();
    }

    /**
     * 🗄️ Process database query với function calling
     */
    public String processQuery(String userQuery) {
        log.info("🗄️ Processing database query: {}", userQuery);
        
        DatabaseQueryAI queryAI = createQueryAI();
        return queryAI.query(userQuery);
    }

    /**
     * 🧠 AI Interface với few-shot examples
     */
    public interface DatabaseQueryAI {
        @SystemMessage("""
            Bạn là một Database Query Assistant chuyên nghiệp cho hệ thống quản lý sản phẩm.

            BẠN CÓ CÁC TOOLS SAU:
            🔧 getDatabaseStatistics() - Lấy thống kê tổng quan
            🔧 findProductById(Long id) - Tìm sản phẩm theo ID chính xác
            🔧 searchProductsByName(String name) - Tìm sản phẩm theo tên
            🔧 findProductsByPriceRange(double min, double max) - Tìm theo khoảng giá
            🔧 getAllCategories() - Lấy tất cả danh mục
            🔧 findCategoryById(Long id) - Tìm danh mục theo ID
            🔧 findProductsByCategoryId(Long id) - Tìm sản phẩm theo danh mục
            🔧 countProductsByCategory(String name) - Đếm sản phẩm theo danh mục
            🔧 findLowStockProducts(int threshold) - Tìm sản phẩm tồn kho thấp

            EXAMPLES - HỌC CÁCH MAPPING:

            User: "Sản phẩm ID 5 giá bao nhiêu?"
            → Call: findProductById(5)

            User: "Cho tôi biết giá của iPhone"
            → Call: searchProductsByName("iPhone")

            User: "Sản phẩm từ 10 triệu đến 20 triệu"
            → Call: findProductsByPriceRange(10000000, 20000000)

            User: "Có bao nhiêu sản phẩm điện tử?"
            → Call: countProductsByCategory("Điện tử")

            User: "Danh mục số 2 có gì?"
            → Call: findProductsByCategoryId(2)

            User: "Thống kê số lượng"
            → Call: getDatabaseStatistics()

            User: "Sản phẩm sắp hết hàng"
            → Call: findLowStockProducts(10)

            RULES:
            ✅ Luôn chọn tool phù hợp nhất với câu hỏi
            ✅ Trích xuất chính xác tham số (ID, số tiền, tên)
            ✅ Với giá tiền: "triệu" = × 1,000,000
            ✅ Nếu không chắc chắn, hỏi người dùng làm rõ
            ✅ Trả lời bằng tiếng Việt tự nhiên sau khi gọi tool
            """)
        String query(@UserMessage String userQuery);
    }
}
