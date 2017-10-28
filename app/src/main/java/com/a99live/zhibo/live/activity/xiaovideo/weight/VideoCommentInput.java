package com.a99live.zhibo.live.activity.xiaovideo.weight;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.xiaovideo.VideoViewInterface;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class VideoCommentInput extends RelativeLayout implements TextWatcher, View.OnClickListener {

    private EditText editText;
    private InputMode inputMode = InputMode.NONE;
    private ImageButton send;
    private VideoViewInterface videoViewInterface;

    public void setVideoViewInteface(VideoViewInterface videoViewInterface){
        this.videoViewInterface = videoViewInterface;
    }

    public VideoCommentInput(Context context) {
        this(context,null);
    }

    public VideoCommentInput(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoCommentInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.videocomment_input,this,true);
        initView();
    }

    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode){
        updateView(mode);
    }

    private void initView(){
        editText = (EditText) findViewById(R.id.edit_input);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    updateView(InputMode.TEXT);
                }
            }
        });
        send = (ImageButton) findViewById(R.id.send);
        send.setOnClickListener(this);
        send.setVisibility(GONE);
    }

    private void updateView(InputMode mode){
        if (mode == inputMode) return;
//        if (isHoldVoiceBtn){
//            return;
//        }
        leavingCurrentState();
        switch (inputMode = mode){
            case MORE:
//                morePanel.setVisibility(VISIBLE);
                break;
            case TEXT:
                if (editText.requestFocus()){
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                }
                break;
            case VOICE:
//                voicePanel.setVisibility(VISIBLE);
//                textPanel.setVisibility(GONE);
//                btnVoice.setVisibility(GONE);
//                btnKeyboard.setVisibility(VISIBLE);
                break;
            case EMOTICON:
//                if (!isEmoticonReady) {
//                    prepareEmoticon();
//                }
//                emoticonPanel.setVisibility(VISIBLE);
                break;
        }
    }

    private void leavingCurrentState(){
        switch (inputMode){
            case TEXT:
                View view = ((Activity) getContext()).getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editText.clearFocus();
                break;
            case MORE:
//                morePanel.setVisibility(GONE);
                break;
            case VOICE:
//                voicePanel.setVisibility(GONE);
//                textPanel.setVisibility(VISIBLE);
//                btnVoice.setVisibility(VISIBLE);
//                btnKeyboard.setVisibility(GONE);
                break;
            case EMOTICON:
//                emoticonPanel.setVisibility(GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean isSendvisible = s!=null&&s.length()>0;
        if (isSendvisible){
            send.setVisibility(VISIBLE);
        }else{
            send.setVisibility(GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                setInputMode(InputMode.NONE);
                if (videoViewInterface!= null){
                    videoViewInterface.sendText(getText());
                }
                break;
        }
    }

    public Editable getText(){
        return editText.getText();
    }

    public void sendOk() {
        editText.setText("");
    }

    public enum InputMode{
        TEXT,
        VOICE,
        EMOTICON,
        MORE,
        VIDEO,
        NONE,
    }
}
