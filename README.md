Here’s a well-structured **README.md** that explains the entire **community-bot** repository, covering its purpose, functionality, events, setup, and deployment.

---

### **README.md**

# Community Bot

**Community Bot** is a Kotlin-based Telegram bot designed for automation, user engagement, and streamlined interactions within a community. It supports event-driven actions such as user management, submissions handling, and bot activity tracking.

## Features

- **Event-Driven Architecture** – Handles various Telegram events like user joins, message interactions, and submissions.
- **Automated User Management** – Tracks user interactions such as profile and resume submissions.
- **Customizable Bot Commands** – Extensible command system for managing requests and processing data.
- **Modular Structure** – Well-organized Kotlin codebase for maintainability.
- **Docker Support** – Easily deployable via Docker.

## Event System

The bot processes various Telegram-based events under `event/`. These include:

### **Bot Activity Events**
- **`BotJoinedChatEvent`** – Triggered when the bot joins a chat.
- **`BotLeftChatEvent`** – Triggered when the bot leaves a chat.
- **`ChatTitleChangedEvent`** – Triggered when a chat title changes.

### **User Interaction Events**
- **`UsersJoinedChatEvent`** – Triggered when users join a chat.
- **`UserLeftChatEvent`** – Triggered when a user leaves a chat.

### **Submission Events**
- **`ProfileSubmittedEvent`** – Triggered when a user submits a profile.
- **`ProjectSubmittedEvent`** – Triggered when a project is submitted.
- **`RequestSubmittedEvent`** – Triggered when a request is submitted.
- **`ResumeSubmittedEvent`** – Triggered when a resume is submitted.
- **`SaleSubmittedEvent`** – Triggered when a sale is submitted.
- **`ServiceSubmittedEvent`** – Triggered when a service is submitted.
- **`VacancySubmittedEvent`** – Triggered when a job vacancy is submitted.

Additionally, the repository includes:
- **`IBotIdAware.kt`** – Interface for bot identification.
- **`IMessageCapturingEvent.kt`** – Interface for message processing.

## Commands

The bot provides several commands for community management and automation.

### **General Commands**
- **`HelpCommand`** – Displays a list of available commands.
- **`GetChatInfosCommand`** – Retrieves information about all chats where the user has admin rights.

### **Admin Commands**
- **`FanoutCommand`** – Sends a message to multiple groups.

### **Customization Commands**
- **`SetWelcomeMessageCommand`** – Configures a custom welcome message for new users.
- **`SetFooterCommand`** – Sets a footer message for chat interactions.

## Setup & Installation

### **Prerequisites**
- **JDK 21+** (Recommended)
- **Gradle** (Kotlin DSL)
- **Docker** (Optional for deployment)

### **Clone the Repository**
```sh
git clone https://github.com/Affid/community-bot.git
cd community-bot
```

### **Build the Project**
```sh
./gradlew build
```

### **Required env variables**
- GLOBAL_ADMINS : a comma-separated list in square brackets
- BOT_TOKEN
- BOT_USERNAME (without @ sign)
- APP_URL
- APP_PORT (9091 by default)
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- DB_SCHEMA ('public' by default)

### **Run Locally**
For local development with Docker:
```sh
docker-compose up -d
```

## Production Deployment

For a production environment, use the **Dockerfile**:

### **Build and Run the Container**
```shell
docker build -t community-bot . --network=host
```

```shell
docker run --env-file=config.env -p 9091:9091 -d --name community-bot community-bot
```

## Contribution Guide

Contributions are welcome! If you'd like to contribute:
1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request.
