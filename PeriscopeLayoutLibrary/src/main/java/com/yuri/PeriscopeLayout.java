package com.yuri;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class PeriscopeLayout extends FrameLayout {

    public static final float DEFAULT_NOISE_FACTOR = 0.3f;
    public static final float BIG_EYE_LEVEL_5 = 0.6f;
    public static final float SMOOTH_SKIN_LEVEL_5 = 0.8f;

    public static final float MAX_DROP_RATIO = 0.5f;

    private LayoutParams lp;
    private Drawable[] drawables;
    private Random random = new Random();

    private int mHeight; // f22160b
    private int mWidth; // f22161c

    private int dHeight; //f22166h
    private int dWidth; //f22167i
    private int nDrawableIndex = 0; //f22172n
    private boolean isDirection;//f22171m;
    public int nDuration = 3000;//f22168j

    int fDelay;//f22159a
    private Handler handler = new Handler(Looper.getMainLooper()); //f22169k
    private Runnable runnable = new ControlRunnable(this); //f22170l

    private Queue<ImageView> containers;//f22165g;

    class ControlRunnable implements Runnable {//C86641
        final /* synthetic */ PeriscopeLayout layout; //f22152a

        ControlRunnable(PeriscopeLayout periscopeLayout) {
            this.layout = periscopeLayout;
        }

        public void run() {
            this.layout.showSymbol();
            if (this.layout.handler != null) {
                this.layout.handler.postDelayed(this, (long) this.layout.fDelay);
            }
        }
    }

    public PeriscopeLayout(Context context) {
        super(context);
        init();
    }

    public PeriscopeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PeriscopeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PeriscopeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.containers = new LinkedList();
        //初始化显示的图片
        drawables = new Drawable[2];
        Drawable symbol1 = getResources().getDrawable(R.drawable.symbol1);
        Drawable symbol2 = getResources().getDrawable(R.drawable.symbol2);

        drawables[0] = symbol1;
        drawables[1] = symbol2;
        //获取图的宽高 用于后面的计算
        //注意 我这里3张图片的大小都是一样的,所以我只取了一个
        dHeight = symbol1.getIntrinsicHeight();
        dWidth = symbol1.getIntrinsicWidth();

        //底部 并且 水平居中
        lp = new LayoutParams(dWidth, dHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        isDirection = true;
        if (getLayoutDirection() != LAYOUT_DIRECTION_RTL) {
            isDirection = false;
        }
    }

    private ImageView getImageView() {//m14349b() {
        if (!this.containers.isEmpty()) {
            return (ImageView) this.containers.poll();
        }
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(this.lp);
        addView(imageView);
        return imageView;
    }

    private void stopAnimation(View view) { //m14346a
        if (view.getTag() != null) {
            this.containers.add((ImageView) view);
            ValueAnimator valueAnimator = (ValueAnimator) view.getTag();
            if (valueAnimator != null) {
                valueAnimator.setTarget(null);
                valueAnimator.cancel();
                valueAnimator.removeAllUpdateListeners();
            }
            if (view.getTag(R.id.animatior) instanceof BezierListener) {
                BezierListener evaluator = (BezierListener) view.getTag(R.id.animatior);
                if (evaluator != null) {
                    evaluator.setTarget(null);
                }
            }
            view.setAlpha(0.0f);
            view.setScaleX(DEFAULT_NOISE_FACTOR);
            view.setScaleY(DEFAULT_NOISE_FACTOR);
            view.setRotation(0.0f);
            view.setTag(null);
        }
    }

    private void removeAllChildView() {//m14351c
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            stopAnimation(getChildAt(i));
        }
    }

    public void showSymbol() {
        ImageView imageView = getImageView();//new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[nDrawableIndex]);
        nDrawableIndex = (nDrawableIndex + 1) % 2;

        Animator animator = getAnimator(imageView);
        imageView.setTag(animator);
        animator.start();
    }

    public void showSymbol(int delay, int duration) {
        this.nDuration = duration;

        //m14345a(i);
        this.fDelay = delay;
        this.handler.removeCallbacksAndMessages(null);
        this.handler.postDelayed(this.runnable, (long) (this.random.nextInt(4) * 100));
    }

    public void pause() {
        stopHandler();
        removeAllChildView();
    }

    public void stop() {
        removeAllChildView();
        stopHandler();
    }


    private void stopHandler() { // m14352d
        this.handler.removeCallbacksAndMessages(null);
        this.handler.removeCallbacks(this.runnable);
    }

    private Animator getAnimator(View target) { //m14348b
        float dip2Px = PeriscopeLayout.dip2Px(48.0f);
        float dip2Px2 = PeriscopeLayout.dip2Px(20.0f);
        float dip2Px3 = PeriscopeLayout.dip2Px(20.0f);
        if (this.isDirection) {
            dip2Px = (((float) this.mWidth) - dip2Px) - dip2Px3;
            dip2Px2 = (((float) this.mWidth) - dip2Px2) - dip2Px3;
        }
        MusicEvaluator evaluator = new MusicEvaluator(new PointF(dip2Px, ((float) (this.mHeight - this.dHeight)) - PeriscopeLayout.dip2Px(8.0f)), new PointF(dip2Px2, PeriscopeLayout.dip2Px(51.0f)));
        PointF[] points = new PointF[2];
        points[0] = new PointF(this.isDirection ? ((float) this.dWidth) - dip2Px3 : (float) (this.mWidth - this.dWidth), ((float) (this.mHeight - this.dHeight)) - PeriscopeLayout.dip2Px(2.0f));
        points[1] = new PointF(this.isDirection ? (((float) this.mWidth) - dip2Px3) - PeriscopeLayout.dip2Px((float) (this.random.nextInt(30) + 12)) : PeriscopeLayout.dip2Px((float) (this.random.nextInt(30) + 12)), 0.0f);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, points);
        BezierListener listener = new BezierListener(this, target);
        animator.addUpdateListener(listener);
        animator.setTarget(target);
        target.setTag(R.id.animatior, listener);
        animator.setDuration((long) this.nDuration);

        return animator;
    }

    private static float dip2Px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }

    public static int px2dip(int pxValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private class BezierListener implements ValueAnimator.AnimatorUpdateListener {

        final /* synthetic */ PeriscopeLayout periscopeLayout;//f22153a;
        private View target;
        private int factor;
        private int addional;

        public BezierListener(PeriscopeLayout periscopeLayout, View target) {
            this.periscopeLayout = periscopeLayout;
            this.target = target;
            this.factor = -1;
            this.addional = 1;
            if (periscopeLayout.random.nextBoolean()) {
                this.factor = 1;
            }
            if (!periscopeLayout.random.nextBoolean()) {
                this.addional = -1;
            }
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (this.target != null && this.target.getTag() != null) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                this.target.setX(pointF.x);
                this.target.setY(pointF.y);
                float animatedFraction = animation.getAnimatedFraction();
                if (animatedFraction <= 0.7f) {
                    this.target.setAlpha((animatedFraction / 0.7f) * 0.7f);
                    this.target.setScaleX(((animatedFraction / 0.7f) * DEFAULT_NOISE_FACTOR) + DEFAULT_NOISE_FACTOR);
                    this.target.setScaleY(((animatedFraction / 0.7f) * DEFAULT_NOISE_FACTOR) + DEFAULT_NOISE_FACTOR);
                } else if (((double) animatedFraction) <= 0.8d) {
                    this.target.setAlpha(0.7f);
                    this.target.setScaleX(BIG_EYE_LEVEL_5);
                    this.target.setScaleY(BIG_EYE_LEVEL_5);
                } else if (animatedFraction <= 1.0f) {
                    float f = (animatedFraction - SMOOTH_SKIN_LEVEL_5) / 0.2f;
                    this.target.setAlpha((1.0f - f) * 0.7f);
                    this.target.setScaleX((0.1f * f) + BIG_EYE_LEVEL_5);
                    this.target.setScaleY((f * 0.1f) + BIG_EYE_LEVEL_5);
                    if (((double) (1.0f - animatedFraction)) < 1.0E-10d) {
                        this.periscopeLayout.stopAnimation(this.target);
                        return;
                    }
                }
                if (animatedFraction <= MAX_DROP_RATIO) {
                    this.target.setRotation(((animatedFraction / MAX_DROP_RATIO) * 20.0f) * ((float) this.factor));
                } else {
                    this.target.setRotation(((((animatedFraction - MAX_DROP_RATIO) / MAX_DROP_RATIO) * 20.0f) * ((float) this.addional)) + ((float) (this.factor * 20)));
                }
            }
        }

        public void setTarget(View target) {
            this.target = target;
        }
    }
}
