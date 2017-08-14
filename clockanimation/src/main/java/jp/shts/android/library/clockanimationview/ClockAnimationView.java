package jp.shts.android.library.clockanimationview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;

/**
 * Clock animation views
 */
public class ClockAnimationView extends android.support.v7.widget.AppCompatImageView {

    public ClockAnimationView(Context context) {
        this(context, null);
    }

    public ClockAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @ColorRes
    private static final int FACE_COLOR = android.R.color.white;
    @ColorRes
    private static final int RIM_COLOR = android.R.color.black;
    private static final int ANIMATION_DURATION = 500;
    private static final float RIM_STROKE_WIDTH = 5f;

    private Paint facePaint;
    @ColorInt
    private int facePaintColor;
    private Paint rimPaint;
    @ColorInt
    private int rimPaintColor;
    private float rimStrokeWidth;
    private long duration;

    private ClockDrawable clockDrawable;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClockAnimationView);
            duration = ta.getInt(R.styleable.ClockAnimationView_animDurations, ANIMATION_DURATION);
            this.facePaintColor = ta.getColor(R.styleable.ClockAnimationView_faceColor, ContextCompat.getColor(context, FACE_COLOR));
            this.rimPaintColor = ta.getColor(R.styleable.ClockAnimationView_rimColor, ContextCompat.getColor(context, RIM_COLOR));
            this.rimStrokeWidth = ta.getDimension(R.styleable.ClockAnimationView_rimStrokeWidth, dp2px(RIM_STROKE_WIDTH));
            ta.recycle();
        } else {
            duration = ANIMATION_DURATION;
            this.facePaintColor = ContextCompat.getColor(context, FACE_COLOR);
            this.rimPaintColor = ContextCompat.getColor(context, RIM_COLOR);
            this.rimStrokeWidth = dp2px(RIM_STROKE_WIDTH);
        }

        initFacePaint();
        initRimColor();

        clockDrawable = new ClockDrawable(facePaint, rimPaint, duration);
        setImageDrawable(clockDrawable);
    }

    /**
     * Set time
     *
     * @param hours   hours
     * @param minutes minutes
     */
    public void setTime(int hours, int minutes) {
        checkParams(hours, minutes);
        clockDrawable.setTime(new ClockTime(hours, minutes));
    }

    /**
     * Start animation
     *
     * @param hours   hours
     * @param minutes minutes
     */
    public void animateToTime(int hours, int minutes) {
        checkParams(hours, minutes);
        clockDrawable.animate(new ClockTime(hours, minutes));
    }

    private void checkParams(int hours, int minutes) {
        if (23 < hours) throw new IllegalArgumentException("hours must be in 0-23.");
        if (59 < minutes) throw new IllegalArgumentException("minutes must be in 0-59.");
    }

    /**
     * Set animation listener.
     *
     * @param clockAnimationListener ClockAnimationListener
     */
    public void setClockAnimationListener(ClockDrawable.ClockAnimationListener clockAnimationListener) {
        clockDrawable.setClockAnimationListener(clockAnimationListener);
    }

    private void initFacePaint() {
        facePaint = new Paint(ANTI_ALIAS_FLAG);
        facePaint.setColor(facePaintColor);
        facePaint.setStyle(FILL);
    }

    private void initRimColor() {
        rimPaint = new Paint(ANTI_ALIAS_FLAG);
        rimPaint.setColor(rimPaintColor);
        rimPaint.setStyle(STROKE);
        rimPaint.setStrokeCap(ROUND);
        rimPaint.setStrokeWidth(rimStrokeWidth);
    }

    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setFacePaintColor(@ColorInt int facePaintColor) {
        this.facePaintColor = facePaintColor;
        initFacePaint();
        clockDrawable.setFacePaint(facePaint);
    }

    public void setRimPaintColor(@ColorInt int rimPaintColor) {
        this.rimPaintColor = rimPaintColor;
        initRimColor();
        clockDrawable.setRimPaint(rimPaint);
    }

    public void setRimStrokeWidth(int rimStrokeWidth) {
        this.rimStrokeWidth = rimStrokeWidth;
    }
}
