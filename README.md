# Mindbug_Student_Project
A student project that aims to create a digital version of Mindbug game

## FRONTEND

---

### Prerequisites

Before you begin, make sure you have the following installed on your machine:
- **Node.js**
- **npm**

---

### Installation

#### 1.  Install dependencies

```bash
npm install
```

#### 2.  Run the application

Make sure the backend is running...
```bash
npm run serve
```

The application will start on the default port: 8081.

You can access it by opening a browser and navigating to the following URL:
```bash
http://localhost:8081
```

## BACKEND

---

### Prerequisites

Before you begin, make sure you have the following installed on your machine:

- **JDK 17** 
- **Maven**  for dependency management and project build

---

### Installation

#### 1.  Install dependencies

```bash
mvn clean install
```

#### 2.  Run the application

```bash
mvn spring-boot:run
```

The application will start on the default port 8080. You can access it by opening a browser and navigating to the following URL:
http://localhost:8080

###  Accessing the H2 database
http://localhost:8080/h2-console

```bash
JDBC URL: jdbc:h2:mem:testdb 
Username: sa
Password:  
```

### Api and websocket
Api url:
```bash
http://<server:port>/api
```
Websocket URL:
```bash
ws://<server:port>/mindbug-ws
```

Subscribe to queue websocket:
```bash
/topic/game-queue
```

Subscribe to game websocket:
```bash
/topic/game/{id}
```

Swagger api doc:
```bash
http://<server:port>/swagger-ui/index.html
```
### Code quality
#### Checkstyle
```bash
mvn checkstyle:check
```
Result: target/checkstyle-result.xml

### Jacoco
#### Run test with jacoco
```bash
mvn clean test
```
#### Generate jacoco report
```bash
mvn jacoco:report
```

Jacoco result: target/site/jacoco/index.html
