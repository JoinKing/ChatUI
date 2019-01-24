package com.king.chatui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwq.lib_common.bus.RxBus;
import com.hwq.lib_common.rxpermissions.RxPermissions;
import com.hwq.lib_common.utils.ToastUtils;
import com.hwq.lib_common.widget.refreshlayout.RefreshListenerAdapter;
import com.hwq.lib_common.widget.refreshlayout.view.RefreshLayout;
import com.king.chatui.adapter.ChatAdapter;
import com.king.chatui.entity.MessageEntity;
import com.king.chatui.fragment.ChatFunctionFragment;
import com.king.chatui.utils.AudioRecorderUtils;
import com.king.chatui.utils.MessageType;

import io.reactivex.functions.Consumer;


public class ChatPresenter {
    private static final String TAG = "ChatPresenter";
    private static final String SHARE_PREFERENCE_NAME = "0x001";
    private static final String SHARE_PREFERENCE_TAG = "0x002";
    private Activity mActivity;
    private InputMethodManager mInputManager;
    private SharedPreferences sp;
    private View mEmotionLayout;
    private EditText mEditText;
    private TextView mVoiceText;
    private View mContentView;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private View mSendButton;
    private View mAddButton;
    private Boolean isShowEmotion = false;
    private Boolean isShowAdd = false;
    private boolean isShowVoice = false;
    private AudioRecorderUtils mAudioRecorderUtils;

    private ChatPresenter() {

    }

    public static ChatPresenter with(Activity activity) {
        ChatPresenter chatPresenter = new ChatPresenter();
        chatPresenter.mActivity = activity;
        chatPresenter.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        chatPresenter.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return chatPresenter;
    }

    public ChatPresenter bindToContent(View contentView) {
        mContentView = contentView;

        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ChatPresenter bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    lockContentHeight();
                    hideEmotionLayout(true);
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    mAddButton.setVisibility(View.GONE);
                    mSendButton.setVisibility(View.VISIBLE);
                } else {
                    mAddButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        return this;
    }

    public ChatPresenter bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (isShowAdd) {
                        mViewPager.setCurrentItem(0);
                        isShowEmotion = true;
                        isShowAdd = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowEmotion = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(0);
                    isShowEmotion = true;
                }
                hideVoiceShowInput();
            }
        });
        return this;
    }

    public ChatPresenter bindToAddButton(View addButton) {
        mAddButton = addButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (isShowEmotion) {
                        mViewPager.setCurrentItem(1);
                        isShowAdd = true;
                        isShowEmotion = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowAdd = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(1);
                    isShowAdd = true;
                }
                hideVoiceShowInput();
            }
        });
        return this;
    }

    /**
     * hide mVoiceText
     * show mEditText
     */
    public void hideVoiceShowInput(){
        mVoiceText.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
    }

    public ChatPresenter bindToSendButton(View sendButton) {
        mSendButton = sendButton;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setHeader("https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=827964771,1400326594&fm=85&s=0900CD114239479C00A47CE80300F022");
                messageEntity.setType(MessageType.RIGHT_CHAT_FILE_TYPE_TEXT);
                messageEntity.setSendState(MessageType.CHAT_ITEM_SENDING);
                messageEntity.setContent(mEditText.getText().toString().trim());
                mEditText.setText("");
                RxBus.getDefault().post(messageEntity);

            }
        });
        return this;
    }

    public ChatPresenter bindToVoiceButton(final ImageView voiceButton) {
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowVoice) {
                    voiceButton.setImageResource(R.mipmap.icon_voice);
                } else {
                    voiceButton.setImageResource(R.mipmap.icon_keyboard);

                }
                isShowVoice = !isShowVoice;
                hideEmotionLayout(false);
                hideSoftInput();
                mVoiceText.setVisibility(mVoiceText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEditText.setVisibility(mVoiceText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        return this;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ChatPresenter bindToVoiceText(TextView voiceText) {
        mVoiceText = voiceText;
        mVoiceText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获得x轴坐标
                int x = (int) event.getX();
                // 获得y轴坐标
                int y = (int) event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (isPermissVoice()){
                            mVoiceText.setText("松开结束");
//                        mPopVoiceText.setText("手指上滑，取消发送");
                            mVoiceText.setTag("1");
                            mAudioRecorderUtils.startRecord(mActivity);

                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isPermissVoice()){
                            if (wantToCancel(x, y)) {
                                mVoiceText.setText("松开结束");
//                            mPopVoiceText.setText("松开手指，取消发送");
                                mVoiceText.setTag("2");
                            } else {
                                mVoiceText.setText("松开结束");
//                            mPopVoiceText.setText("手指上滑，取消发送");
                                mVoiceText.setTag("1");
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (isPermissVoice()){
                            //                        mVoicePop.dismiss();
                            if ("2".equals(mVoiceText.getTag())) {
                                //取消录音（删除录音文件）
                                mAudioRecorderUtils.cancelRecord();
                            } else {
                                //结束录音（保存录音文件）
                                mAudioRecorderUtils.stopRecord();
                            }
                            mVoiceText.setText("按住说话");
                            mVoiceText.setTag("3");
                        }

                        break;
                }
                return true;
            }
        });
        return this;
    }


    boolean isP = false;

    /**
     * 6.0 以上动态开启语音权限
     * @return
     */
    @SuppressLint("CheckResult")
    public boolean isPermissVoice() {
        String p[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
        RxPermissions permission = new RxPermissions(mActivity);
        permission.request(p).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    isP = true;
                } else {
                    ToastUtils.showShort("请打开录音权限");
                }
            }
        });
        return isP;
    }

    private boolean wantToCancel(int x, int y) {
        // 超过按钮的宽度
        if (x < 0 || x > mVoiceText.getWidth()) {
            return true;
        }
        // 超过按钮的高度
        if (y < -50 || y > mVoiceText.getHeight() + 50) {
            return true;
        }
        return false;
    }

    public ChatPresenter setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    private ChatAdapter mChatAdapter;

    public ChatPresenter setChatAdapter(ChatAdapter adapter) {
        mChatAdapter = adapter;
        return this;
    }


    public ChatPresenter setChatFunctionFragment(ChatFunctionFragment functionFragment) {
//        this.chatFunctionFragment = functionFragment;
//        chatFunctionFragment.setCallback(this);
        return this;
    }

    public ChatPresenter setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        return this;
    }

    public ChatPresenter setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mChatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        hideEmotionLayout(false);
                        hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this;
    }

    public ChatPresenter build() {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        mAudioRecorderUtils = new AudioRecorderUtils();

//        View view = View.inflate(mActivity, R.layout.layout_microphone, null);
//        mVoicePop = new PopupWindowFactory(mActivity, view);
//
//        //PopupWindow布局文件里面的控件
//        final ImageView mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
//        final TextView mTextView = (TextView) view.findViewById(R.id.tv_recording_time);
//        mPopVoiceText = (TextView) view.findViewById(R.id.tv_recording_text);
//        //录音回调
        mAudioRecorderUtils.setOnAudioStatusUpdateListener(new AudioRecorderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
//                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
//                mTextView.setText(Utils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(long time, String filePath) {
//                mTextView.setText(Utils.long2String(0));
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setHeader("http://img0.imgtn.bdimg.com/it/u=401967138,750679164&fm=26&gp=0.jpg");
                messageEntity.setType(MessageType.RIGHT_CHAT_FILE_TYPE_VOICE);
                messageEntity.setFilepath(filePath);
                messageEntity.setSendState(MessageType.CHAT_ITEM_SENDING);
                messageEntity.setVoiceTime(time);
                RxBus.getDefault().post(messageEntity);

            }

            @Override
            public void onError() {
                mVoiceText.setVisibility(View.GONE);
                mEditText.setVisibility(View.VISIBLE);
            }
        });
        return this;
    }

    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, 768);
        }
        hideSoftInput();
        Log.e(TAG, "showEmotionLayout: ->" + softInputHeight);
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    public void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void lockContentHeight() {
        Log.e(TAG, "lockContentHeight: ->" + mContentView.getHeight());
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            Log.w("ChatPresenter", "Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
            Log.e(TAG, "getSupportSoftInputHeight: ->" + softInputHeight);
            sp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


}
