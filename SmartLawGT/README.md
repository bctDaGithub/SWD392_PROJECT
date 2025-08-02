# SmartLawGT - Smart Legal Assistant System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-blue.svg)](https://www.microsoft.com/en-us/sql-server)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)

## 📋 Overview

SmartLawGT is an intelligent legal assistant system that provides users with accurate legal information and consultation services through AI-powered chat interface. The system integrates with multiple payment gateways and offers subscription-based services for enhanced features.

## ✨ Key Features

- 🤖 **AI-Powered Legal Consultation** - Integration with Google Gemini AI for intelligent legal advice
- 💳 **Multiple Payment Methods** - Support for VNPay and MoMo payment gateways
- 📦 **Package-Based Subscriptions** - Flexible usage packages with daily limits
- 🔐 **Secure Authentication** - JWT-based authentication with Google OAuth2 integration
- 📧 **OTP Verification** - Email-based OTP system for account security
- 💾 **Smart Caching** - Redis-based caching for improved response times
- 📊 **Real-time Updates** - WebSocket and RabbitMQ for real-time notifications
- 🔍 **Semantic Search** - Ollama integration for intelligent document search
- 📱 **RESTful API** - Comprehensive API with Swagger documentation

## 🏗️ Architecture

The system follows a **CQRS (Command Query Responsibility Segregation)** pattern with event-driven architecture:

- **Command Side**: Handles write operations (SQL Server)
- **Query Side**: Handles read operations (MongoDB)
- **Event Bus**: RabbitMQ for asynchronous communication
- **Caching Layer**: Redis for performance optimization
- **AI Integration**: Google Gemini AI and Ollama for intelligent features

## 🛠️ Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.x** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - SQL database operations
- **Spring Data MongoDB** - NoSQL database operations
- **Spring AMQP** - RabbitMQ integration
- **Spring WebSocket** - Real-time communication

### Databases
- **SQL Server** - Primary database for write operations
- **MongoDB** - Read database for query operations
- **Redis** - Caching and session management

### AI & ML
- **Google Gemini AI** - Legal consultation AI
- **Ollama** - Local embedding and semantic search

### Payment Integration
- **VNPay** - Vietnamese payment gateway
- **MoMo** - Mobile payment solution

### Message Queue
- **RabbitMQ** - Asynchronous messaging

### Documentation
- **Swagger/OpenAPI 3** - API documentation

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:
- Java 17 or higher
- Maven 3.6+
- SQL Server 2019+
- MongoDB 7.0+
- Redis 7.0+
- RabbitMQ 3.12+
- Ollama (for AI features)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/your-username/SmartLawGT.git
cd SmartLawGT
```

2. **Set up databases**

**SQL Server:**
```sql
CREATE DATABASE SmartLawGT;
```

**MongoDB:**
```bash
# MongoDB will create the database automatically
# Default database name: smartlawgt
```

**Redis:**
```bash
# Start Redis server
redis-server
```

**RabbitMQ:**
```bash
# Start RabbitMQ server
rabbitmq-server
```

3. **Configure application properties**

Update `src/main/resources/application.properties` with your configurations:

```properties
# Database configurations
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=SmartLawGT
spring.datasource.username=your_username
spring.datasource.password=your_password

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/smartlawgt

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# API Keys (replace with your own)
gemini.api.key=your_gemini_api_key
vnpay.hash-secret=your_vnpay_secret
momo.secret-key=your_momo_secret
jwt.secret=your_jwt_secret

# Email configuration
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

4. **Install and run Ollama** (for AI features)
```bash
# Install Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# Pull the embedding model
ollama pull nomic-embed-text
```

5. **Build and run the application**
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, you can access:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🔧 Configuration

### Payment Gateway Setup

**VNPay Configuration:**
1. Register for VNPay merchant account
2. Get TMN Code and Hash Secret
3. Configure return URLs

**MoMo Configuration:**
1. Register for MoMo partner account
2. Get Partner Code, Access Key, and Secret Key
3. Set up IPN and return URLs

### AI Services Setup

**Google Gemini:**
1. Get API key from Google AI Studio
2. Configure in application.properties

**Ollama:**
1. Install Ollama locally
2. Pull required models
3. Configure base URL

## 🏛️ Project Structure

```
src/main/java/org/example/smartlawgt/
├── command/           # Write side (CQRS)
│   ├── controllers/   # REST controllers
│   ├── services/      # Business logic
│   ├── repositories/  # Data access
│   └── entities/      # JPA entities
├── query/             # Read side (CQRS)
│   ├── controllers/   # Query controllers
│   ├── services/      # Query services
│   ├── repositories/  # MongoDB repositories
│   └── documents/     # MongoDB documents
├── integration/       # External integrations
│   ├── ai/           # AI services (Gemini, Ollama)
│   ├── payment/      # Payment gateways
│   ├── auth/         # Authentication
│   └── otp/          # OTP services
├── events/           # Domain events
├── listeners/        # Event listeners
├── config/           # Configuration classes
└── schedulers/       # Scheduled tasks
```

## 🔐 Security

- JWT-based authentication
- Google OAuth2 integration
- Email-based OTP verification
- Rate limiting for API endpoints
- Secure payment processing

## 📊 Monitoring & Logging

- Structured logging with SLF4J
- Health check endpoints
- Performance monitoring with caching
- Error tracking and handling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Developer**: SWD392 Team
- **Project**: Smart Legal Assistant System

## 📞 Support

For support and questions:
- Email: tuongtcse181735@fpt.edu.vn
- GitHub Issues: [Create an issue](https://github.com/your-username/SmartLawGT/issues)

## 🚀 Deployment

### Docker Support (Coming Soon)
```bash
# Build Docker image
docker build -t smartlawgt .

# Run with Docker Compose
docker-compose up -d
```

### Environment Variables
```bash
# Required environment variables
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
