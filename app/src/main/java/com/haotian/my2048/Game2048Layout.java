package com.haotian.my2048;

import android.content.Context;
import android.net.sip.SipAudioCall;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by HAOTIAN on 2016/2/26.
 */
public class Game2048Layout  extends RelativeLayout {
    /**
     * 设置Item的数量n*n；默认为4
     */
    private int mColumn = 4;
    /**
     * 存放所有的Item
     */
    private Game2048Item[] mGame2048Items;

    /**
     * Item横向与纵向的边距
     */
    private int mMargin = 10;
    /**
     * 面板的padding
     */
    private int mPadding;
    /**
     * 检测用户滑动的手势
     */
    private GestureDetector mGestureDetector;

    // 用于确认是否需要生成一个新的值
    private boolean isMergeHappen = true;
    private boolean isMoveHappen = true;

    /**
     * 记录分数
     */
    private int mScore;

    public interface OnGame2048Listener{
        public void onScoreChange(int score);
        public void onGameOver();

    }

    public OnGame2048Listener mGame2048Listener;

    public void setOnGame2048Listener(OnGame2048Listener onGame2048Listener){
        mGame2048Listener = onGame2048Listener;
    }
    public Game2048Layout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMargin, getResources().getDisplayMetrics());
        // 设置Layout的内边距，四边一致，设置为四内边距中的最小值
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom());

        mGestureDetector = new GestureDetector(context , new MyGestureDetector());
        Log.i("tag", "Game2048Layout: 666666666666666");
    }


    public Game2048Layout(Context context)
    {
        this(context, null);
    }

    public Game2048Layout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    private int min(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
         int t;
         if (paddingLeft<paddingTop)  {t = paddingLeft;} else {t = paddingTop;}
         if (t > paddingRight) {t = paddingRight;}
         if (t >paddingBottom) {t = paddingBottom;}
        return  t;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private boolean once;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("tag", "onMeasure:  i am again");
        // 获得正方形的边长
        int length = Math.min(getMeasuredHeight(), getMeasuredWidth());
        // 获得Item的宽度
        int childWidth = (length - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;

        if (!once)
        {
            if (mGame2048Items == null)
            {
                mGame2048Items = new Game2048Item[mColumn * mColumn];
            }
            // 放置Item
            for (int i = 0; i < mGame2048Items.length; i++)
            {

                Game2048Item item = new Game2048Item(getContext());

                mGame2048Items[i] = item;
                item.setId(i + 1);
                RelativeLayout.LayoutParams lp = new LayoutParams(childWidth,childWidth);
                // 设置横向边距,不是最后一列
                if ((i + 1) % mColumn != 0)
                {
                    lp.rightMargin = mMargin;
                }
                // 如果不是第一列
                if (i % mColumn != 0)
                {
                    lp.addRule(RelativeLayout.RIGHT_OF,//
                            mGame2048Items[i - 1].getId());
                }
                // 如果不是第一行，//设置纵向边距，非最后一行
                if ((i + 1) > mColumn)
                {
                    lp.topMargin = mMargin;
                    lp.addRule(RelativeLayout.BELOW,//
                            mGame2048Items[i - mColumn].getId());
                }
                addView(item, lp);
            }
            generateNum();
        }
        once = true;

        setMeasuredDimension(length, length);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("tag", "onTouchEvent: 77777777777777");
        return (mGestureDetector.onTouchEvent(event));
    }

    /**
     * 运动方向的枚举
     *
     * @author zht
     *
     */
    private enum ACTION
    {
        LEFT, RIGHT, UP, DOWM
    }
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener
    {

        final int FLING_MIN_DISTANCE = 50;

        @Override
        public boolean onDown(MotionEvent e) {
            Log.i("hehe","1111111111111111");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY)
        {
            Log.i("tag", "onFling: 22222222222222222164556465 ");
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();

            if (x > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > Math.abs(velocityY))
            {
                action(ACTION.RIGHT);

            } else if (x < -FLING_MIN_DISTANCE
                    && Math.abs(velocityX) > Math.abs(velocityY))
            {
                action(ACTION.LEFT);

            } else if (y > FLING_MIN_DISTANCE
                    && Math.abs(velocityX) < Math.abs(velocityY))
            {
                action(ACTION.DOWM);

            } else if (y < -FLING_MIN_DISTANCE
                    && Math.abs(velocityX) < Math.abs(velocityY))
            {
                action(ACTION.UP);
            }
            return true;

        }

    }

    /**
     * 根据用户运动，整体进行移动合并值等
     */
    private void action(ACTION action)
    {
        Log.i("tag", "action: "+action);
        // 行|列
        for (int i = 0; i < mColumn; i++)
        {
            List<Game2048Item> row = new ArrayList<Game2048Item>();
            // 行|列
            for (int j = 0; j < mColumn; j++)
            {
                // 得到下标
                int index = getIndexByAction(action, i, j);

                Game2048Item item = mGame2048Items[index];
                // 记录不为0的数字
                if (item.getNumber() != 0)
                {
                    row.add(item);
                }
            }

            for (int j = 0; j < mColumn && j < row.size(); j++)
            {
                int index = getIndexByAction(action, i, j);
                Game2048Item item = mGame2048Items[index];

                if (item.getNumber() != row.get(j).getNumber())
                {
                    Log.i("tag", "action: reesersresfesfsefsefsesesef");
                    isMoveHappen = true;
                }
            }

            // 合并相同的
            mergeItem(row);


            // 设置合并后的值
            for (int j = 0; j < mColumn; j++)
            {
                int index = getIndexByAction(action, i, j);
                if (row.size() > j)
                {
                    mGame2048Items[index].setNumber(row.get(j).getNumber());
                } else
                {
                    mGame2048Items[index].setNumber(0);
                }
            }

        }
        generateNum();

    }

    private int getIndexByAction(ACTION action, int i, int j) {
         if (action == ACTION.DOWM ){ return(i+(mColumn-j-1)*mColumn); }
         if (action == ACTION.UP ){ return(i+j*mColumn); }
         if (action == ACTION.LEFT ){ return(j+i*mColumn); }
         if (action == ACTION.RIGHT ){ return(mColumn-1-j+i*mColumn); }
        return -1;
    }


    /**
     * 合并相同的Item
     *
     * @param row
     */
    private void mergeItem(List<Game2048Item> row)
    {
        if (row.size() < 2)
            return;

        for (int j = 0; j < row.size() - 1; j++)
        {
            Game2048Item item1 = row.get(j);
            Game2048Item item2 = row.get(j + 1);

            if (item1.getNumber() == item2.getNumber())
            {
                isMergeHappen = true;

                int val = item1.getNumber() + item2.getNumber();
                item1.setNumber(val);

                // 加分
                mScore += val;

                if (mGame2048Listener != null)
                {
                    mGame2048Listener.onScoreChange(mScore);
                }

                // 向前移动
                for (int k = j + 1; k < row.size() - 1; k++)
                {
                    row.get(k).setNumber(row.get(k + 1).getNumber());
                }

                row.get(row.size() - 1).setNumber(0);
                return;
            }

        }

    }
    /**
     * 产生一个数字
     */
    public void generateNum()
    {

        if (checkOver())
        {
            Log.e("TAG", "GAME OVER");
            if (mGame2048Listener != null)
            {
                mGame2048Listener.onGameOver();
            }
            return;
        }

        if (!isFull())
        {
            if (isMoveHappen || isMergeHappen)
            {
                Random random = new Random();
                int next = random.nextInt(16);
                Game2048Item item = mGame2048Items[next];

                while (item.getNumber() != 0)
                {
                    next = random.nextInt(16);
                    item = mGame2048Items[next];
                }

                item.setNumber(Math.random() > 0.75 ? 4 : 2);

                isMergeHappen = isMoveHappen = false;
            }

        }
    }
    /**
     * 检测当前所有的位置都有数字，且相邻的没有相同的数字
     *
     * @return
     */
    private boolean checkOver()
    {
        // 检测是否所有位置都有数字
        if (!isFull())
        {
            return false;
        }

        for (int i = 0; i < mColumn; i++)
        {
            for (int j = 0; j < mColumn; j++)
            {

                int index = i * mColumn + j;

                // 当前的Item
                Game2048Item item = mGame2048Items[index];
                // 右边
                if ((index + 1) % mColumn != 0)
                {
                    Log.e("TAG", "RIGHT");
                    // 右边的Item
                    Game2048Item itemRight = mGame2048Items[index + 1];
                    if (item.getNumber() == itemRight.getNumber())
                        return false;
                }
                // 下边
                if ((index + mColumn) < mColumn * mColumn)
                {
                    Log.e("TAG", "DOWN");
                    Game2048Item itemBottom = mGame2048Items[index + mColumn];
                    if (item.getNumber() == itemBottom.getNumber())
                        return false;
                }
                // 左边
                if (index % mColumn != 0)
                {
                    Log.e("TAG", "LEFT");
                    Game2048Item itemLeft = mGame2048Items[index - 1];
                    if (itemLeft.getNumber() == item.getNumber())
                        return false;
                }
                // 上边
                if (index + 1 > mColumn)
                {
                    Log.e("TAG", "UP");
                    Game2048Item itemTop = mGame2048Items[index - mColumn];
                    if (item.getNumber() == itemTop.getNumber())
                        return false;
                }

            }

        }

        return true;

    }
    /**
     * 是否填满数字
     *
     * @return
     */
    private boolean isFull()
    {
        // 检测是否所有位置都有数字
        for (int i = 0; i < mGame2048Items.length; i++)
        {
            if (mGame2048Items[i].getNumber() == 0)
            {
                return false;
            }
        }
        return true;
    }

    public void restart()
    {
        for (Game2048Item item : mGame2048Items)
        {
            item.setNumber(0);
        }
        mScore = 0;
        if (mGame2048Listener != null)
        {
            mGame2048Listener.onScoreChange(mScore);
        }
        isMoveHappen = isMergeHappen = true;
        generateNum();
    }

}
