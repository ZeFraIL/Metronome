package zeev.fraiman.metronome;

import android.animation.ValueAnimator;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MetronomeView.OnBpmChangedListener, SeekBar.OnSeekBarChangeListener {

    private MetronomeView metronomeView;
    private Button startButton;
    private Button stopButton;
    private SeekBar tempoSeekBar;
    private TextView bpmTextView;

    private ValueAnimator animator;
    private SoundPool soundPool;
    private int soundId;
    private boolean soundLoaded = false;
    private int currentBpm = 120;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> tickFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        metronomeView = findViewById(R.id.metronomeView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        tempoSeekBar = findViewById(R.id.tempoSeekBar);
        bpmTextView = findViewById(R.id.bpmTextView);

        metronomeView.setOnBpmChangedListener(this);
        tempoSeekBar.setOnSeekBarChangeListener(this);

        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> soundLoaded = true);
        soundId = soundPool.load(this, R.raw.tick, 1);

        startButton.setOnClickListener(v -> startMetronome());
        stopButton.setOnClickListener(v -> stopMetronome());

        // Set initial state
        bpmTextView.setText(String.format("%d BPM", currentBpm));
        tempoSeekBar.setProgress(currentBpm);
        metronomeView.setBpm(currentBpm);
    }

    private void startMetronome() {
        if (tickFuture != null && !tickFuture.isCancelled()) {
            return; // Already running
        }

        // Visual Animator
        animator = ValueAnimator.ofFloat((float) -Math.PI / 6, (float) Math.PI / 6);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(calculateDuration(currentBpm));
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(animation -> metronomeView.setAngle((float) animation.getAnimatedValue()));
        animator.start();

        // Sound Scheduler
        Runnable tickRunnable = () -> {
            if (soundLoaded) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
            }
        };
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        long period = calculateDuration(currentBpm);
        tickFuture = scheduler.scheduleAtFixedRate(tickRunnable, period, period, TimeUnit.MILLISECONDS);

        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopMetronome() {
        if (animator != null) {
            animator.cancel();
        }
        if (tickFuture != null) {
            tickFuture.cancel(false);
        }
        metronomeView.setAngle(0);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private long calculateDuration(int bpm) {
        if (bpm == 0) return 1000;
        return (long) (60000.0 / bpm);
    }


    private void setTempo(int bpm) {
        this.currentBpm = bpm;
        boolean wasRunning = animator != null && animator.isRunning();
        if (wasRunning) {
            stopMetronome();
        }

        bpmTextView.setText(String.format("%d BPM", bpm));
        tempoSeekBar.setProgress(bpm);
        metronomeView.setBpm(bpm);

        if (wasRunning) {
            startMetronome();
        }
    }


    @Override
    public void onBpmChanged(int bpm) {
        // Called from MetronomeView touch event
        this.currentBpm = bpm;
        bpmTextView.setText(String.format("%d BPM", bpm));
        tempoSeekBar.setProgress(bpm);

        if (animator != null && animator.isRunning()) {
            stopMetronome();
            startMetronome();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.currentBpm = progress;
            bpmTextView.setText(String.format("%d BPM", progress));
            metronomeView.setBpm(progress);

            if (animator != null && animator.isRunning()) {
                stopMetronome();
                startMetronome();
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMetronome();
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        soundPool.release();
    }
}
