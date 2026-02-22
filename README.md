# 📋 Task Manager App

A modern, full-featured **Android Task Manager** built with the latest Jetpack stack. Manage your tasks on the go with a clean Material 3 UI, offline support, and a secure JWT-authenticated REST backend.

---

## ✨ Features

- 🔐 **Authentication** — Register, Login, Forgot Password with JWT token storage
- 📋 **Task Management** — Create, edit, delete tasks with status & due date
- 📊 **Dashboard** — Live summary of total, pending and completed tasks
- 👤 **Profile** — View and update your account info
- 💾 **Offline First** — Tasks saved locally with Room and auto-synced when back online
- 🔄 **Manual Sync** — One-tap sync button to push/pull latest data

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.1.20 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Networking | Retrofit 2 + OkHttp + Logging Interceptor |
| Local DB | Room (offline cache) |
| State | StateFlow + collectAsStateWithLifecycle |
| Async | Coroutines |
| Token Storage | DataStore Preferences |
| Navigation | Navigation Compose |

---

## 🏗 Project Structure

```
com.taskmanager/
├── data/
│   ├── local/          # Room DB, DAOs, Entities
│   ├── remote/         # Retrofit API, DTOs, Auth Interceptor
│   └── repository/     # Repository implementations + Mappers
├── domain/
│   ├── model/          # Domain models (Task, User, Dashboard)
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
├── presentation/
│   ├── auth/           # Login, Register, Forgot Password screens
│   ├── dashboard/      # Dashboard screen + Bottom Nav
│   ├── task/           # Task list, create, edit, detail screens
│   ├── profile/        # Profile screen
│   ├── navigation/     # NavGraph + Screen routes
│   └── ui/             # Shared components + Theme
├── di/                 # Hilt modules (Network, Database, Repository)
└── utils/              # TokenManager (DataStore)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Meerkat or newer
- JDK 17
- Android device or emulator (API 26+)
- A running backend server

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/yourusername/task-manager-app.git
   cd task-manager-app
   ```

2. **Set your backend URL**

   Open `app/src/main/java/com/taskmanager/di/NetworkModule.kt` and update:
   ```kotlin
   // Real device on WiFi
   private const val BASE_URL = "http://192.168.x.x:3000/"

   // Android emulator
   private const val BASE_URL = "http://10.0.2.2:3000/"

   // Production
   private const val BASE_URL = "https://api.yourapp.com/"
   ```

3. **Run the app**

   Open in Android Studio → click ▶ Run

---

## 🌐 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & receive JWT |
| GET | `/api/auth/profile` | Get current user profile |
| PUT | `/api/auth/profile` | Update profile |
| POST | `/api/auth/logout` | Logout |
| GET | `/api/todos` | Get all tasks |
| POST | `/api/todos` | Create new task |
| GET | `/api/todos/:id` | Get single task |
| PUT | `/api/todos/:id` | Update task |
| DELETE | `/api/todos/:id` | Delete task |
| GET | `/api/dashboard` | Get task summary stats |

---

## 📱 Screenshots

> _Add your screenshots here_

| Login | Dashboard | Task List | Profile |
|-------|-----------|-----------|---------|
| ![login]() | ![dashboard]() | ![tasks]() | ![profile]() |

---

## 📄 License

```
MIT License — feel free to use, modify and distribute.
```

---

<p align="center">Built with ❤️ using Kotlin & Jetpack Compose</p>
