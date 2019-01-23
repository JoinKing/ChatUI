package com.king.chatui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwq.lib_common.base.BaseActivity;
import com.hwq.lib_common.bus.RxBus;
import com.hwq.lib_common.bus.RxSubscriptions;
import com.hwq.lib_common.utils.FixMemLeak;
import com.hwq.lib_common.utils.KeyBoardUtils;
import com.hwq.lib_common.widget.refreshlayout.RefreshListenerAdapter;
import com.hwq.lib_common.widget.refreshlayout.header.progresslayout.ProgressLayout;
import com.hwq.lib_common.widget.refreshlayout.view.RefreshLayout;
import com.king.chatui.adapter.ChatAdapter;
import com.king.chatui.adapter.ChatFragmentPagerAdapter;
import com.king.chatui.entity.MessageEntity;
import com.king.chatui.fragment.ChatEmotionFragment;
import com.king.chatui.fragment.ChatFunctionFragment;
import com.king.chatui.listenter.ChatListenter;
import com.king.chatui.utils.MediaManager;
import com.king.chatui.utils.MessageType;
import com.king.chatui.view.NoScrollViewPager;
import com.king.chatui.view.image.ZoomImageActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class ChatActivity extends AppCompatActivity implements View.OnLayoutChangeListener, ChatListenter, SwipeRefreshLayout.OnRefreshListener {
    private ChatAdapter chatAdapter;
    private RecyclerView chat_list;
    private LinearLayoutManager layoutManager;
    private ArrayList<Fragment> fragments;
    private ChatFragmentPagerAdapter adapter;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;

    private RelativeLayout emotion_layout;
    private NoScrollViewPager viewpager;
    private EditText etInput;
    private ImageView emotion_add;
    private ImageView emotion_voice;
    private TextView tvSend;
    private ImageView emotionButton;
    private SwipeRefreshLayout mSwipeRefresh;//刷新控件
    private TextView voiceText;
    private List<MessageEntity> messageEntityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSwipeRefresh = findViewById(R.id.rlRefressh);
        mSwipeRefresh.setOnRefreshListener(this);
        chat_list = findViewById(R.id.chat_list);
        etInput = findViewById(R.id.etInput);
        emotion_add = findViewById(R.id.emotion_add);
        tvSend = findViewById(R.id.tvSend);
        emotion_layout = findViewById(R.id.emotion_layout);
        viewpager = findViewById(R.id.viewpager);
        emotion_voice = findViewById(R.id.emotion_voice);
        voiceText = findViewById(R.id.voice_text);
        emotionButton = findViewById(R.id.emotion_button);
        findViewById(R.id.iv_loading).addOnLayoutChangeListener(this);
        /**
         *  rippleView 刷新
         *  loadingView 加载
         */
//        ProgressLayout rippleView = new ProgressLayout(this);
//        rlRefressh.setHeaderView(rippleView);
//        rlRefressh.setEnableLoadmore(false);
        //初始化Rxbus
        initRxBus();
        //初始化数据控件
        initData();

    }

    Disposable disposable;

    private void initRxBus() {
        disposable = RxBus.getDefault().toObservable(MessageEntity.class).subscribe(new Consumer<MessageEntity>() {
            @Override
            public void accept(MessageEntity messageEntity) throws Exception {
                chatAdapter.add(messageEntity);
                stick();
            }
        });
        RxSubscriptions.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(disposable);
        RxSubscriptions.clear();
        //解决华为手机输入事件引起得内存泄漏问题
        FixMemLeak.fixLeak(this);
    }

    public void stick() {
        chat_list.smoothScrollToPosition(0);
    }

    private void initData() {
        //功能面板
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        chatFunctionFragment = new ChatFunctionFragment();
        adapter = new ChatFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        fragments.add(chatEmotionFragment);
        fragments.add(chatFunctionFragment);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
        //聊天数据
        initList();//模拟聊天默认数据
        chatAdapter = new ChatAdapter(messageEntityList);
        chatAdapter.setListenter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(true);//列表翻转
        layoutManager.setStackFromEnd(false);//列表再底部开始展示，反转后由上面开始展示
        chat_list.setLayoutManager(layoutManager);
        chat_list.setAdapter(chatAdapter);
        chat_list.smoothScrollBy(chat_list.getHeight(), chat_list.getHeight());
        /**
         * 事件处理
         */
        ChatPresenter.with(this)
                .setChatFunctionFragment(chatFunctionFragment)
                .setEmotionView(emotion_layout)
                .setViewPager(viewpager)
                .setChatAdapter(chatAdapter)
                .bindToContent(mSwipeRefresh)
                .setRecyclerView(chat_list)
                .bindToEditText(etInput)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotion_add)
                .bindToSendButton(tvSend)
                .bindToVoiceButton(emotion_voice)
                .bindToVoiceText(voiceText)
                .build();
    }
    int index = 10;
    /**
     * 历史数据
     */
    private void initList() {
        for (int i = 0; i < 100; i++) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setType(MessageType.LEFT_CHAT_FILE_TYPE_TEXT);
            messageEntity.setContent("测试数据" + i);
            messageEntityList.add(messageEntity);
        }
        if (null!=chatAdapter){
            chatAdapter.notifyDataSetChanged();
            mSwipeRefresh.setRefreshing(false);
        }

    }


    public void dismiss(View view) {
        KeyBoardUtils.hideKeyBoard(this, view);
    }


    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldL, int oldT, int oldR, int oldB) {
        if (oldB != -1 && oldB > bottom) {
            chat_list.requestLayout();
            chat_list.post(new Runnable() {
                @Override
                public void run() {
                    stick();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chatFunctionFragment.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void chatImageOnClickListenter(String imageUrl) {
        startActivity(new Intent(this,ZoomImageActivity.class).putExtra("imageUrl",imageUrl));
    }

    @Override
    public void chatVoiceListenter(String voice) {
        MediaManager.playSound(voice, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

    }

    @Override
    public void onRefresh() {
        initList();
    }
}
