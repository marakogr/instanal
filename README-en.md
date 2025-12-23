[![ru](https://img.shields.io/badge/lang-ru-blue.svg)](https://github.com/marakogr/instanal/blob/master/README.md)

# 📊 InstAnal — Instagram Chat Analysis

**InstAnal** is an application for analyzing Instagram chats with the creation of interactive dashboards in **Apache
Superset**.

The project is designed for extracting chat data, normalizing it, calculating communication metrics, and visually
analyzing conversation activity.

---

## 🚀 Features

- 📥 Import Instagram chat history (JSON)
- 🧮 Calculation of activity, ratings, and communication metrics
- 📊 Dashboard constructor for Apache Superset with predefined charts
- 🐳 Fully containerized deployment via Docker
- 📈 Ready-to-use PostgreSQL database for analytics

---

## 🖼️ Interface

### Login Page

![Login Page](https://github.com/marakogr/instanal/blob/master/img/login.png?raw=true)

---

### Main Page

![Main Page](https://github.com/marakogr/instanal/blob/master/img/main.png?raw=true)

### Chat Import

![Chat Import](https://github.com/marakogr/instanal/blob/master/img/chat.png?raw=true)

### Example Analytical Dashboard

![Dashboard](https://github.com/marakogr/instanal/blob/master/img/superset.jpg?raw=true)

---

## ⚙️ Quick Start

### 1️⃣ Start Containers

```bash
docker compose up -d --build
```

### 2️ Initialize Superset

```bash
docker compose exec superset superset db upgrade

docker compose exec superset superset fab create-admin \
--username admin \
--firstname Admin \
--lastname User \
--email admin@admin.com \
--password admin

docker compose exec superset superset init
```

### 3️⃣ Access Superset

URL: http://localhost:8088
Login: admin
Password: admin

### 🗄️ Database Connection

Add a data source in Superset

```text
postgresql+psycopg2://postgres:postgres@postgres:5432/instanal
```

### 📥 Data Import

1. Create your account in the application and add a friend
2. Download Instagram chat history (scroll to the required date, open dev tools -> network -> websocket -> copy as HAR,
   save as JSON file)
3. Select the friend and click "Import Chat", upload the obtained history
4. After uploading, the data is immediately available for analysis

### 🧱 Project Architecture

```text
instanal/
├── src/                    # Processing and analysis logic
├── img/                    # Screenshots for README
├── docker-compose.yaml     # Service orchestration
├── Dockerfile              # Image builds
└── README.md               # Project documentation
```

### 🛠️ Technology Stack

| Компонент       | Назначение                    |
|-----------------|-------------------------------|
| Docker          | Containerization              |
| Docker Compose  | Environment orchestration     |
| PostgreSQL      | Analytical data storage       |
| Apache Superset | BI and visualization          |
| Java            | Processing and business logic |

### 💡 Development Ideas
📊 Advanced communication metrics
🤖 ML/AI dialog analysis
🧠 Sentiment analysis of messages
⏱️ Analysis of communication time patterns

### 👤 Author
Grigoriy Marakov
GitHub: https://github.com/marakogr