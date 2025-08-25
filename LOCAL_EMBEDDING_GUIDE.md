# 🏠 Local Embedding Guide - Tránh Quota Issues

## ❌ Vấn đề: Vertex AI Quota Exhausted

Khi gặp lỗi:

```
RESOURCE_EXHAUSTED: Quota exceeded for aiplatform.googleapis.com/online_prediction_requests_per_base_model
```

## ✅ Giải pháp: Local Embedding Model

### 🚀 Cách sử dụng nhanh:

**Windows:**

```batch
run-dev.bat
```

**Linux/Mac:**

```bash
./run-dev.sh
```

### Cấu hình Manual:

```yaml
embedding:
  use-local: true
  fallback-enabled: true
```

## 🎯 Ưu điểm:

- ✅ Zero API Calls - Không quota limits
- ✅ Hoạt động Offline
- ✅ Tốc độ cao với cache
- ✅ Hỗ trợ tiếng Việt

## 🔄 Fallback Mechanism:

```
User Input → Try Vertex AI → If fails → Use Local Embedding
```

---

**🎉 Không bao giờ lo quota limits nữa!**
