package com.king.chatui;

import com.hwq.lib_common.base.BaseApplication;
import com.hwq.lib_common.utils.Utils;

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(getApplicationContext());
    }
}
