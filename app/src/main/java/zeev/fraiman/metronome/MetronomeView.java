package zeev.fraiman.metronome;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MetronomeView extends View {

    public interface OnBpmChangedListener {
        void onBpmChanged(int bpm);
    }

    private Paint pendulumPaint, weightPaint, scalePaint, scaleBpmPaint, scaleMarkingPaint;
    private float angle = 0;

    private float pendulumLength;
    private float weightHeight = 50f;
    private float weightWidth = 80f; // Made it a bit smaller
    private float weightY;

    private int minBpm = 40;
    private int maxBpm = 208;
    private int currentBpm = 120;

    private OnBpmChangedListener listener;

    // Data for the scale markings
    private static final Map<Integer, String> tempoMarkings = new LinkedHashMap<>();
    private static final int[] bpmValues = {40, 44, 48, 52, 56, 60, 66, 72, 80, 88, 96, 104, 112, 120, 132, 144, 152, 168, 184, 200, 208};

    static {
        tempoMarkings.put(40, "Grave");
        tempoMarkings.put(48, "Largo");
        tempoMarkings.put(52, "Lento");
        tempoMarkings.put(56, "Adagio");
        tempoMarkings.put(60, "Larghetto");
        tempoMarkings.put(66, "Adagetto");
        tempoMarkings.put(72, "Andante");
        tempoMarkings.put(80, "Andantino");
        tempoMarkings.put(88, "Maestoso");
        tempoMarkings.put(96, "Moderato");
        tempoMarkings.put(104, "Allegretto");
        tempoMarkings.put(120, "Animato");
        tempoMarkings.put(132, "Allegro");
        tempoMarkings.put(144, "Assai");
        tempoMarkings.put(152, "Vivace");
        tempoMarkings.put(168, "Presto");
        tempoMarkings.put(200, "Prestissimo");
    }

    public MetronomeView(Context context) {
        super(context);
        init();
    }

    public MetronomeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MetronomeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        pendulumPaint = new Paint();
        pendulumPaint.setColor(Color.LTGRAY);
        pendulumPaint.setStrokeWidth(15f);

        weightPaint = new Paint();
        weightPaint.setColor(Color.WHITE);

        scalePaint = new Paint();
        scalePaint.setColor(Color.GRAY);
        scalePaint.setStrokeWidth(2f);

        scaleBpmPaint = new Paint();
        scaleBpmPaint.setColor(Color.WHITE);
        scaleBpmPaint.setTextSize(28f);
        scaleBpmPaint.setTextAlign(Paint.Align.LEFT);

        scaleMarkingPaint = new Paint();
        scaleMarkingPaint.setColor(Color.WHITE);
        scaleMarkingPaint.setTextSize(28f);
        scaleMarkingPaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void setOnBpmChangedListener(OnBpmChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        pendulumLength = h * 0.9f;
        setBpm(this.currentBpm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#333333")); // Dark background

        int centerX = getWidth() / 2;
        int pivotY = getHeight();

        drawScale(canvas, centerX);

        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), centerX, pivotY);

        canvas.drawLine(centerX, pivotY, centerX, pivotY - pendulumLength, pendulumPaint);

        // Trapezoid shape for the weight
        android.graphics.Path weightPath = new android.graphics.Path();
        float topY = weightY - weightHeight / 2;
        float bottomY = weightY + weightHeight / 2;
        weightPath.moveTo(centerX - weightWidth / 2, topY);
        weightPath.lineTo(centerX + weightWidth / 2, topY);
        weightPath.lineTo(centerX + (weightWidth/2 + 20), bottomY);
        weightPath.lineTo(centerX - (weightWidth/2 + 20), bottomY);
        weightPath.close();
        canvas.drawPath(weightPath, weightPaint);

        canvas.restore();
    }

    private void drawScale(Canvas canvas, int centerX) {
        float markingTextX = centerX - 40;
        float bpmTextX = centerX + 40;

        for (int bpm : bpmValues) {
            float y = bpmToY(bpm);
            canvas.drawLine(centerX - 20, y, centerX + 20, y, scalePaint);
            canvas.drawText(String.valueOf(bpm), bpmTextX, y + 10, scaleBpmPaint);

            if (tempoMarkings.containsKey(bpm)) {
                canvas.drawText(tempoMarkings.get(bpm), markingTextX, y + 10, scaleMarkingPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            int newBpm = yToBpm(event.getY());
            if (newBpm != this.currentBpm) {
                setBpm(newBpm);
                if (listener != null) {
                    listener.onBpmChanged(newBpm);
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    public void setBpm(int bpm) {
        bpm = Math.max(minBpm, Math.min(bpm, maxBpm));
        this.currentBpm = bpm;
        this.weightY = bpmToY(bpm);
        invalidate();
    }

    private int yToBpm(float y) {
        float rodTopY = getHeight() - pendulumLength;
        float rodBottomY = getHeight();
        y = Math.max(rodTopY, Math.min(y, rodBottomY));
        float progress = (y - rodTopY) / pendulumLength;
        int bpm = (int) (minBpm + progress * (maxBpm - minBpm));
        return Math.max(minBpm, Math.min(bpm, maxBpm));
    }

    private float bpmToY(int bpm) {
        float rodTopY = getHeight() - pendulumLength;
        float progress = (float) (bpm - minBpm) / (maxBpm - minBpm);
        return rodTopY + progress * pendulumLength;
    }
}
