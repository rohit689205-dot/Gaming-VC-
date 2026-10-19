# Implementation Plan: RoomLobby Component, Recent Rooms Dashboard & Error Resolution

Create a dedicated `RoomLobby` component featuring a live camera preview feed with real-time audio/video controls and a 'Join Call' action before entering the main session. Update the dashboard so that the "Popular Rooms" section is replaced with "Recent Rooms" (tracking rooms the user joins), and ensure robust error handling and lifecycle cleanup.

## User Review Required

> [!IMPORTANT]
> - **Camera Preview in Lobby**: Front camera stream will start immediately when camera permission is granted. When camera is toggled off or permission is denied, an avatar placeholder with an audio wave indicator will be shown.
> - **Recent Rooms Behavior**: The Dashboard will display "Recently Joined Rooms" populated from the user's active session history, seeded with default squad rooms ("BGMI Squad", "Valorant Chill", "Free Fire Legends", "Minecraft Builders") so the list is always populated.

---

## Proposed Changes

### 1. Dedicated `RoomLobby` Component (`LobbyScreen.kt` & `GamingComponents.kt`)
- Build a reusable `RoomLobby` composable:
  - **Camera Preview Surface**: Uses `AndroidView` with CameraX `PreviewView`, front camera by default, and responsive aspect ratio.
  - **Live Controls Overlay**: Mute/unmute microphone button, camera on/off toggle, and camera flip/switch button.
  - **Interactive 'Join Call' Button**: Primary gradient action button with loading state that joins the room session and transitions to `LiveCallScreen`.
  - **Room Details & Squad Info**: Displays room title, category badge, live participant count, and participant avatars.
  - **Safe Lifecycle Management**: Releases `CameraManager` on disposal or when navigating back to avoid camera resource leaks.

### 2. Dashboard: Replace "Popular Rooms" with "Recent Rooms" (`DashboardScreen.kt` & `CallViewModel.kt`)
- Update `DashboardScreen.kt`:
  - Rename the section header from "Popular Rooms" to "Recent Rooms".
  - Change the list to show recently joined rooms, with time badges (e.g., "Active now", "Joined 10m ago").
  - Connect room card tap actions to open the `RoomLobby` preview.
- Update `CallViewModel.kt`:
  - Track `recentRooms: List<RoomSession>` in `CallUiState`.
  - Add `recordRecentRoom(room: RoomSession)` when entering or creating a room.
  - Cache recent rooms in `SessionManager` so they persist across app restarts.

### 3. Error Prevention & Quality Fixes
- Fix any potential permissions crashes by providing clear rationale and in-lobby permission request buttons for Camera and Microphone.
- Ensure camera preview properly pauses/resumes with Android lifecycle events.
- Validate that all unit and Robolectric tests pass (`gradle :app:testDebugUnitTest`).

---

## Verification Plan

### Automated Tests
- Run `gradle :app:testDebugUnitTest` to verify unit and Robolectric tests.
- Run `compile_applet` to verify compilation.

### Manual Verification
1. Navigate to any room from Dashboard ("Recent Rooms") or Room Discovery -> confirm `RoomLobby` opens.
2. Verify camera preview appears with front camera feed and controls (mic toggle, camera toggle, switch lens).
3. Tap 'Join Call' -> verify smooth transition into `LiveCallScreen`.
4. Verify Dashboard displays "Recent Rooms" with the latest room joined.
