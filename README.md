# SecureBox 🔐

SecureBox is a modern, feature-rich Android file manager built with **Jetpack Compose**. It provides an intuitive interface for browsing, managing, and organizing your files with advanced features like image thumbnails, smart recents, and type-safe file operations.

## ✨ Features

### 📁 File Management
- **Smart File Browser:** Navigate through directories with smooth animations and modern UI
- **Image Thumbnails:** Instant preview of images using Coil's efficient loading and caching
- **Recent Files:** Unified recents showing recently downloaded, modified, and opened files
- **Pagination:** Smooth infinite scroll with pull-to-refresh support (30 items per page)
- **Quick Actions:** Long-press or tap three-dot menu for file operations

### 🎨 User Interface
- **Material Design 3:** Modern, beautiful UI following latest Material guidelines
- **Adaptive Thumbnails:** Different previews for images, folders, and documents
- **Bottom Sheets:** Smooth modals for file operations with image previews
- **Empty States:** Helpful messages and icons when no files are present
- **Storage Categories:** Quick access cards for Downloads, Pictures, Documents, Audio, Videos, and More

### 🛠️ File Operations
- **Rename:** Edit file names with extension protection (prevents accidental format changes)
- **Delete:** Safe deletion with confirmation dialogs for files and folders
- **Result-based Error Handling:** Clear, actionable error messages:
  - "File not found" instead of generic "Failed"
  - "Permission denied" with context
  - "Name already exists" for duplicates
  - "Invalid characters" for special symbols

### ⚡ Performance
- **Lazy Loading:** Only loads visible files, saving memory
- **Thumbnail Caching:** Images load instantly after first view (Coil memory + disk cache)
- **Background Operations:** File operations don't block UI
- **Optimized Pagination:** Prefetches next page 5 items before scroll end

## 🏗️ Architecture

### Clean Architecture
- **MVVM Pattern:** ViewModels manage UI state and business logic
- **Repository Pattern:** Centralized data access layer
- **Unidirectional Data Flow:** Immutable state with sealed classes

### Error Handling
- **Result<T> Pattern:** Type-safe error handling throughout the app
- **Specific Exceptions:** FileNotFoundException, SecurityException, IllegalArgumentException
- **User-Friendly Messages:** Context-aware error messages for each failure case

## 🧰 Tech Stack

### Core
- **Kotlin** - Modern, concise programming language
- **Jetpack Compose** - Declarative UI framework
- **Coroutines** - Asynchronous programming
- **Flow** - Reactive state management

### Android Jetpack
- **Hilt** - Dependency injection
- **Navigation Compose** - Type-safe navigation
- **ViewModel** - Lifecycle-aware state management
- **Lifecycle Runtime** - Compose lifecycle integration

### Libraries
- **Coil 3** - Async image loading with automatic resizing and caching
- **Kotlinx Serialization** - Type-safe data serialization
- **Material Icons Extended** - Rich iconography
- **Google Fonts** - Custom typography

### Storage
- **File API** - Native Android file system access
- **MediaStore** (planned) - System media queries for enhanced recents

## 📱 Screenshots

*(Will Add Soon)*
