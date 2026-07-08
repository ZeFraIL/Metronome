# Class Description: MetronomeView

## 1. General Information
*   **Class Name:** `MetronomeView`
*   **Type:** Custom View (Extends `View`)
*   **Purpose:** This class is responsible for drawing the visual part of the metronome. It renders the dark background, the tempo scale with musical terms, and the moving pendulum with its weight.
*   **Interaction:** It sends notifications to `MainActivity` via an "Interface" (`OnBpmChangedListener`) whenever the user interacts with the pendulum by touching the screen.

## 2. Variables (Class Fields)

| Name | Type | Purpose | Where is it used |
| :--- | :--- | :--- | :--- |
| `pendulumPaint` | `Paint` | "Brush" settings (color, thickness) for the pendulum rod | `init`, `onDraw` |
| `weightPaint` | `Paint` | Brush for the sliding weight | `init`, `onDraw` |
| `scalePaint` | `Paint` | Brush for the scale lines | `init`, `drawScale` |
| `angle` | `float` | Current rotation angle of the pendulum | `onDraw`, `setAngle` |
| `pendulumLength`| `float` | The calculated length of the pendulum based on screen size | `onSizeChanged`, `onDraw` |
| `weightY` | `float` | The vertical position (height) of the weight on the rod | `onDraw`, `setBpm`, `bpmToY` |
| `currentBpm` | `int` | The speed value currently represented by the view | `setBpm`, `yToBpm`, `onTouchEvent` |
| `tempoMarkings` | `Map` | A dictionary mapping BPM numbers to musical names (e.g., 120 = "Animato") | `static` block, `drawScale` |

## 3. Class Methods

### Method: `init`
*   **Type:** `private`
*   **Return value:** `void`
*   **Parameters:** None
*   **What it does:** Sets up all the "paints" (colors and text sizes). It's like preparing the palette before starting to paint a picture.
*   **When called:** When the View is first created (in the constructors).

### Method: `onDraw`
*   **Type:** `protected`
*   **Return value:** `void`
*   **Parameters:** `Canvas canvas` (The "paper" to draw on)
*   **What it does:**
    1.  Colors the background dark gray.
    2.  Calls `drawScale` to draw the numbers and lines.
    3.  "Rotates" the canvas based on the current `angle`.
    4.  Draws the pendulum line and the trapezoid-shaped weight.
*   **When called:** Every time `invalidate()` is called (many times per second during animation).
*   **Important:** This method must be very fast. Never create new objects (like `new Paint()`) inside `onDraw`!

### Method: `onTouchEvent`
*   **Type:** `public`
*   **Return value:** `boolean` (True if the touch was handled)
*   **Parameters:** `MotionEvent event` (Details about where the user touched)
*   **What it does:**
    1.  Detects if the user is pressing or sliding their finger.
    2.  Calculates the new BPM based on the `Y` coordinate (height) of the touch.
    3.  Updates the view and tells `MainActivity` about the change.
*   **When called:** When you touch the `MetronomeView` on your screen.

### Method: `yToBpm` & `bpmToY`
*   **Type:** `private`
*   **Return value:** `int` / `float`
*   **What it does:** These are "translator" methods. `yToBpm` converts a pixel position on the screen to a BPM number, and `bpmToY` does the opposite.
*   **Important:** They use linear interpolation (a math technique) to map values between the top and bottom of the rod.

## 4. Lifecycle (Custom View)
Custom Views don't have the same lifecycle as Activities, but they have:
*   **`onSizeChanged()`:** Called when the app first opens or the screen rotates. We use this to measure how tall the pendulum should be.
*   **`onDraw()`:** The heart of the view where the drawing happens.

## 5. Interface Interaction (UI)
*   **Drawing:** Uses the `Canvas` API to draw lines (`drawLine`), text (`drawText`), and paths (`drawPath`).
*   **Input:** Direct touch handling via `onTouchEvent`.

## 6. Interaction with other components
*   **Listener Pattern:** It uses the `OnBpmChangedListener` interface. This allows `MetronomeView` to "shout" a message like "Hey, the BPM changed to 140!" without needing to know exactly how `MainActivity` works. This is called **Decoupling**.

## 7. General Logic
The view is a visual representation of a physical object. It calculates where the weight should be based on the BPM. When an animation runs, `MainActivity` rapidly changes the `angle` variable and tells the view to "redraw itself" (`invalidate`).

## 8. Simplified Explanation
**Analogy:** Imagine `MetronomeView` is a **Mechanical Puppet**.
*   It knows how to look (background, lines, colors).
*   It has "strings" (`angle` and `weightY`) that control its movement.
*   When you touch it and move its arm (the weight), it has a "voice" (`listener`) to tell the puppeteer (`MainActivity`) that something changed.

---
**Note for Students:** Look at the `static` block where `tempoMarkings` are filled. Using a `Map` is a very efficient way to store "pairs" of data (BPM and Name) instead of using a long list of `if-else` statements.
