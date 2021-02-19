package android.vonnie.barrageview;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import java.util.List;

/**
 * @author LongpingZou
 * @date 2019/3/29
 */
public class BubbleBarrage<T> {
    private final static String TAG = "BubbleBarrage";
    private LayoutTransition transition = new LayoutTransition();
    private Context context;
    private LinearLayout barrageContainer;
    private int layoutId;
    private int index = 0;
    /**
     * 执行间隔
     */
    private long intervalTime;
    /**
     * 显示的数量
     */
    private int visibleCount;
    /**
     * 每个弹幕要展示的时间=推送间隔*要显示的个数
     */
    private long visibleTime;

    private Handler handler = new Handler();
    /**
     * barrage load listener
     */
    private OnBarrageLoadListener<T> onBarrageLoadListener;
    private int margin;
    private List<T> barrages;
    private long delayStart;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "current in index -->" + index + "barrage size -->" + barrages.size());
            if (index > barrages.size() - 1) {
                index = 0;
                barrages.clear();
            } else {
                addBarrageToContainer(index);
                index++;
                if (handler != null) {
                    handler.postDelayed(this, intervalTime);
                }
            }
            Log.i(TAG, "current index -->" + index);
        }
    };

    public BubbleBarrage(Builder<T> builder) {
        this.intervalTime = builder.intervalTime;
        this.visibleCount = builder.visibleCount;
        this.barrages = builder.barrages;
        this.margin = builder.margin;
        this.delayStart = builder.delayStart;
        this.visibleTime = intervalTime * visibleCount;
        this.layoutId = builder.layoutId;
        this.barrageContainer = builder.container;
        this.onBarrageLoadListener = builder.onBarrageLoadListener;
        barrageContainer.setLayoutTransition(transition);
        setAppearTransition();
        setDisappearTransition();
    }


    /**
     * start to play barrage
     */
    public void start(Context context) {
        this.context = context;
        if (barrages == null) {
            try {
                throw new Exception("barrage data should not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        handler.postDelayed(runnable, delayStart);
    }


    /**
     * insert a barrage to list for next playing
     *
     * @param barrage
     */
    public void insertToNext(T barrage) {
        barrages.add(index, barrage);
        Log.i(TAG, "barrages size -->" + barrages.size());
        if (index == 0) {
            handler.postDelayed(runnable, delayStart);
        }
    }


    /**
     * append barrages to list
     *
     * @param barrages
     */
    public void appendBarrages(List<T> barrages) {
        this.barrages.addAll(barrages);
        if (index == 0) {
            handler.postDelayed(runnable, delayStart);
        }
    }

    /**
     * append a barrage to list
     *
     * @param barrage
     */
    public void appendBarrage(T barrage) {
        this.barrages.add(barrage);
        if (index == 0) {
            handler.postDelayed(runnable, delayStart);
        }
    }


    /**
     * set transition of view appearing
     */
    @SuppressLint("ObjectAnimatorBinding")
    private void setAppearTransition() {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        ObjectAnimator multiAnim = ObjectAnimator.ofPropertyValuesHolder(this, alpha, scaleX, scaleY).setDuration(transition.getDuration(LayoutTransition.APPEARING));
        multiAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                View view = (View) ((ObjectAnimator) animation).getTarget();
                if (view != null) {
                    view.setPivotX(0f);
                    view.setPivotY(view.getHeight());
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        transition.setAnimator(LayoutTransition.APPEARING, multiAnim);

    }


    /**
     * set transition of view disappearing
     */
    @SuppressLint("ObjectAnimatorBinding")
    private void setDisappearTransition() {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f);
        ObjectAnimator multiAnim = ObjectAnimator.ofPropertyValuesHolder(this, alpha, scaleX, scaleY).setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));
        multiAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                View view = (View) ((ObjectAnimator) animation).getTarget();
                if (view != null) {
                    view.setPivotX(0f);
                    view.setPivotY(0f);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        transition.setAnimator(LayoutTransition.DISAPPEARING, multiAnim);
    }

    /**
     * add barrage into container
     */
    private void addBarrageToContainer(int index) {
        //create barrage view
        View barrage = createBarrage(index);
        //setting each barrage margins
        LinearLayout.LayoutParams layoutParams = setItemMargin();
        barrageContainer.addView(barrage, layoutParams);
        //at the end of visible time, remove view
        barrage.postDelayed(removeView(barrage), visibleTime);
    }

    /**
     * create bubble barrage
     *
     * @return
     */
    private View createBarrage(int index) {
        View barrage = LayoutInflater.from(context).inflate(layoutId, null);
        //setting barrages load listener
        if (onBarrageLoadListener != null) {
            onBarrageLoadListener.loadBarrage(barrage, barrages, index);
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(visibleTime + 1000);
        alphaAnimation.setFillAfter(true);
        barrage.startAnimation(alphaAnimation);
        return barrage;
    }


    /**
     * setting barrages margin
     *
     * @return
     */
    private LinearLayout.LayoutParams setItemMargin() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(margin, 0, margin, margin);
        return layoutParams;
    }

    /**
     * at the end of visible time,remove barrage view
     *
     * @param barrage
     * @return
     */
    private Runnable removeView(final View barrage) {
        return new Runnable() {
            @Override
            public void run() {
                barrageContainer.removeView(barrage);
            }
        };
    }


    /**
     * destroy view
     */
    public void destroyView() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

    /**
     * when create barrage, call this listener
     */
    interface OnBarrageLoadListener<T> {
        /**
         * load barrage
         *
         * @param view  item view
         * @param data  data array
         * @param index barrage index
         */
        void loadBarrage(View view, List<T> data, int index);
    }


    public static class Builder<T> {
        private long intervalTime = 2000;
        private int visibleCount = 3;
        private int margin = 5;
        private long delayStart = 0;
        private List<T> barrages;
        private int layoutId;
        private LinearLayout container;
        private OnBarrageLoadListener<T> onBarrageLoadListener;

        public Builder<T> setIntervalTime(long intervalTime) {
            this.intervalTime = intervalTime;
            return this;
        }

        public Builder<T> setVisibleCount(int visibleCount) {
            this.visibleCount = visibleCount;
            return this;
        }

        public Builder<T> setItemMargin(int margin) {
            this.margin = margin;
            return this;
        }

        public Builder<T> setBarrages(List<T> barrages) {
            this.barrages = barrages;
            return this;
        }

        public Builder<T> setDelayStart(long delayStart) {
            this.delayStart = delayStart;
            return this;
        }

        public Builder<T> setLayoutId(int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        public Builder<T> setContainer(LinearLayout container) {
            this.container = container;
            return this;
        }

        public Builder<T> setOnBarrageLoadListener(OnBarrageLoadListener<T> onBarrageLoadListener) {
            this.onBarrageLoadListener = onBarrageLoadListener;
            return this;
        }

        public BubbleBarrage<T> build() {
            return new BubbleBarrage<T>(this);
        }
    }
}
