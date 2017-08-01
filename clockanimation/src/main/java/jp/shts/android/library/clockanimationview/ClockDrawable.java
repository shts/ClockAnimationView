package jp.shts.android.library.clockanimationview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by evelina on 15/07/2016.
 * Updated by shts(Shota Saito) on 27/07/2017
 */
public class ClockDrawable extends Drawable implements Animatable {

    private Paint facePaint;
    private Paint rimPaint;
    private ValueAnimator minuteAnimator;
    private ValueAnimator hourAnimator;

    private float rimRadius;
    private float faceRadius;

    private Path hourHandPath;
    private Path minuteHandPath;

    private float remainingHourRotation = 0f;
    private float remainingMinRotation = 0f;

    private float targetHourRotation = 0f;
    private float targetMinRotation = 0f;

    private float currentHourRotation = 0f;
    private float currentMinRotation;

    private boolean hourAnimInterrupted;
    private boolean minAnimInterrupted;

    private ClockTime previousTime;
    private final long duration;

    @Nullable
    private ClockAnimationListener clockAnimationListener;

    ClockDrawable(Paint facePaint, Paint rimPaint, long duration) {
        this.facePaint = facePaint;
        this.rimPaint = rimPaint;
        this.duration = duration;
        init();
    }

    private void init() {
        hourHandPath = new Path();
        minuteHandPath = new Path();

        previousTime = new ClockTime(0, 0);

        setUpMinuteAnimator();
        setUpHourAnimator();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        rimRadius = Math.min(bounds.width(), bounds.height()) / 2f - rimPaint.getStrokeWidth();
        faceRadius = rimRadius - rimPaint.getStrokeWidth();

        float hourHandLength = (float) (0.7 * faceRadius);
        float minuteHandLength = (float) (0.9 * faceRadius);
        float top = bounds.centerY();

        hourHandPath.reset();
        hourHandPath.moveTo(bounds.centerX(), bounds.centerY());
        hourHandPath.addRect(bounds.centerX(), top, bounds.centerX(), top - hourHandLength, Direction.CCW);
        hourHandPath.close();

        minuteHandPath.reset();
        minuteHandPath.moveTo(bounds.centerX(), bounds.centerY());
        minuteHandPath.addRect(bounds.centerX(), top, bounds.centerX(), top - minuteHandLength, Direction.CCW);
        minuteHandPath.close();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();

        // draw the outer rim of the clock
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), rimRadius, rimPaint);
        // draw the face of the clock
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), faceRadius, facePaint);

        int saveCount = canvas.save();
        canvas.rotate(currentHourRotation, bounds.centerX(), bounds.centerY());
        // draw hours hand
        canvas.drawPath(hourHandPath, rimPaint);
        canvas.restoreToCount(saveCount);

        saveCount = canvas.save();
        canvas.rotate(currentMinRotation, bounds.centerX(), bounds.centerY());
        // draw minutes hand
        canvas.drawPath(minuteHandPath, rimPaint);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {
        rimPaint.setAlpha(alpha);
        facePaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        rimPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void start() {
        hourAnimInterrupted = false;
        minAnimInterrupted = false;
        hourAnimator.start();
        minuteAnimator.start();
    }

    void setTime(ClockTime newTime) {
        long minutesDifference = getMinutesDifference(previousTime, newTime);
        // 60min ... 360grade
        // minDif .. minDelta
        float minDeltaRotation = ((float) minutesDifference * 360f) / 60f;
        // 720min ... 360grade = 12h ... 360grade
        // minDif ... hourDelta
        float hourDeltaRotation = ((float) minutesDifference * 360f) / 720f;

        remainingMinRotation += minDeltaRotation;
        remainingHourRotation += hourDeltaRotation;

        targetHourRotation = currentHourRotation + remainingHourRotation;
        targetMinRotation = currentMinRotation + remainingMinRotation;

        start();

        previousTime = newTime;

        invalidateSelf();
    }

    void animate(ClockTime newTime) {
        long minutesDifference = getMinutesDifference(previousTime, newTime);
        // 60min ... 360grade
        // minDif .. minDelta
        float minDeltaRotation = ((float) minutesDifference * 360f) / 60f;
        // 720min ... 360grade = 12h ... 360grade
        // minDif ... hourDelta
        float hourDeltaRotation = ((float) minutesDifference * 360f) / 720f;

        remainingMinRotation += minDeltaRotation;
        remainingHourRotation += hourDeltaRotation;

        if (isRunning()) {
            stop();
        }

        targetHourRotation = currentHourRotation + remainingHourRotation;
        hourAnimator.setFloatValues(currentHourRotation, targetHourRotation);

        targetMinRotation = currentMinRotation + remainingMinRotation;
        minuteAnimator.setFloatValues(currentMinRotation, targetMinRotation);

        start();

        previousTime = newTime;
    }

    @Override
    public void stop() {
        hourAnimInterrupted = true;
        minAnimInterrupted = true;
        hourAnimator.cancel();
        minuteAnimator.cancel();
    }

    @Override
    public boolean isRunning() {
        return hourAnimator.isRunning() || minuteAnimator.isRunning();
    }

    private long getMinutesDifference(ClockTime t1, ClockTime t2) {
        return Math.abs(t1.toMinutes() - t2.toMinutes());
    }

    void setClockAnimationListener(@Nullable ClockAnimationListener clockAnimationListener) {
        this.clockAnimationListener = clockAnimationListener;
    }

    public interface ClockAnimationListener {
        void onMinuteAnimationUpdate();

        void onMinuteAnimationEnd();

        void onHourAnimationUpdate();

        void onHourAnimationEnd();
    }

    void setFacePaint(Paint facePaint) {
        this.facePaint = facePaint;
        invalidateSelf();
    }

    void setRimPaint(Paint rimPaint) {
        this.rimPaint = rimPaint;
        invalidateSelf();
    }

    private void setUpMinuteAnimator() {
        minuteAnimator = ValueAnimator.ofFloat(0, 0);
        minuteAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        minuteAnimator.setDuration(duration);
        minuteAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (clockAnimationListener != null)
                    clockAnimationListener.onMinuteAnimationUpdate();
                float fraction = (float) valueAnimator.getAnimatedValue();
                remainingMinRotation = targetMinRotation - fraction;
                currentMinRotation = fraction;
                invalidateSelf();
            }
        });
        minuteAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!minAnimInterrupted) {
                    remainingMinRotation = 0f;
                }
                if (clockAnimationListener != null) clockAnimationListener.onMinuteAnimationEnd();
            }
        });
    }

    private void setUpHourAnimator() {
        hourAnimator = ValueAnimator.ofFloat(0, 0);
        hourAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hourAnimator.setDuration(duration);
        hourAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (clockAnimationListener != null) clockAnimationListener.onHourAnimationUpdate();
                float fraction = (float) valueAnimator.getAnimatedValue();
                remainingHourRotation = targetHourRotation - fraction;
                currentHourRotation = fraction;
                invalidateSelf();
            }
        });
        hourAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!hourAnimInterrupted) {
                    remainingHourRotation = 0f;
                }
                if (clockAnimationListener != null) clockAnimationListener.onHourAnimationEnd();
            }
        });
    }
}