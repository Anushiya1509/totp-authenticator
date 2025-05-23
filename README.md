
# 🔐 TOTP-Enabled Authentication API

This project is a Java Spring Boot-based authentication service that supports:

- ✅ User registration and login using JWT
- 🔐 TOTP (Time-based One-Time Password) generation and verification
- 🔒 Encrypted TOTP secret storage (AES-based)
- 💾 PostgreSQL or Oracle DB support
- ☁️ Ready for deployment on AWS or Docker

---

## 🚀 Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security (JWT)
- PostgreSQL / Oracle XE
- AES encryption
- Google Authenticator-compatible TOTP
- DevSamStevens TOTP library
- Docker (optional)
- AWS-ready structure

---

## 🧱 Project Structure

```
com.anushiya.totp
├── controller        # API endpoints for auth and TOTP
├── dto              # Request payloads
├── entity           # JPA Entities
├── repository       # UserRepository (Spring Data JPA)
├── security         # JWT provider and filter
├── service          # Business logic for auth and TOTP
├── config           # Security and encryption key configs
└── application.properties
```

---

## ⚙️ Setup & Run

### 1. 📦 Prerequisites

- JDK 17+
- Maven
- PostgreSQL or Oracle XE running locally
- Docker (optional for DB)
- (Optional) Postman for testing

---

### 2. 🛠 Configure DB

In `src/main/resources/application.properties`, configure your DB:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/totp
spring.datasource.username=postgres
spring.datasource.password=mysecretpassword
spring.jpa.hibernate.ddl-auto=update

# AES encryption key (must be 16, 24, or 32 chars)
encryption.key=SuperSecureTotpEncryptionKey256!
```

---

### 3. ▶️ Run the Application

```bash
mvn spring-boot:run
```

---

## 🔑 API Endpoints

### 📝 Register

```http
POST /api/auth/register
```

```json
{
  "username": "anushiya",
  "password": "MyPassword123!",
  "email": "anushiya@email.com"
}
```

---

### 🔐 Login

```http
POST /api/auth/login
```

```json
{
  "username": "anushiya",
  "password": "MyPassword123!"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI..."
}
```

---

### 🧪 Generate TOTP

```http
POST /api/totp/generate?username=anushiya
Authorization: Bearer <JWT>
```

---

### ✅ Verify TOTP

```http
POST /api/totp/verify?username=anushiya&code=123456
Authorization: Bearer <JWT>
```

---

## 🛡 Security

- AES encryption with a configurable key from `application.properties`
- JWT-based stateless authentication
- All TOTP secrets are securely encrypted and stored
- Passwords are hashed with `BCrypt`

---

## 📦 Docker (Optional)

```bash
docker pull postgres
docker run --name postgres-totp -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres
```

---

## 🤝 Contribution

Pull requests are welcome. Please follow clean architecture and separation of concerns.

---

## 📄 License

This project is open-source and free to use.
