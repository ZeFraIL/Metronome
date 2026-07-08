# 📱 Android Application Documentation (LEVEL 10/10)

________________________________________
## 🧾 General Information
**Project Name:**
Metronome
**Author(s):**
Zeev Fraiman
**Date:**
May 2024
**Language:**
Java
**Development Environment:**
Android Studio
**Android Version (minSdk / targetSdk):**
28 / 36

________________________________________
## 🎯 Project Goal
*   **What problem does the app solve:** Provides musicians with a reliable and precise metronome for practicing rhythm.
*   **Why is this task important:** Rhythm is the foundation of music. A precise metronome is essential for both beginners and professional musicians.
*   **Target Audience:** Musicians, music students, and teachers.

________________________________________
## 📌 Application Requirements
### Functional Requirements
*   Audible "tick" sound at a specified BPM.
*   Visual pendulum animation synchronized with the sound.
*   BPM adjustment via SeekBar.
*   BPM adjustment by dragging the weight on the virtual pendulum.
*   Start/Stop functionality.
*   Display of musical tempo names (Grave, Allegro, etc.).

### Non-functional Requirements
*   **Performance:** High-precision timing for the metronome ticks.
*   **Usability:** Simple and intuitive interface mimicking a classic mechanical metronome.
*   **Reliability:** Stable operation without drifting from the set tempo.

________________________________________
## 🧠 General Architecture
*   **Approach:**
    *   Simple MVC (Model-View-Controller).
*   **Why this approach:** Given the project's size, a full MVVM would be overkill. The current structure clearly separates the custom drawing logic (View) from the orchestration logic (Controller).
*   **Core System Components:**
    *   `MainActivity`: Acts as the Controller.
    *   `MetronomeView`: Custom View (View).
    *   `SoundPool` & `ScheduledExecutorService`: Data/Logic layer for sound.

________________________________________
## 🧩 UML Diagram
`[MainActivity] –> [MetronomeView]`
`[MainActivity] –> [SoundPool / ScheduledExecutorService]`

________________________________________
## 📁 Package Structure
*   `zeev.fraiman.metronome`: Contains all core logic and UI components.
*   **Scaling:** For a small utility app, a flat structure is efficient. If expanded (e.g., adding a library of rhythms), we would move logic to a `domain` package and UI to a `ui` package.

________________________________________
## 🧩 Detailed Class Descriptions
### 📌 Class: MainActivity
*   **Role:** Entry point and controller of the application.
*   **Responsibility:** Initializing components, handling user input, managing the lifecycle of the metronome sound and animation.
*   **Main Methods:**
    *   `onCreate()` — Sets up UI and initializes SoundPool.
    *   `startMetronome()` — Starts the sound scheduler and visual animator.
    *   `stopMetronome()` — Stops all active processes.
    *   `calculateDuration()` — Logic for converting BPM to milliseconds.
*   **Interaction with other classes:** Communicates with `MetronomeView` to update visuals and receives BPM change events from it.

### 📌 Class: MetronomeView
*   **Role:** Custom UI component.
*   **Responsibility:** Drawing the mechanical metronome interface, the pendulum, and the tempo scale.
*   **Why it is used:** To provide a unique, interactive experience that mimics a real metronome.

________________________________________
## 🔄 Application Workflow
1.  User opens the app.
2.  User adjusts BPM using the slider or by dragging the pendulum weight.
3.  User presses "Start".
4.  `ScheduledExecutorService` begins playing sounds at precise intervals.
5.  `ValueAnimator` moves the pendulum in sync with the sound.
6.  User presses "Stop" to halt both sound and animation.

________________________________________
## 🎨 UI/UX Analysis
*   **Design Choice:** The interface is designed to resemble a classic mechanical metronome to provide a familiar feel to musicians.
*   **Principles Used:**
    *   **Simplicity:** Minimal buttons, clear controls.
    *   **Logic:** The physical metaphor (dragging the weight) is intuitive.
    *   **Accessibility:** Large text for BPM display.
*   **Improvements:** Adding themes (light/dark) or custom sound samples.

________________________________________
## ⚙️ Threading
*   **Used:**
    *   `ScheduledExecutorService`: Used for playing sounds to ensure high precision regardless of UI load.
    *   `ValueAnimator`: Runs on the Main Thread for smooth UI updates.
*   **Why this choice:** Standard `Handler` or `Thread.sleep` can be imprecise. `ScheduledExecutorService` is more reliable for rhythmic tasks.
*   **Prevention:**
    *   **ANR:** Long-running sound logic is offloaded from the main thread.
    *   **Memory Leaks:** Resources are released in `onDestroy()`.

________________________________________
## 💾 Data Handling
*   **Storage:** The app currently doesn't persist data between sessions.
*   **Reason:** For a simple metronome, default values are often sufficient.
*   **Ensuring correctness:** Input BPM is clamped between 40 and 208.

________________________________________
## 🌐 Networking
*   Not applicable for this project.

________________________________________
## 🔐 Security
*   No sensitive data is processed or stored.

________________________________________
## 🧪 Testing
*   **Unit Tests:** Default `ExampleUnitTest`.
*   **UI Tests:** Default `ExampleInstrumentedTest`.
*   **What is checked:** Basic context and environment setup.

________________________________________
## 🐞 Error Handling
*   `SoundPool` load listener ensures sounds are only played after they are ready.
*   Null checks for animator and scheduler before cancellation.

________________________________________
## ⚡ Performance
*   **Optimizations:** Use of `SoundPool` for low-latency audio.
*   **Potential Bottlenecks:** Very high BPMs might stress the UI thread's animation if not handled carefully, but 208 BPM is well within limits.

________________________________________
## 🚀 Expansion Possibilities
*   Adding "Tap Tempo" feature.
*   Different time signatures (3/4, 6/8, etc.).
*   Save presets for different songs.
*   Vibration feedback on the beat.
