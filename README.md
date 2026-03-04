# 🥷 Ninja Bubble

A cross-platform mobile game built with **Kotlin Multiplatform** and Compose Multiplatform — runs on both Android and iOS from a single shared codebase.



---

## Tech Stack & Skills Demonstrated

**Kotlin Multiplatform (KMP)**
- Shared game logic, domain models, and UI across Android and iOS
- `expect` / `actual` pattern for platform-specific implementations (audio, platform info)
- Compose Multiplatform for a single shared UI layer

**Architecture**
- Koin dependency injection with platform-specific modules (`androidMain` / `iosMain`)
- Clean domain layer — `Target` interface with `EasyTarget`, `MediumTarget`, `StrongTarget` subtypes
- Sealed `GameStatus` and `MoveDirection` enums driving UI state

**Compose & Animation**
- Canvas-based game rendering — targets, weapons, and ninja drawn every frame
- `Animatable` for smooth target descent and ninja movement
- `withFrameMillis` game loop for frame-synced updates
- Sprite sheet animation via a third-party sprite library

**Game Systems**
- Collision detection using distance formula between weapon and target radii
- Progressive difficulty — 5 levels with increasing target and weapon speed
- Multi-hit targets (Medium = 2 hits, Strong = 3 hits) with radius growth on damage
- Gesture detection via custom `AwaitPointerEventScope` extension — left/right swipe to move

**Audio**
- `ExoPlayer` on Android, `AVAudioPlayer` on iOS — both injected via Koin

---

## Gameplay
- Move the ninja left and right by swiping
- Running automatically fires kunai upward
- Destroy bubbles before they reach the bottom
- Easy = 1pt, Medium = 3pts, Strong = 5pts — don't let any through!
![gameplay](https://github.com/user-attachments/assets/c4358661-aceb-4410-b52d-50ecfd85b909)

---


