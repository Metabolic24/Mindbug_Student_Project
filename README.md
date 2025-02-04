# Mindbug_Student_Project
A student project that aims to create a digital version of Mindbug game

## FRONTEND

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
Subscribe to game websocket:
```bash
/topic/game
```