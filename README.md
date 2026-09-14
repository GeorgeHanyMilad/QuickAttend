# QuickAttend – Student Attendance App for Eng. George Hany

**QuickAttend** is a mobile-first, 100% offline Android application designed specifically for Teaching Assistants to quickly record student attendance during university sections.

## 📱 Features

- **Branding Header**: Header title clearly displays **`Student Attendance App – Eng. George Hany`**.
- **100% Offline**: Zero external APIs, zero cloud databases, no Firebase, and no user accounts required.
- **Single-Student Attendance Screen**:
  - Displays **only ONE student at a time** for ultra-fast, focused calling.
  - Large **`[ ✓ Present ]`** (Green) and **`[ ✕ Not Present ]`** (Red) touch targets.
  - Real-time progress tracker (e.g. `Student 12 of 32` / `12 / 32`).
  - Real-time counters (`Present: 10`, `Not Present: 2`).
- **Reliable Undo**: Clear **`[ ↶ Undo ]`** button to return to the previous student and correct any mistakes without losing recorded history.
- **Auto Save & Resumable Sessions**: Every action is saved instantly in local Room SQLite database. If the app closes or the device restarts, tap **`[ Continue Attendance ]`** on the home screen to pick up exactly where you left off.
- **Excel (.xlsx / .xls) & Text (.csv / .txt) Import**:
  - Upload student lists directly from device storage.
  - Intelligent automatic column header detection (searches for "ID", "Code", "Name", "Student", etc.).
  - Fallback Column Mapping screen if column headers are ambiguous.
- **Local Excel Export & Share Sheet**:
  - Exports a clean, formatted `.xlsx` file locally on the device containing Present student details and summary metadata (Section Name, Date, Start Time, End Time, Present/Absent counts, Attendance Rate %).
  - Integrates with Android Share Sheet to share reports instantly via WhatsApp, Gmail, Google Drive, Telegram, etc.
- **Strict Light Mode UI**: Modern Material 3 interface optimized for one-handed use during sections.

---

## 🛠️ Tech Stack

- **Language**: Kotlin 1.9.22
- **UI Framework**: Jetpack Compose + Material 3
- **Local Database**: Room SQLite Persistence Library
- **Excel Processing**: Apache POI (`poi-ooxml`)
- **File Sharing**: Android `FileProvider` (`content://`)
- **Target SDK**: Android 14 (API level 34) / Min SDK: API 26 (Android 8.0)

---

## 🚀 Building the APK

### Option 1: Automatic Build via GitHub Actions (Recommended)
1. Push this codebase to a GitHub repository (`main` or `master` branch).
2. GitHub Actions will automatically trigger the workflow defined in `.github/workflows/build.yml`.
3. Go to the **Actions** tab on GitHub, click the latest build run, and download the compiled **`QuickAttend-Debug-APK`** or **`QuickAttend-Release-Unsigned-APK`** artifact directly.

### Option 2: Local Build via Android Studio
1. Open **Android Studio** (Newer version supporting AGP 8.2+).
2. Select **Open** and choose the `QuickAttend` project folder.
3. Allow Gradle to sync.
4. Click **Build > Build APK(s)** or click **Run** to launch directly on your Android device or emulator.

---

## 📁 Project Structure

```
QuickAttend/
├── app/
│   ├── src/main/java/com/georgehany/quickattend/
│   │   ├── MainActivity.kt               # Main Compose UI Entry Point
│   │   ├── QuickAttendApp.kt             # Application class
│   │   ├── data/
│   │   │   ├── local/                    # Room DB (Session, Student, AttendanceRecord)
│   │   │   ├── parser/                   # FileImporter (Excel & Text parser + Column Detector)
│   │   │   └── export/                   # ExcelExporter (Local .xlsx generator & Share intent)
│   │   └── ui/
│   │       ├── theme/                    # Forced Light Theme & Material 3 palette
│   │       ├── viewmodel/                # MainViewModel
│   │       └── screens/                  # HomeScreen, AttendanceScreen, SummaryScreen, Dialogs
│   └── src/main/res/                     # Icons, FileProvider XML, Strings
├── .github/workflows/build.yml            # CI/CD pipeline for APK generation
├── build.gradle.kts                       # Top-level build configuration
├── settings.gradle.kts
└── README.md
```
