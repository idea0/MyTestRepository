package com.haomee.consts;

public class CommonConsts {

	public static final String PREFERENCES_SETTING = "preferences_setting_yoker";
	public static final int NETWORK_TIMEOUT_LENGTH = 10000; // 网络超时
	public static String video_extension = ".mp4"; // 视频后缀名，统一算了，管他MP4还是flv,干脆不要后缀了，免得麻烦
	public static final int DOWNLOAD_STATUS_UNKNOW = -1;
	public static final int DOWNLOAD_STATUS_DOING = 1;
	public static final int DOWNLOAD_STATUS_DONE = 2;
	public static final int DOWNLOAD_STATUS_WAITING = 3;
	public static final int DOWNLOAD_STATUS_PAUSE = 4;
	public static final int DOWNLOAD_STATUS_ERROR = 5;
	public static final String PREFERENCES_SESSION_USERS = "yocker_session_users";

	public static int screenWidth = 0;
	public static int screenHeight = 0;

	public static String[] FORBIDDEN = { "qq", "邮箱", "见吗", "宾馆", "酒店", "你爸", "你妈", "微信", "手机", "电话", "地址", "住哪", "钱", "支付宝", "真名", "QQ", "Q号", "喜欢你", "约吗", "见面" };

	public static String CHAT_VIDEO_SEND = "你发起了一个共同观看视频的邀请";
	public static String CHAT_VIDEO_RECEIVE = "你收到过一个共同观看视频的邀请";

	public static String CHAT_VIDEO_BE_AGREED = "对方同意了你的共同观看视频邀请";
	public static String CHAT_VIDEO_AGREE = "你同意了对方的共同观看视频邀请";

	public static String CHAT_VIDEO_BE_REJECTED = "对方拒绝了你的共同观看视频邀请";
	public static String CHAT_VIDEO_REJECT = "你拒绝了对方共同观看视频的邀请";

	public static String CHAT_VIDEO_MISSED = "你错过了对方共同观看视频的邀请";
	public static String CHAT_VIDEO_BE_MISSED = "对方无法响应你的共同观看视频邀请";
	
	public static int colors[]={0xff0d81db , 0xff00b1c0, 0xff0dd7c2};//渐变颜色
	public static boolean IS_BUSY = false;//

}
