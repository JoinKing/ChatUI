package com.king.chatui.utils;

public class MessageType {
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



}
