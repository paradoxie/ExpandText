package cf.paradoxie.expandtext;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView, mTextViewToggle;
    private boolean isOpen = false;
    private int mShortHeight;//限定行数高度
    private int mLongHeight;//展开全文高度
    private LinearLayout.LayoutParams mLayoutParams;
    private int mMaxlines = 2;//设定显示的最大行数
    private int maxLine;//真正的最大行数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.tv);
        mTextViewToggle = (TextView) findViewById(R.id.tv_toggle);
        //拿到高度
        mShortHeight = getShortHeight();

        mTextView.post(new Runnable() {
            @Override
            public void run() {
                maxLine = mTextView.getLineCount();//获取完全显示饿行数

                mLongHeight = getLongHeight();//获取完全显示需要的高度

                Log.d("MainActivity", "长的" + mLongHeight + ",短的=" + mShortHeight);
                if (mLongHeight <= mShortHeight) {
                    mTextViewToggle.setVisibility(View.GONE);//完全显示需要的高度小于低的高度的时候，不需要展开
                }
            }
        });

        //显示少的高度
        mLayoutParams = (LinearLayout.LayoutParams) mTextView.getLayoutParams();
        mLayoutParams.height = mShortHeight;
        mTextView.setLayoutParams(mLayoutParams);

        mTextViewToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();//开始展开收起动画
            }
        });

    }

    private void toggle() {
        ValueAnimator animator;
        if (isOpen) {
            animator = ValueAnimator.ofInt(mLongHeight, mShortHeight);
            isOpen = false;
        } else {
            animator = ValueAnimator.ofInt(mShortHeight, mLongHeight);
            isOpen = true;
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                mLayoutParams.height = value;
                mTextView.setLayoutParams(mLayoutParams);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isOpen) {
                    mTextViewToggle.setText("收起全文");
                } else {
                    mTextViewToggle.setText("展开全文");
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.setDuration(500);
        animator.start();
    }


    private int getShortHeight() {//虚拟一个tv来获得理论高度值，短文高度
        int width = mTextView.getMeasuredWidth();//宽度

        TextView textView = new TextView(this);
        textView.setText(R.string.str);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setMaxLines(mMaxlines);

        int measureSpecWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);//宽度match
        int measureSpecHeight = View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.AT_MOST);//高度wrap，1920为最大高度
        textView.measure(measureSpecWidth, measureSpecHeight);

        return textView.getMeasuredHeight();
    }

    private int getLongHeight() {//长文高度

        int height = mShortHeight / mMaxlines * maxLine;

        return height;
    }

}
