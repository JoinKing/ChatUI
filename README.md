# ChatUI
/--关于聊天界面简介--/
该聊天ui使用mvc的方式集成，部分具类导入我自己库，使用的时候可以不导入，自行copy出来使用


1、已实现功能（实现文字，图片，语音，消息，6.0动态权限的获取）
2、图片点击后可以进行放大缩小
3、语音消息的播放
4、工程包介绍

  （0）ChatActivity 聊天界面  ChatPresenter 聊天界面的事件处理
  （1）adapter
      ChatAdapter 消息适配器（包括接收和发送holder）
      ChatFragmentPagerAdapter （Fragment的管理，包括功能面板和表情面板，如果更多可以自行添加）
  （2）entity    
      MessageEntity 消息实体类
  （3）fragment 功能面板的fragment
      ChatEmotionFragment 表情面板
      ChatFunctionFragment 功能面板
  （4）holder
      ReceiveHolder  接收者的holder
      SendHolder 发送者holder
  （5）listenter （holder所有事件回掉）
      ChatListenter 暂时仅仅添加语音播放和图片点击的事件
  （6）utils
      AudioRecorderUtils 录音工具类
      PhotoUtils 拍照工具类
      MessageType 消息类型及状态工具类
      MediaManager 播放语音工具类
      
  
  （7）view
      NoScrollViewPager 去除滑动的ViewPager
      image 图片放大缩小工具 ，打开直接（startActivity(new Intent(this,ZoomImageActivity.class).putExtra("imageUrl",imageUrl));）
      
5、消息及状态介绍
    /** LEFT-接受消息  RIGHT-发送消息**/

    /**0x006    -    文本消息 - **/
    /**0x007    -    文件消息 - **/
    /**0x008    -    图片消息 - **/
    /**0x009    -    语音消息 - **/
    /**0x010    -    名片消息 - **/
    /**0x011    -   超链接消息- **/

    //LEFT-接受消息
    public static final int LEFT_CHAT_FILE_TYPE_TEXT = 0x006;
    public static final int LEFT_CHAT_FILE_TYPE_FILE = 0x007;
    public static final int LEFT_CHAT_FILE_TYPE_IMAGE = 0x008;
    public static final int LEFT_CHAT_FILE_TYPE_VOICE = 0x009;
    public static final int LEFT_CHAT_FILE_TYPE_CONTACT = 0x010;
    public static final int LEFT_CHAT_FILE_TYPE_LINK = 0x011;
    //RIGHT-发送消息
    public static final int RIGHT_CHAT_FILE_TYPE_TEXT = 0x012;
    public static final int RIGHT_CHAT_FILE_TYPE_FILE = 0x013;
    public static final int RIGHT_CHAT_FILE_TYPE_IMAGE = 0x014;
    public static final int RIGHT_CHAT_FILE_TYPE_VOICE = 0x015;
    public static final int RIGHT_CHAT_FILE_TYPE_CONTACT = 0x016;
    public static final int RIGHT_CHAT_FILE_TYPE_LINK = 0x017;


    /** 0x003-发送中  0x004-发送失败  0x005-发送成功**/
    public static final int CHAT_ITEM_SENDING = 0x003;
    public static final int CHAT_ITEM_SEND_ERROR = 0x004;
    public static final int CHAT_ITEM_SEND_SUCCESS = 0x005;
    
  5.消息的发送可以使用我框架中的RxBus进行通讯，统一管理，使用起来更加的简洁  
    例如：RxBus.getDefault().post(messageEntity);
  
  
--图片消息--
![Image text](https://github.com/JoinKing/ChatUI/blob/master/png/Screenshot_20190123-152630.jpg)
--语音消息--
![Image text](https://github.com/JoinKing/ChatUI/blob/master/png/Screenshot_20190123-152704.jpg)
--功能面板--
![Image text](https://github.com/JoinKing/ChatUI/blob/master/png/Screenshot_20190123-152714.jpg)
--表情面板--
![Image text](https://github.com/JoinKing/ChatUI/blob/master/png/Screenshot_20190123-152710.jpg)
