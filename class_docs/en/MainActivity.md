# Class Description: MainActivity

## 1. General Information
*   **Class Name:** `MainActivity`
*   **Type:** Activity
*   **Purpose:** This is the "brain" of the application. It manages the user interface, coordinates between the visual pendulum and the ticking sound, and handles the overall lifecycle of the metronome (starting, stopping, and changing speed).
*   **Interaction:** It communicates with `MetronomeView` to update the visual animation and receives updates from it when the user drags the pendulum. It also uses Android's `SoundPool` for audio and `ScheduledExecutorService` for precise timing.

## 2. Variables (Class Fields)

| Name | Type | Purpose | Where is it used |
| :--- | :--- | :--- | :--- |
| `metronomeView` | `MetronomeView` | Reference to the custom pendulum UI component | `onCreate`, `onBpmChanged`, `onProgressChanged` |
| `startButton` | `Button` | Button to start the metronome | `onCreate`, `startMetronome`, `stopMetronome` |
| `stopButton` | `Button` | Button to stop the metronome | `onCreate`, `startMetronome`, `stopMetronome` |
| `tempoSeekBar` | `SeekBar` | Slider to adjust the BPM (speed) | `onCreate`, `setTempo`, `onBpmChanged` |
| `bpmTextView` | `TextView` | Displays the current BPM number | `onCreate`, `setTempo`, `onBpmChanged` |
| `animator` | `ValueAnimator` | Handles the smooth back-and-forth movement of the pendulum | `startMetronome`, `stopMetronome` |
| `soundPool` | `SoundPool` | Efficiently plays the short "tick" sound | `onCreate`, `startMetronome`, `onDestroy` |
| `soundId` | `int` | ID of the loaded tick sound in memory | `onCreate`, `startMetronome` |
| `soundLoaded` | `boolean` | Flag to check if the sound is ready to play | `onCreate`, `startMetronome` |
| `currentBpm` | `int` | Stores the current speed (Beats Per Minute) | Throughout the class |
| `scheduler` | `ScheduledExecutorService` | A background thread manager for precise timing | `startMetronome`, `onDestroy` |
| `tickFuture` | `ScheduledFuture` | A reference to the active ticking task (used to stop it) | `startMetronome`, `stopMetronome` |

## 3. Class Methods

### Method: `onCreate`
*   **Type:** `protected`
*   **Return value:** `void` (nothing)
*   **Parameters:** `Bundle savedInstanceState` (used for restoring previous state)
*   **What it does:**
    1.  Sets the UI layout using `setContentView`.
    2.  Links the Java variables to the UI elements in XML (Buttons, SeekBar, etc.).
    3.  Initializes the `SoundPool` to prepare the audio.
    4.  Sets up "Click Listeners" so the app knows what to do when buttons are pressed.
*   **When called:** Automatically when the app starts.
*   **Important:** This is where the setup happens. If a component isn't initialized here, the app might crash later.

### Method: `startMetronome`
*   **Type:** `private`
*   **Return value:** `void`
*   **Parameters:** None
*   **What it does:**
    1.  Checks if the metronome is already running (to avoid starting it twice).
    2.  Creates a `ValueAnimator` to move the pendulum angle between -30 and +30 degrees.
    3.  Calculates the "period" (time between ticks) based on the BPM.
    4.  Uses the `scheduler` to play the "tick" sound repeatedly at that exact interval.
    5.  Disables the "Start" button and enables the "Stop" button.
*   **When called:** When the user clicks the "Start" button.
*   **Important:** Timing is handled in a background thread to ensure it stays steady even if the UI is busy.

### Method: `stopMetronome`
*   **Type:** `private`
*   **Return value:** `void`
*   **Parameters:** None
*   **What it does:**
    1.  Stops the pendulum animation.
    2.  Cancels the scheduled "tick" sounds.
    3.  Resets the pendulum to the center position (angle 0).
    4.  Enables the "Start" button again.
*   **When called:** When the user clicks "Stop" or when changing the BPM while running.

### Method: `calculateDuration`
*   **Type:** `private`
*   **Return value:** `long` (milliseconds between ticks)
*   **Parameters:** `int bpm` (speed)
*   **What it does:** Uses math (`60000 / bpm`) to find out how many milliseconds are in one beat.
*   **When called:** Internally by `startMetronome`.

## 4. Lifecycle
*   **`onCreate()`:** Called at the very beginning. Initializes everything.
*   **`onDestroy()`:** Called when the app is closed. It **must** release resources like the `SoundPool` and stop the `scheduler` to prevent the app from wasting battery in the background.

## 5. Interface Interaction (UI)
*   **Elements:** `Button` (Start/Stop), `SeekBar` (Tempo control), `TextView` (BPM display), and `MetronomeView` (The pendulum).
*   **Connection:** Uses `findViewById(R.id...)` to link XML design to Java code.
*   **Events:**
    *   `OnClickListener`: Handles button presses.
    *   `OnSeekBarChangeListener`: Updates speed when the user slides the bar.
    *   `OnBpmChangedListener`: A custom event triggered when the user drags the pendulum weight.

## 6. Interaction with other components
*   **MetronomeView:** `MainActivity` tells the view what the BPM is, and the view tells the activity if the user manually adjusted the weight.

## 7. General Logic
1.  User sets speed (BPM).
2.  `MainActivity` calculates how fast the ticks should be.
3.  When "Start" is pressed, a background "timer" (`scheduler`) starts playing sounds.
4.  Simultaneously, an "animator" starts swinging the visual pendulum on the screen.

## 8. Simplified Explanation
**Analogy:** Imagine `MainActivity` is the **Conductor** of an orchestra.
*   He looks at the **Score** (the BPM setting).
*   He tells the **Drummer** (`SoundPool`) exactly when to hit the drum.
*   He tells the **Dancer** (`MetronomeView`) how fast to move.
*   When you say "Stop!", he makes everyone go quiet and sit down.

---
**Note for Students:** Notice how `soundPool.release()` is called in `onDestroy`. This is "good housekeeping" in programming — always clean up after yourself!
