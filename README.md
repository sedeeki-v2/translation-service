
# Translation Management Service

An **API-driven Translation Management Service** built with **Spring Boot 3**, **MongoDB**, and **Docker**.  
The service allows storing, tagging, and retrieving translations for multiple locales. It is designed for scalability and can be easily extended to support new languages in the future.  

---

## 📖 Features

- Store translations for multiple locales (e.g., `en`, `fr`, with support for future languages).  
- Tag translations for better context (e.g., `ui`, `error`, `button`).  
- CRUD APIs to **create, update, view, and search translations** by tags, keys, or content.  
- A **JSON export endpoint** that frontend applications (e.g., Vue.js) can consume.  
  - Always returns the **latest translations** on request.  
- Swagger UI documentation available at:  
  ```
  http://localhost:8080/swagger-ui/index.html
  ```  

---

## 🛠️ Requirements

- Java 21  
- Docker & Docker Compose  
- Maven  

---

## 🚀 Running the Application

You can run the application using Docker Compose after building the JAR.  

### 1️⃣ Build the JAR file  
From the project root, run:  

```bash
./mvnw clean package -DskipTests
```

This will generate a `target/app.jar` file (or similar depending on your setup).  

---

### 2️⃣ Start services with Docker Compose  

```bash
docker-compose up --build
```

This will:  
- Start a **MongoDB 6.0** instance  
- Start the **Spring Boot application** (bound to port `8080`)  

---

### 3️⃣ Access the application  

- **API Base URL**:  
  ```
  http://localhost:8080/api/translations
  ```
- **Swagger UI**:  
  ```
  http://localhost:8080/swagger-ui/index.html
  ```

---

## 📂 Example Endpoints

- `POST /api/auth/register` → Register a new user  
- `POST /api/auth/login` → Authenticate and receive JWT  
- `GET /api/translations` → Retrieve all translations (projection view)  
- `POST /api/translations/seed` → Seed 100,000 demo translations  

---

## 🧩 Tech Stack

- **Spring Boot 3.5.4**  
- **MongoDB 6.0**  
- **Spring Security + JWT** for authentication  
- **Springdoc OpenAPI** for Swagger documentation  
- **Docker Compose** for containerized setup  

---

## 📌 Notes

- Default MongoDB database: `translation_db`  
- MongoDB exposed on: `localhost:27017`  
- Application exposed on: `localhost:8080`  
- JWT secret & expiration can be configured in `docker-compose.yml`.  
