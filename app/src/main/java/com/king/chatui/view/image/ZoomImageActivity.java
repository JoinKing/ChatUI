package com.king.chatui.view.image;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.hwq.lib_common.bus.RxBus;
import com.hwq.lib_common.bus.RxSubscriptions;
import com.king.chatui.R;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class ZoomImageActivity extends Activity {
    private ZoomImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        initRxBus();
        imageView = findViewById(R.id.imageurl);
        Glide.with(this).load(getIntent().getStringExtra("imageUrl")).into(imageView);

    }
    private Disposable disposable;
    private void initRxBus() {
        disposable =   RxBus.getDefault()
                .toObservable(ZoomBus.class)
                .subscribe(new Consumer<ZoomBus>() {
                    @Override
                    public void accept(ZoomBus zoomBus) throws Exception {
                        onBackPressed();
                    }
                });
        RxSubscriptions.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxSubscriptions.remove(disposable);
    }
}
