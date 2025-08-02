# SmartLawGT - Smart Legal Assistant System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-blue.svg)](https://www.microsoft.com/en-us/sql-server)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![Design Document](https://img.shields.io/badge/System%20Design-View-blue.svg)](https://docs.google.com/document/d/1XEemXfbtc2ARjurRpzdPvj_x0ltT9uoGJ99Ru_ROj2E/edit?usp=sharing)

## 🖥️ Frontend Repository

The frontend for SmartLawGT is developed separately and available at:

- 🔗 [SmartLawGT Frontend (React)](https://github.com/Sang-Truong20/swd)

## 📑 Table of Contents
- [Overview](#-overview)
- [Documentation](#-documentation)
- [Key Features](#-key-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Security](#-security)
- [Monitoring & Logging](#-monitoring--logging)
- [Contributing](#-contributing)
- [License](#-license)
- [Support](#-support)
- [Deployment](#-deployment)

## 📋 Overview
SmartLawGT is an AI-powered legal assistant providing accurate legal consultation via a chat interface. It supports multiple payment gateways, secure authentication, and subscription-based premium services.

## 📄 Documentation
- 🧩 [System Design Document](https://docs.google.com/document/d/1XEemXfbtc2ARjurRpzdPvj_x0ltT9uoGJ99Ru_ROj2E/edit?usp=sharing)
- 📚 [API Docs (Swagger)](http://localhost:8080/swagger-ui.html)

## ✨ Key Features
- 🤖 **AI-Powered Legal Consultation** - Google Gemini AI integration
- 💳 **Multiple Payment Methods** - VNPay and MoMo support
- 📦 **Subscription Packages** - Daily usage limits, flexible plans
- 🔐 **Secure Authentication** - JWT & Google OAuth2
- 📧 **OTP Verification** - Email-based OTP for user security
- 💾 **Smart Caching** - Redis for fast response
- 📊 **Real-time Updates** - WebSocket & RabbitMQ
- 🔍 **Semantic Search** - Ollama-based document search
- 📱 **RESTful API** - Swagger/OpenAPI 3 support

## 🏗️ Architecture
CQRS (Command Query Responsibility Segregation) with event-driven design:
- **Command Side**: SQL Server for write operations
- **Query Side**: MongoDB for read operations
- **Event Bus**: RabbitMQ
- **Cache**: Redis
- **AI Services**: Google Gemini AI & Ollama

## 🛠️ Technology Stack
**Backend**
- Java 17, Spring Boot 3.x
- Spring Security, Spring Data JPA/MongoDB
- Spring AMQP, Spring WebSocket

**Databases**
- SQL Server 2019+, MongoDB 7.0+, Redis 7.0

**AI & ML**
- Google Gemini AI, Ollama

**Payment Integration**
- VNPay, MoMo

**Messaging**
- RabbitMQ

**Documentation**
- Swagger/OpenAPI 3

## 🚀 Getting Started
### Prerequisites
- Java 17+, Maven 3.6+
- SQL Server, MongoDB, Redis, RabbitMQ, Ollama

### Installation
1. Clone repo:
```bash
git clone https://github.com/your-username/SmartLawGT.git
cd SmartLawGT
```

2. Set up databases:
```sql
CREATE DATABASE SmartLawGT;
```

3. Configure application.properties:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SmartLawGT
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.data.mongodb.uri=mongodb://localhost:27017/smartlawgt
spring.redis.host=localhost
spring.redis.port=6379
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
# API keys
gemini.api.key=your_gemini_api_key
vnpay.hash-secret=your_vnpay_secret
momo.secret-key=your_momo_secret
jwt.secret=your_jwt_secret
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

4. Install and run Ollama:
```bash
curl -fsSL https://ollama.ai/install.sh | sh
ollama pull nomic-embed-text
```

5. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

## 🏛️ Project Structure
```
src/main/java/org/example/smartlawgt/
├── command/
├── query/
├── integration/
├── events/
├── listeners/
├── config/
├── schedulers/
```

## 🔐 Security
- JWT Auth, Google OAuth2
- Email OTP
- Rate limiting
- Secure payments

## 📊 Monitoring & Logging
- SLF4J logging
- Health checks
- Redis performance

## 🤝 Contributing
1. Fork repo
2. Create branch (`feature/xyz`)
3. Commit & push
4. Open Pull Request

## 📄 License
MIT License - see [LICENSE](LICENSE)

## 👥 Team
- **Lead Developer**: Tran Cong Tuong
- **Project**: Smart Legal Assistant System (SWD392)

## 📞 Support
- Email: congtuong.dev@gmail.com
- GitHub Issues: [Create issue](https://github.com/your-username/SmartLawGT/issues)

## 🚀 Deployment
### Docker (Coming Soon)
```bash
docker build -t smartlawgt .
docker-compose up -d
```

### Environment Variables
```bash
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=your_database_url
MONGODB_URI=your_mongodb_uri
REDIS_URL=your_redis_url
GEMINI_API_KEY=your_gemini_key
VNPAY_SECRET=your_vnpay_secret
MOMO_SECRET=your_momo_secret
```

---

⭐ **Star this repository if you find it helpful!**
