<div align="center">

# ✨ TaskFlow — Smart Task Manager

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
<img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
<img src="https://img.shields.io/badge/Architecture-MVVM-FF6F00?style=for-the-badge" />
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" />

<br/>

> 🚀 A beautifully crafted, feature-rich Android productivity app that helps you organize tasks, build routines, and crush your goals — all from one place.

<br/>

<img src="image/app-preview.jpg" width="200" alt="TaskFlow App Preview" />

<br/>

**Your all-in-one productivity companion for Android**

<br/><br/>

[📥 Download APK](#-installation) · [🐛 Report Bug](https://github.com/your-username/task-management-app/issues) · [💡 Request Feature](https://github.com/your-username/task-management-app/issues)

</div>

---

## 📑 Table of Contents

- [About](#-about)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Screenshots](#-screenshots)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

---

## 🧠 About

**TaskFlow** is a modern, offline-first Android Task Management application designed to simplify how you plan your day, track habits, and stay productive. Built with cutting-edge Android technologies and following Material Design 3 guidelines, it provides a seamless and intuitive user experience.

Whether you're a student tracking assignments, a professional managing deadlines, or anyone looking to stay organized — TaskFlow has you covered.

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 📋 Task Management
- ✏️ Create, edit & delete tasks
- ✅ Mark tasks as completed
- 🔄 Recurring tasks (daily, weekly, custom)
- 🏷️ Categories & color-coded labels
- 🔴🟡🟢 Priority levels (High / Medium / Low)

</td>
<td width="50%">

### ⏰ Reminders & Scheduling
- 🔔 Push notifications & alerts
- 📅 Calendar view for task planning
- ⏱️ Due date & time tracking
- 🔁 Smart recurring reminders
- 📆 Weekly & monthly overviews

</td>
</tr>
<tr>
<td width="50%">

### 📊 Productivity & Insights
- 📈 Daily / weekly productivity stats
- 🏆 Streak tracking & achievements
- 📉 Task completion analytics
- 🎯 Goal tracking dashboard

</td>
<td width="50%">

### 🎨 User Experience
- 🌗 Light & Dark mode
- 🔍 Search, filter & sort
- 💾 Fully offline with local storage
- 📱 Responsive across all Android devices
- 🎭 Smooth animations & transitions

</td>
</tr>
</table>

---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technology |
|:---:|:---:|
| **Language** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) |
| **UI Framework** | ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white) |
| **Database** | ![Room](https://img.shields.io/badge/Room%20DB-3DDC84?style=for-the-badge&logo=android&logoColor=white) |
| **Preferences** | ![DataStore](https://img.shields.io/badge/DataStore-FF6F00?style=for-the-badge&logo=android&logoColor=white) |
| **Design** | ![Material 3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white) |
| **Architecture** | ![MVVM](https://img.shields.io/badge/MVVM-0288D1?style=for-the-badge) |
| **DI** | ![Hilt](https://img.shields.io/badge/Hilt-34A853?style=for-the-badge&logo=android&logoColor=white) |
| **Navigation** | ![Nav Compose](https://img.shields.io/badge/Navigation%20Compose-1565C0?style=for-the-badge&logo=android&logoColor=white) |

</div>

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────┐
│                  UI Layer                    │
│         (Jetpack Compose + Screens)         │
├─────────────────────────────────────────────┤
│              ViewModel Layer                 │
│          (State + Business Logic)            │
├─────────────────────────────────────────────┤
│             Repository Layer                 │
│        (Single Source of Truth)              │
├──────────────────┬──────────────────────────┤
│   Room Database  │     DataStore Prefs      │
│  (Tasks, Tags)   │   (Settings, Theme)      │
└──────────────────┴──────────────────────────┘
```

The app follows **Clean Architecture** principles with the **MVVM** (Model-View-ViewModel) pattern for clear separation of concerns and testability.

---

## 📸 Screenshots

<div align="center">

> ⚠️ *Screenshots will be added once the UI is finalized.*

<!--
<table>
<tr>
<td><img src="screenshots/home_light.png" width="200" alt="Home Light"/></td>
<td><img src="screenshots/home_dark.png" width="200" alt="Home Dark"/></td>
<td><img src="screenshots/calendar.png" width="200" alt="Calendar"/></td>
<td><img src="screenshots/stats.png" width="200" alt="Stats"/></td>
</tr>
<tr>
<td align="center">Home (Light)</td>
<td align="center">Home (Dark)</td>
<td align="center">Calendar View</td>
<td align="center">Productivity</td>
</tr>
</table>
-->

</div>

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog or later
- **JDK 17** or higher
- **Android SDK** API 24+ (Android 7.0)
- **Gradle** 8.0+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/your-username/task-management-app.git

# 2. Navigate to the project directory
cd task-management-app

# 3. Open in Android Studio and sync Gradle

# 4. Run the app on an emulator or physical device
```

### Build APK

```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📂 Project Structure

```
📦 task-management-app
├── 📁 app
│   └── 📁 src/main
│       ├── 📁 java/com/yourpackage/taskflow
│       │   ├── 📁 data          # Room DB, DAOs, Repositories
│       │   ├── 📁 di            # Hilt dependency injection modules
│       │   ├── 📁 domain        # Use cases & domain models
│       │   ├── 📁 ui            # Compose screens & components
│       │   │   ├── 📁 home
│       │   │   ├── 📁 calendar
│       │   │   ├── 📁 stats
│       │   │   ├── 📁 settings
│       │   │   └── 📁 theme     # Material 3 theming
│       │   └── 📁 utils         # Extensions & helpers
│       └── 📁 res               # Resources (icons, strings)
├── 📄 build.gradle.kts
├── 📄 settings.gradle.kts
└── 📄 README.md
```

---

## 🗺️ Roadmap

- [x] Project setup & architecture
- [x] Task CRUD operations
- [x] Categories & priority labels
- [ ] Calendar integration
- [ ] Push notification reminders
- [ ] Recurring task scheduling
- [ ] Productivity analytics dashboard
- [ ] Widget support
- [ ] Cloud sync (Firebase)
- [ ] Multi-language support (i18n)

> 💬 Have a feature idea? [Open a feature request!](https://github.com/your-username/task-management-app/issues/new?labels=enhancement&template=feature_request.md)

---

## 🤝 Contributing

Contributions make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**!

1. **Fork** the repository
2. **Create** your feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add some AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

> Please read the [Contributing Guidelines](CONTRIBUTING.md) before submitting a PR.

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

## 📬 Contact

<div align="center">

**Aryan Sharma**

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/your-username)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/your-profile)
[![Email](https://img.shields.io/badge/Email-EA4335?style=for-the-badge&logo=gmail&logoColor=white)](mailto:your-email@example.com)

</div>

---

<div align="center">

### ⭐ If you found this project useful, please consider giving it a star!

<img src="https://img.shields.io/github/stars/your-username/task-management-app?style=social" />

<br/>

Made with ❤️ and ☕ by **Aryan Sharma**

</div>
