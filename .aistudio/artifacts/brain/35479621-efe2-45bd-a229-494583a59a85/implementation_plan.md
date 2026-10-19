# System Notifications & In-App Alerts for VideoCall

Deliver an integrated, real-time notification system covering both device-level Android status bar notifications (with high-priority heads-up alerts and direct action buttons) and in-app animated notification banners for incoming video call invitations and friend requests.

## User Review & Critical Decisions

> [!IMPORTANT]
> Based on your selections, notifications will be delivered across two complementary tiers:
> 1. **System Status Bar Notifications**: Using Android's `NotificationManager` with high-importance notification channels, heads-up banners, and interactive action buttons ("Join VC", "Accept", "Decline").
> 2. **In-App Live Banners**: Animated cards appearing at the top of the Dashboard and active Video Call screen with instant 1-tap actions.

- **Confirmed Channels & Priority**: High-priority channel for incoming video call invitations (`call_invitations_channel`) and default-priority channel for friend requests (`friend_requests_channel`).
- **Confirmed Interactive Actions**: Notifications will feature actionable buttons directly on the Android notification tray (`Join VC`, `Accept`, and `Decline`).
- **Target OS Compatibility**: Android 13+ (API 33+) runtime `POST_NOTIFICATIONS` permission integration with graceful pre-permission explanation.

---

## 1. Overview & Core Concept

### What It Does
When a user receives a video call invitation or a friend request, the app immediately raises:
- An **Android System Notification** in the system tray / lock screen with heads-up display and interactive buttons (`Join VC`, `Accept`, `Decline`). Tapping `Join VC` launches the app directly into the meeting lobby/call.
- An **In-App Toast/Banner** displayed at the top of the active screen with instant response controls, ensuring users never miss a call while inside or outside the app.

### Target Audience & Persona
- Call hosts and participants collaborating in real-time who need immediate, interruptive alerts when invited to an active video meeting or connected with a colleague.

### Key Value
- Zero missed meetings: eliminates the need to manually refresh or copy-paste room codes when invited by friends.

---

## 2. User Experience & Visual Design

### Key User Flows

```
[Remote User sends VC Invite / Friend Req]
                    │
                    ▼
       [Firestore Real-Time Listener]
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
[In-App Floating Banner]  [Android System Tray Alert]
 ├─ "Join VC" button       ├─ Heads-up notification
 ├─ "Accept" button        ├─ Action: "Join VC" / "Accept"
 └─ "Decline" button       └─ Action: "Decline"
```

1. **Incoming Video Call**:
   - Device displays a high-priority heads-up notification with phone/video icon: *"Raj invited you to Video Call (vid-839-201)"*.
   - User can tap **"Join VC"** directly from the notification shade to launch the app directly into that room.
2. **Incoming Friend Request**:
   - Device displays an alert: *"New friend request from Alex (UID: guest_492018)"*.
   - User can tap **"Accept"** to immediately add the friend, or **"Decline"**.
3. **In-App Floating Banner**:
   - Styled with dark slate background (`SlateNavy`), neon accent borders (`VibrantCyan` / `ActiveGreen`), and smooth enter/exit animations (`fadeIn` + `slideInVertically`).

### Visual Palette & Components
- **Call Alert Color**: `ActiveGreen` (#10B981) for Join actions.
- **Decline Color**: `CallRed` (#EF4444) for Decline/Cancel actions.
- **Brand Accents**: `RoyalViolet` (#6366F1) and `VibrantCyan` (#06B6D4).
- **Notification Icons**: Clean Material vector icons (`ic_videocam`, `ic_person_add`, `ic_call`).

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Native Notification Channels & PendingIntents**
  - *Chosen Approach*: Create two distinct `NotificationChannel`s (`CALL_CHANNEL_ID` with `IMPORTANCE_HIGH` and `FRIEND_CHANNEL_ID` with `IMPORTANCE_DEFAULT`). Use `PendingIntent.getActivity` with explicit intent extras (`ACTION_JOIN_ROOM`, `EXTRA_ROOM_ID`).
  - *Why*: High importance allows incoming call notifications to display as heads-up popups on top of other apps or lock screen.
  - *Alternatives Considered*: Standard Toast only (insufficient when app is in background).

- **Decision 2: Seamless Deep-Link Intent Handling in MainActivity**
  - *Chosen Approach*: Handle incoming action intents in `MainActivity.onCreate` and `MainActivity.onNewIntent`. When an action button like "Join VC" is clicked from the system notification, `MainActivity` immediately signals `CallViewModel.prepareJoinRoom(roomId)`.
  - *Why*: Frictionless 1-tap join experience straight from the lock screen or notification shade.

- **Decision 3: Deduping and Lifecycle-Aware Alerts**
  - *Chosen Approach*: Maintain a local registry of active notification IDs so repeated Firestore snapshot emissions don't trigger duplicate chimes or duplicate tray entries.

---

## 4. Technical Architecture & Implementation Strategy

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    CallRepository                           │
│  - observeIncomingFriendRequests()                          │
│  - observeCallInvitations()                                 │
└──────────────────────────────┬──────────────────────────────┘
                               │ Flow emissions
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                      CallViewModel                          │
│  - Tracks new incoming IDs                                  │
│  - Calls AppNotificationManager.notifyIncomingCall()        │
│  - Calls AppNotificationManager.notifyFriendRequest()       │
└──────────────┬───────────────────────────────┬──────────────┘
               │                               │
               ▼                               ▼
┌──────────────────────────────┐┌──────────────────────────────┐
│    AppNotificationManager    ││   Compose In-App Banners     │
│ - NotificationCompat.Builder ││ - AnimatedVisibility cards   │
│ - Channels: CALLS & FRIENDS  ││ - Top of Dashboard & LiveCall│
│ - Action PendingIntents      ││ - 1-tap Accept/Decline/Join  │
└──────────────┬───────────────┘└──────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────────────┐
│             MainActivity (Intent Dispatcher)                │
│  - Reads ACTION_JOIN_ROOM & EXTRA_ROOM_ID                   │
│  - Routes user directly to Lobby/Live Call                  │
└─────────────────────────────────────────────────────────────┘
```

### Component Breakdown

1. **`AppNotificationManager.kt`**:
   - `createNotificationChannels(context)`: Registers `calls_channel` and `friends_channel`.
   - `showCallInvitation(context, invitation)`: Dispatches notification with `Join VC` and `Dismiss` actions.
   - `showFriendRequest(context, request)`: Dispatches notification with `Accept` and `Decline` actions.
   - `cancelNotification(context, notificationId)`.

2. **`MainActivity.kt`**:
   - Runtime `POST_NOTIFICATIONS` permission check on launch.
   - Handles `Intent` extras from notification taps to auto-join rooms or open the friends tab.

3. **`CallViewModel.kt`**:
   - Triggers `AppNotificationManager` whenever a new call invitation or friend request is received.
   - Clears notifications when an invite or request is accepted/dismissed in-app.

4. **In-App Banners (`DashboardScreen.kt` & `LiveCallScreen.kt`)**:
   - Visual parity with system notifications, ensuring consistent UX whether inside the app or multi-tasking.
