package com.a99live.zhibo.live.adapter;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.ChatEntity;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CenterImageSpan;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 消息列表的Adapter
 */
public class TCChatMsgListAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

    private static String TAG = TCChatMsgListAdapter.class.getSimpleName();
    private static final int ITEMCOUNT = 7;
    private List<ChatEntity> listMessage = null;
    private LayoutInflater inflater;
    private LinearLayout layout;
    private int mTotalHeight;
    public static final int TYPE_TEXT_SEND = 0;
    public static final int TYPE_TEXT_RECV = 1;
    private Context context;
    private ListView mListView;
    private ArrayList<ChatEntity> myArray = new ArrayList<>();
    private Handler mHandler;

//    private boolean  mBLiveAnimator = false;

    class AnimatorInfo {
        long createTime;

        public AnimatorInfo(long uTime) {
            createTime = uTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }

    private static final int MAXANIMATORCOUNT = 8;
//    private static final int MAXLISTVIEWHEIGHT = 450;
    private static final int MAXLISTVIEWHEIGHT = UIUtils.getDimen(R.dimen.dp_150);
    private static final int ANIMATORDURING = 8000;
    private static final int MAXITEMCOUNT = 50;
    private LinkedList<AnimatorSet> mAnimatorSetList;
    private LinkedList<AnimatorInfo> mAnimatorInfoList;
    private boolean mScrolling = false;
//    private boolean mCreateAnimator = false;

    public TCChatMsgListAdapter(Context context, ListView listview, List<ChatEntity> objects, Handler handler) {
        this.context = context;
        mListView = listview;
        inflater = LayoutInflater.from(context);
        this.listMessage = objects;
        this.mHandler = handler;

        mAnimatorSetList = new LinkedList<>();
        mAnimatorInfoList = new LinkedList<>();

        mListView.setOnScrollListener(this);
    }


    @Override
    public int getCount() {
        return listMessage.size();
    }

    @Override
    public Object getItem(int position) {
        return listMessage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 通过名称计算颜色
     */
    private int calcNameColor(String strName) {
        if (strName == null) return 0;
        byte idx = 0;
        byte[] byteArr = strName.getBytes();
        for (int i = 0; i < byteArr.length; i++) {
            idx ^= byteArr[i];
        }

        switch (idx & 0x7) {
            case 1:
                return context.getResources().getColor(R.color.yellow);
            case 2:
                return context.getResources().getColor(R.color.yellow);
            case 3:
                return context.getResources().getColor(R.color.yellow);
            case 4:
                return context.getResources().getColor(R.color.yellow);
            case 5:
                return context.getResources().getColor(R.color.yellow);
            case 6:
                return context.getResources().getColor(R.color.yellow);
            case 7:
                return context.getResources().getColor(R.color.yellow);
            case 0:
            default:
                return context.getResources().getColor(R.color.yellow);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SpannableString spanString;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_listview_msg, null);
            holder.sendContext = (TextView) convertView.findViewById(R.id.sendcontext);
            holder.level = (ImageView) convertView.findViewById(R.id.iv_level);
            convertView.setTag(R.id.tag_first, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.tag_first);
        }

        final ChatEntity item = listMessage.get(position);

//        if (mCreateAnimator && mBLiveAnimator) {
//            playViewAnimator(convertView, position, item);
//        }
            //每一个判断中自己生成，不用前部生成
//        spanString = new SpannableString(item.getSendName() + "  " + item.getContext());
        //此图已由图文混排代替
        holder.level.setVisibility(View.GONE);
        if (item.getType() == TCConstants.TEXT_TYPE) {
            spanString = new SpannableString("&*& "+item.getSendName() + "  " + item.getContext());
            // 根据名称计算颜色
            spanString.setSpan(new ForegroundColorSpan(calcNameColor(item.getSendName())),
                    0, item.getSendName().length() + 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.sendContext.setTextColor(context.getResources().getColor(R.color.white));
//            holder.level.setVisibility(View.VISIBLE);
        } else if (item.getType() == TCConstants.SYSTEM_TYPE){
            spanString = new SpannableString(item.getSendName() + "  " + item.getContext());
            //系统消息
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#ffce4a")), 0, item.getSendName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.sendContext.setTextColor(context.getResources().getColor(R.color.green));
//            holder.level.setVisibility(View.GONE);
        } else if (item.getType() == TCConstants.MEMBER_ENTER){
            spanString = new SpannableString(item.getSendName() + "  " + item.getContext());
            spanString.setSpan(new ForegroundColorSpan(calcNameColor(item.getSendName())),
                    0, item.getSendName().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            holder.level.setVisibility(View.GONE);
            holder.sendContext.setTextColor(context.getResources().getColor(R.color.green));
        } else if (item.getType() == TCConstants.PRAISE){
            spanString = new SpannableString("&*& "+item.getSendName() + "  " + item.getContext());
            spanString.setSpan(new ForegroundColorSpan(calcNameColor(item.getSendName())),
                    0, item.getSendName().length()+4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            holder.level.setVisibility(View.VISIBLE);
            holder.sendContext.setTextColor(context.getResources().getColor(R.color.purple));
        }else if (item.getType() == TCConstants.SEND_GIFT){
            spanString = new SpannableString("&*& "+item.getSendName() + "  " + item.getContext());
            spanString.setSpan(new ForegroundColorSpan(calcNameColor(item.getSendName())),
                    0, item.getSendName().length()+4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            holder.sendContext.setTextColor(context.getResources().getColor(R.color.sky_blue));
//            holder.level.setVisibility(View.VISIBLE);
        }else {
            spanString = new SpannableString(item.getSendName() + "  " + item.getContext());
            // 设置名称为粗体
//            StyleSpan boldStyle = new StyleSpan(Typeface.BOLD_ITALIC);
//            spanString.setSpan(boldStyle, 0, item.getSendName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanString.setSpan(new ForegroundColorSpan(calcNameColor(item.getSendName())),
                    0, item.getSendName().length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            holder.level.setVisibility(View.GONE);
            holder.sendContext.setTextColor(context.getResources().getColor(R.color.colorSendName5));
        }
        String level = item.getLevel();
        textFactory(holder.sendContext,spanString,level);
//        holder.sendContext.setText(spanString);
        // 设置控件实际宽度以便计算列表项实际高度
        //holder.sendContext.fixViewWidth(mListView.getWidth());
        holder.sendContext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null){
                    String id = item.getIdentity();
                    if (id != null && !TextUtils.isEmpty(id) && !"-1".equals(id)) {
                        Message message = mHandler.obtainMessage(TCConstants.OPEN_USER_BY_IDENTITY);
                        message.obj = id;
                        message.sendToTarget();
                    }
                }
            }
        });


        return convertView;
    }

    /**
     * // 评论内容，设置标签、链接跳转
     Spannable content = new SpannableStringBuilder("占位  " + comment.getContent());
     CenterImageSpan span = new CenterImageSpan(mActivity,R.drawable.ic_btn_ask,ImageSpan.ALIGN_BASELINE);
     // 用ImageSpan替换文本
     content.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
     holder.mAskText.setText(content);
     * @param tv
     * @param str
     */
    public void textFactory(TextView tv, SpannableString str,String level) {
//        Drawable drawable = context.getResources().getDrawable(R.drawable.level_1);
//        drawable.setBounds( 0,0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //需要处理的文本，&&是需要被替代的文本
//        SpannableString spannable = new SpannableString(str);
        int i = str.toString().indexOf("&*&");
        if (i < 0) {
            tv.setText(str);
            return;
        }
        int levelIsOk = 1;
        try {
            levelIsOk = Integer.parseInt(level);
        }catch (Exception e){
            levelIsOk = 1;
        }
        if (levelIsOk >80){
            levelIsOk = 80;
        }
        //要让图片替代指定的文字就要用ImageSpan
        int drawableId = getDrawableId(context, "level" + levelIsOk);
        CenterImageSpan span = new CenterImageSpan(context,drawableId, ImageSpan.ALIGN_BASELINE);
        str.setSpan(span, i, i + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(str);
    }

    public int getDrawableId(Context paramContext, String paramString) {
        return paramContext.getResources().getIdentifier(paramString,
                "drawable", paramContext.getPackageName());
    }

    static class ViewHolder {
        public TextView sendContext;
        public ImageView level;

    }

    /**
     * 停止View属性动画
     *
     * @param itemView 当前执行动画View
     */
    private void stopViewAnimator(View itemView) {
        AnimatorSet aniSet = (AnimatorSet) itemView.getTag(R.id.tag_second);
        if (null != aniSet) {
            aniSet.cancel();
            mAnimatorSetList.remove(aniSet);
        }
    }

    /**
     * 播放View属性动画
     *
     * @param itemView   动画对应View
     * @param startAlpha 初始透明度
     * @param duringTime 动画时长
     */
    private void playViewAnimator(View itemView, float startAlpha, long duringTime) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", startAlpha, 0f);
        AnimatorSet aniSet = new AnimatorSet();
        aniSet.setDuration(duringTime);
        aniSet.play(animator);
        aniSet.start();
        mAnimatorSetList.add(aniSet);
        itemView.setTag(R.id.tag_second, aniSet);
    }

    /**
     * 播放渐消动画
     *
     * @param pos 位置
     * @param view 执行动画View
     */
    public void playDisappearAnimator(int pos, View view) {
        int firstVisable = mListView.getFirstVisiblePosition();
        if (firstVisable <= pos) {
            playViewAnimator(view, 1f, ANIMATORDURING);
        } else {
            Log.d(TAG, "playDisappearAnimator->unexpect pos: " + pos + "/" + firstVisable);
        }
    }

    /**
     * 继续播放渐消动画
     *
     * @param itemView 执行动画View
     * @param position 位置
     * @param item
     */
    private void continueAnimator(View itemView, int position, final ChatEntity item) {
        int animatorIdx = listMessage.size() - 1 - position;

        if (animatorIdx < MAXANIMATORCOUNT) {
            float startAlpha = 1f;
            long during = ANIMATORDURING;

            stopViewAnimator(itemView);

            // 播放动画
            if (position < mAnimatorInfoList.size()) {
                AnimatorInfo info = mAnimatorInfoList.get(position);
                long time = info.getCreateTime();  //  获取列表项加载的初始时间
                during = during - (System.currentTimeMillis() - time);     // 计算动画剩余时长
                startAlpha = 1f * during / ANIMATORDURING;                    // 计算动画初始透明度
                if (during < 0) {   // 剩余时长小于0直接设置透明度为0并返回
                    itemView.setAlpha(0f);
                    Log.v(TAG, "continueAnimator->already end animator:" + position + "/" + item.getContext() + "-" + during);
                    return;
                }
            }

            // 创建属性动画并播放
            Log.v(TAG, "continueAnimator->pos: " + position + "/" + listMessage.size() + ", alpha:" + startAlpha + ", dur:" + during);
            playViewAnimator(itemView, startAlpha, during);
        } else {
            Log.v(TAG, "continueAnimator->ignore pos: " + position + "/" + listMessage.size());
        }
    }

    /**
     * 播放消失动画
     */
    private void playDisappearAnimator() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View itemView = mListView.getChildAt(i);
            if (null == itemView) {
                Log.w(TAG, "playDisappearAnimator->view not found: " + i + "/" + mListView.getCount());
                break;
            }

            // 更新动画创建时间
            int position = mListView.getFirstVisiblePosition() + i;
            if (position < mAnimatorInfoList.size()) {
                mAnimatorInfoList.get(position).setCreateTime(System.currentTimeMillis());
            } else {
                Log.e(TAG, "playDisappearAnimator->error: " + position + "/" + mAnimatorInfoList.size());
            }

            playViewAnimator(itemView, 1f, ANIMATORDURING);
        }
    }

    /**
     * 播放列表项动画
     *
     * @param itemView 要播放动画的列表项
     * @param position 列表项的位置
     * @param item     列表数据
     */
    private void playViewAnimator(View itemView, int position, final ChatEntity item) {
        if (!myArray.contains(item)) {  // 首次加载的列表项动画
            myArray.add(item);
            mAnimatorInfoList.add(new AnimatorInfo(System.currentTimeMillis()));
        }

        if (mScrolling) {  // 滚动时不播放动画，设置透明度为1
            itemView.setAlpha(1f);
        } else {
            continueAnimator(itemView, position, item);
        }
    }

    /**
     * 删除超过上限(MAXITEMCOUNT)的列表项
     */
    private void clearFinishItem() {
        // 删除超过的列表项
        while (listMessage.size() > MAXITEMCOUNT) {
            listMessage.remove(0);
            if (mAnimatorInfoList.size() > 0) {
                mAnimatorInfoList.remove(0);
            }
        }

        // 缓存列表延迟删除
        while (myArray.size() > (MAXITEMCOUNT << 1)) {
            myArray.remove(0);
        }

        while (mAnimatorInfoList.size() >= listMessage.size()) {
            Log.e(TAG, "clearFinishItem->error size: " + mAnimatorInfoList.size() + "/" + listMessage.size());
            if (mAnimatorInfoList.size() > 0) {
                mAnimatorInfoList.remove(0);
            } else {
                break;
            }
        }
    }

    /**
     * 重新计算ITEMCOUNT条记录的高度，并动态调整ListView的高度
     */
    private void redrawListViewHeight() {

        int totalHeight = 0;
        int start = 0, lineCount = 0;

        if (listMessage.size() <= 0) {
            return;
        }

        if (mTotalHeight >= MAXLISTVIEWHEIGHT) {
            return;
        }

        // 计算底部ITEMCOUNT条记录的高度
//        mCreateAnimator = false;    // 计算高度时不播放属性动画
        for (int i = listMessage.size() - 1; i >= start && lineCount < ITEMCOUNT; i--, lineCount++) {
            View listItem = getView(i, null, mListView);

            listItem.measure(View.MeasureSpec.makeMeasureSpec(MAXLISTVIEWHEIGHT, View.MeasureSpec.AT_MOST)
                    ,View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            // add item height
            totalHeight += listItem.getMeasuredHeight();
            if(totalHeight > MAXLISTVIEWHEIGHT) {
                totalHeight = MAXLISTVIEWHEIGHT;
                break;
            }
        }
//        mCreateAnimator = true;



        mTotalHeight = totalHeight;
        // 调整ListView高度
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight + (mListView.getDividerHeight() * (lineCount - 1));
        mListView.setLayoutParams(params);
    }

    /**
     * 停止当前所有属性动画并重置透明度
     */
    private void stopAnimator() {
        // 停止动画
        for (AnimatorSet anSet : mAnimatorSetList) {
            anSet.cancel();
        }
        mAnimatorSetList.clear();
    }

    /**
     * 重置透明度
     */
    private void resetAlpha() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            View view = mListView.getChildAt(i);
            view.setAlpha(1f);
        }
    }

    /**
     * 继续可视范围内所有动画
     */
    private void continueAllAnimator() {
//        int startPos = mListView.getFirstVisiblePosition();
//
//        for (int i = 0; i < mListView.getChildCount(); i++) {
//            View view = mListView.getChildAt(i);
//            if (null != view && startPos + i < listMessage.size()) {
//                continueAnimator(view, startPos + i, listMessage.get(startPos + i));
//            }
//        }
    }

    /**
     * 重载notifyDataSetChanged方法实现渐消动画并动态调整ListView高度
     */
    @Override
    public void notifyDataSetChanged() {
        Log.v(TAG, "notifyDataSetChanged->scroll: " + mScrolling);
//        if (mScrolling) {
//            // 滑动过程中不刷新
//            super.notifyDataSetChanged();
//            return;
//        }
//
//        // 删除多余项
        clearFinishItem();
//
//        if (mBLiveAnimator) {
//            // 停止之前动画
//            stopAnimator();
//
//            // 清除动画
//            mAnimatorSetList.clear();
//        }

        super.notifyDataSetChanged();

        // 重置ListView高度
        redrawListViewHeight();

//        if (mBLiveAnimator && listMessage.size() >= MAXITEMCOUNT) {
//            continueAllAnimator();
//        }

        // 自动滚动到底部
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.setSelection(mListView.getCount() - 1);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_FLING:
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
//                if (mBLiveAnimator) {
//                    // 开始滚动时停止所有属性动画
//                    stopAnimator();
//                    resetAlpha();
//                }
                mScrolling = true;
                break;
            case SCROLL_STATE_IDLE:
                mScrolling = false;
//                if (mBLiveAnimator) {
//                    // 停止滚动时播放渐消动画
//                    playDisappearAnimator();
//                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
