# Secure Box Project Context

This file is a handoff note for a new AI/chat session. Read it before making changes.

## Project Identity

Secure Box is an Android file manager built with Kotlin and Jetpack Compose. The goal is not to feel like an old-school file explorer full of clutter. The app should feel clean, calm, fast, visual, and direct: open it, see useful recent files, tap a category, browse or act on files, and get out.

The public tagline used in the README is:

> A file manager that doesn't feel like one.
>
> No clutter. No bloat. Just your files, shown the way you want.

The app is a learning project, but the standard is still production-minded. Avoid tutorial-ish shortcuts, messy callbacks, broad rewrites, or generic Android sample-app design.

## Current Product Behavior

The home screen shows storage categories and recent files.

Categories are intentionally split by meaning:

- Downloads opens the actual Downloads directory.
- Internal Storage opens the root external storage directory for filesystem browsing.
- Images does not open DCIM as a folder. It opens a phone-wide image collection, like a gallery, sorted latest first.
- Videos does not open Movies as a folder. It opens all video files from MediaStore, sorted latest first.
- Music opens all audio files from MediaStore, sorted latest first.
- Documents opens document files from MediaStore, sorted latest first.

This distinction matters a lot. Media categories must mean "show me this kind of file across the phone", not "open a directory that may or may not contain this kind of file".

Files can be opened, renamed, copied, moved, and deleted. Long press or the three-dot action opens file operations. Copy and move use a destination picker with a bottom "confirm/paste here" style action.

The app requires Android 11+ because it uses broad storage access via `MANAGE_EXTERNAL_STORAGE`. It works best on a real Android device because emulators often have empty or unrealistic storage.

## Stack

- Language: Kotlin 2.3.21
- Android Gradle Plugin: 9.2.1
- UI: Jetpack Compose with Material 3
- Compose BOM: 2026.05.01
- Dependency injection: Hilt
- Navigation: Navigation3 with a simple back stack
- Async/state: Coroutines, Flow, StateFlow
- Images/thumbnails: Coil 3
- Serialization: Kotlinx Serialization 1.11.0, Kotlin serialization plugin 2.3.21

Version catalog:

- `gradle/libs.versions.toml`

Main app module:

- `app/`

## Architecture

The app follows MVVM with a repository layer.

High-level flow:

- Composables render state and send user actions upward.
- ViewModels own UI state, loading, pagination, events, and operation choices.
- `FileRepository` talks to MediaStore and the filesystem.
- Shared operations are centralized in `SharedFileOperationsViewModel`.
- Navigation state is centralized in `NavigationViewModel`.

Important files:

- `app/src/main/java/com/romit/securebox/navigation/SecureBoxApp.kt`
  Main app shell. Owns the Scaffold, app top bar, bottom operation bar, Navigation3 `NavDisplay`, snackbar host, and global dialogs/bottom sheets.

- `app/src/main/java/com/romit/securebox/navigation/Screen.kt`
  Sealed routes: `Home`, `FileBrowser`, `MediaCollection`, `AllRecents`, `DestinationScreen`.

- `app/src/main/java/com/romit/securebox/data/repository/FileRepository.kt`
  Source of truth for file queries and file operations. Uses MediaStore for recents/media and `java.io.File` for filesystem browsing and operations.

- `app/src/main/java/com/romit/securebox/util/StorageHelper.kt`
  Storage categories, file icon mapping, size formatting, date formatting, MIME helpers.

- `app/src/main/java/com/romit/securebox/presentation/sharedViewmodel/SharedFileOperationsViewModel.kt`
  Shared state for rename/delete/copy/move/create-folder flows.

- `app/src/main/java/com/romit/securebox/presentation/sharedViewmodel/NavigationViewModel.kt`
  Navigation back stack actions.

## Feature Structure

Feature screens live under `presentation/feature...`.

Current feature folders:

- `featureHome`
  Home screen, categories, recents preview, loading/empty states.

- `featureBrowse`
  Directory browser for actual folder navigation. Used by Downloads, Internal Storage, and normal folder clicks.

- `featureMedia`
  Media collection screen for Images, Videos, Music, and Documents. This is not folder browsing. It uses MediaStore and pagination.

- `featureRecents`
  Full recents screen with pagination.

- `featureDestination`
  Folder picker used when copying or moving files.

Shared reusable components live under `components/`, divided by category:

- `components/app`
  Root-level app shell components such as `AppTopBar`, `FileOperationBottomAppBar`, `GradientBackground`.

- `components/file`
  File and folder UI: `FileCard`, `FolderCard`, `FileThumbnail`, `FileDetailsPane`.

- `components/storage`
  Storage/category UI.

- `components/dialogs`
  Dialog orchestration and dialogs for create folder, delete, rename.

- `components/sheets`
  Bottom sheet and operation row UI.

- `components/experimental`
  Experiments only. Do not put production UI here unless the experiment is being promoted.

Rule of thumb: if many screens use it, it belongs in `components`. If only one feature uses it, keep it inside that feature folder.

## Media And Pagination

Media collections are implemented in:

- `presentation/featureMedia/MediaFilesScreen.kt`
- `presentation/featureMedia/MediaFilesViewModel.kt`
- `presentation/featureMedia/MediaFilesUiState.kt`
- `FileRepository.getMediaFiles(...)`

Pagination exists in the media screen and ViewModel.

Current behavior:

- Page size is 30.
- First load queries newest files with `lastTimestamp = null`.
- Next pages use the last loaded file's `lastModified` timestamp.
- Query adds `DATE_MODIFIED * 1000 < lastTimestamp`.
- Sort order is `DATE_MODIFIED DESC`.
- End is reached when a returned page has fewer than 30 items.
- UI loads the next page when the LazyColumn reaches the bottom.

Recents also use pagination:

- `presentation/featureRecents/AllRecentsScreen.kt`
- `presentation/featureRecents/AllRecentsScreenViewModel.kt`
- `FileRepository.getRecentFiles(lastTimestamp, pageSize)`

Keep media categories and filesystem browsing separate. Do not make Images/Videos/Music/Documents navigate to folders unless the product direction explicitly changes.

## File Icons

The app now uses custom bitmap artwork for file icons instead of Material 3 generic file icons.

Source artwork folder:

- `App File Icons/`

Android resource outputs:

- `app/src/main/res/drawable-nodpi/app_file_icon_pdf.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_docx.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_xlsx.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_ppt.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_txt.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_video.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_zip.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_code.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_kotlin.webp`
- `app/src/main/res/drawable-nodpi/app_file_icon_html.webp`
- `app/src/main/res/drawable-nodpi/app_folder_icon.webp`

Mapping lives in:

- `StorageHelper.getFileIconRes(file)`

Current mapping includes:

- PDF
- Word/doc/docx/odt
- Spreadsheet/xls/xlsx/ods/csv
- Presentation/ppt/pptx/odp
- Archives/zip/rar/7z/tar/gz/tgz
- Video formats and video MIME types
- Kotlin files: `kt`, `kts`
- HTML files: `html`, `htm`
- Generic code formats
- Text formats

Folders use the custom `app_folder_icon` image. File thumbnails should generally show icons as-is, without an extra surface container. The bottom sheet is the exception: it can keep its framed thumbnail styling if needed by the design.

Images still use real thumbnails through Coil when `file.isImage` is true.

## Visual Design Direction

The design direction is calm, polished, native Android, and useful. It should not feel like a marketing landing page inside the app. It should feel like a real tool that has taste.

Preferences:

- Material 3 color system.
- Dynamic color is enabled when available.
- Light and dark themes should both work.
- Keep surfaces clean and restrained.
- Use rounded shapes but avoid making everything bubbly.
- Repeated file items use grouped list shapes through `getListItemShape`.
- Avoid visual clutter.
- Prefer actual file icons and image thumbnails over generic icons.
- Do not wrap every thumbnail in extra decorative containers.
- Keep spacing comfortable but not wasteful.
- Text should be direct and human, not corporate.

README style:

- The README starts with a centered logo, title, subtitle, and badges using `<div align="center">`.
- The quote/tagline follows under the centered header.
- Screenshot and icon showcases should be visually centered and laid out left-to-right in tables.
- Use simple HTML tables in README when GitHub Markdown alignment is unreliable.
- Avoid obsolete HTML image attributes where possible; prefer `style="width: ..."` in existing README tables.

## Typography And Fonts

Theme files:

- `app/src/main/java/com/romit/securebox/ui/theme/Type.kt`
- `app/src/main/java/com/romit/securebox/ui/theme/Theme.kt`
- `app/src/main/java/com/romit/securebox/ui/theme/Color.kt`

Current custom display font:

- `app/src/main/res/font/bricolage_grotesque_variable.ttf`

This replaced Source Serif. The old Source Serif files are not meant to be used anymore.

Important variable font detail:

- Bricolage Grotesque's variable font file has a default `wght` around 800, so text can look bold if the `wght` axis is not explicitly driven.
- `CustomFontFamily` should use `FontVariation.Settings(...)` to map Compose `FontWeight` to the variable font weight axis.
- If text still looks too bold, try `FontWeight.ExtraLight` or confirm the composable is actually using `CustomFontFamily`.

Current use:

- `headlineMedium` uses `CustomFontFamily`.
- `titleLarge` uses `CustomFontFamily`.
- Other typography mostly uses Roboto via Google Fonts provider.

## File Operations

File operations return `Result<String>` from repository/use-case style APIs. Preserve specific failures where possible:

- File not found
- Permission denied/no write permission
- Name already exists
- Invalid name
- Insufficient storage
- Delete/rename/copy/move failures

Do not replace these with vague "Something went wrong" errors at the repository layer. UI can still show a generic fallback only when the exception does not have a better message.

Use cases exist under:

- `domain/usecases/CopyFileUseCase.kt`
- `domain/usecases/CreateFolderUseCase.kt`
- `domain/usecases/DeleteFileUseCase.kt`
- `domain/usecases/GetDirectoriesUseCase.kt`
- `domain/usecases/MoveFileUseCase.kt`
- `domain/usecases/RenameFileUseCase.kt`

The project is moving toward cleaner domain use cases, but some logic still lives directly in `FileRepository` and ViewModels.

## Navigation Rules

Navigation3 is used with a simple list back stack.

Main routing rules:

- Home category click:
  - Downloads and Internal Storage -> `Screen.FileBrowser(path)`
  - Images, Videos, Music, Documents -> `Screen.MediaCollection(type)`

- File click:
  - Directory -> `Screen.FileBrowser(file.path)`
  - Normal file -> `openFile(context, file)`

- All recents:
  - Open file or navigate into directories using the same rule as file browser.

- Destination picker:
  - Used only for choosing folders during copy/move operations.

Animations are currently removed in `NavDisplay` using `EnterTransition.None` and `ExitTransition.None`.

## README And Public Presentation

README is meant to be polished and visually strong.

It currently includes:

- Centered Secure Box logo
- Title and badge row
- Product tagline
- What the app does
- Screenshots table
- File icon showcase table
- How it is built
- Stack table
- Work-in-progress checklist
- Run instructions
- Centered closing line

Keep the README high quality. If adding new assets, show them nicely. Avoid huge unformatted blocks or left-heavy icon galleries.

## Git And Generated Artifacts

`.gitignore` should ignore build artifacts, APK outputs, and baseline profile artifacts. Do not commit generated APKs or baseline profile outputs.

Be careful with generated files and Android Studio build directories:

- `app/build/`
- `build/`
- `.gradle/`
- generated APKs
- generated baseline profile artifacts

These should not be part of normal source commits.

## Current Known Caveats

- Gradle compile may fail on this machine if Windows blocks the Gradle wrapper lock file in `C:\Users\4444444\.gradle\wrapper\dists\...gradle-9.4.1-bin.zip.lck`.
- If that happens, it is a local permission/lock issue, not necessarily a Kotlin code issue.
- The project currently has local uncommitted font changes around replacing Source Serif with Bricolage Grotesque.

## Development Style For Future AI Work

Before changing code:

- Read the relevant feature and shared component files first.
- Preserve the existing app structure.
- Keep changes focused.
- Do not rewrite unrelated architecture.
- Do not revert user changes.
- Prefer `rg` for search.
- Use `apply_patch` for edits.
- Verify with compile/tests when Gradle is available.

When implementing UI:

- Match the existing Material 3 Compose style.
- Prefer reusable shared components for repeated UI.
- Keep feature-only components inside feature folders.
- Use the custom file/folder artwork where possible.
- Keep media collection behavior gallery-like.
- Keep filesystem browsing behavior folder-like.

When writing commit messages:

- Be detailed enough to explain product intent, especially for UX changes.
- Mention behavior changes, architecture changes, assets/resources, and verification.
- Keep the subject concise, but make the body useful.

## Product North Star

Secure Box should make files feel obvious, visual, and under control.

The user should not have to think:

- "Why did Images open DCIM?"
- "Why does Videos show an empty Movies folder?"
- "What type of file is this generic icon?"
- "Where do I paste after choosing copy?"

The app should answer those questions through behavior and design.

