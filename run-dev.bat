@echo off
echo 🚀 Starting Chat Bot with Local Embedding (Development Mode)
echo.
echo ⚠️  Make sure PostgreSQL and ChromaDB are running!
echo 📍 PostgreSQL: localhost:5432
echo 📍 ChromaDB: localhost:8000
echo.

set SPRING_PROFILES_ACTIVE=dev
set JAVA_OPTS=-Dembedding.use-local=true -Dembedding.fallback-enabled=true

echo 🏠 Using Local Embedding Model (No API Quota Limits)
echo 📊 Logging Level: DEBUG for embedding services
echo.

mvn spring-boot:run -Dspring-boot.run.profiles=dev

pause 