#!/bin/bash

echo "🚀 Starting Chat Bot with Local Embedding (Development Mode)"
echo ""
echo "⚠️  Make sure PostgreSQL and ChromaDB are running!"
echo "📍 PostgreSQL: localhost:5432" 
echo "📍 ChromaDB: localhost:8000"
echo ""

export SPRING_PROFILES_ACTIVE=dev
export JAVA_OPTS="-Dembedding.use-local=true -Dembedding.fallback-enabled=true"

echo "🏠 Using Local Embedding Model (No API Quota Limits)"
echo "📊 Logging Level: DEBUG for embedding services"
echo ""

mvn spring-boot:run -Dspring-boot.run.profiles=dev 