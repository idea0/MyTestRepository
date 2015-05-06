package com.haomee.consts;

import com.haomee.yocker.YockerApplication;

public class PathConsts {

	public static final String ROOT_PATH = "/Yocker"; // 应用根目录
	public static final String DIR_TEMP = ROOT_PATH + "/temp/"; // 临时
	public static final String VIDEO_CACHE_DIR = ROOT_PATH + "/videoCache"; // 视频缓存目录
	public static final String IMAGE_CACHDIR = ROOT_PATH + "/imgCache/"; // 图片缓存目录

	public static final String DOWNLOAD_EMOTIONS = ROOT_PATH + "/emotions/";// 表情目录

	public static final String download_log_name = "v_download"; // 下载日志文件（后来的版本把后缀名.log去掉了）

	// 脏词库文件
	public static final String BAD_WORDS_PATH = ROOT_PATH + "/badwords/";// 臧词路径
	public static final String BAD_WORDS_FILE = "yocker_bad_words.json";// 臧词文件

	public static final String DOMAIN = "http://api.yocker.haomee.cn/";
	public static final String PREFIX_URL = DOMAIN + "?&pf=1&android_version=" + android.os.Build.VERSION.SDK_INT + "&app_version=" + YockerApplication.appVersion + "&app_channel=" + YockerApplication.channelName_encode;// 网络根路径

	public static final String URL_UPDATE = PREFIX_URL + "&m=Index&a=updateVersion"; // 版本更新

	public static final String URL_PLAY_JS = PREFIX_URL + "&m=Api&a=newPlay&android=1&id="; // 获取播放文件js接口
	public static final String URL_PLAY = PREFIX_URL + "&m=Index&a=htmlPlay&android=1"; // 获取播放文件,可切换清晰度

	// 离线数据对应路径
	public static final String OFFLINE_DATA = ROOT_PATH + "/offlineData/"; // 离线数据（没有网络时的缓存数据）
	public static final String BACKUP_DB_DOWNLOAD = "db_download.backup";
	public static final String download_m3u8 = "v_download.m3u8"; // 离线m3u8文件

	// http://api.yocker.haomee.cn/?m=Index&a=videoList&last_id=&limit=
	public static final String URL_VIDEO_LIST = PREFIX_URL + "&m=Index&a=videoList";// 视频列表
//	http://api.yocker.haomee.cn/?m=Index&a=randVideo&limit=10
	public static final String URL_RAND_VIDEO=PREFIX_URL+"&m=Index&a=randVideo";//随机视频列表
	// http://api.yocker.haomee.cn/?m=User&a=login&did=123&check=d9b1d7db4cd6e70935368a1efb10e377
	public static final String URL_REGEST_USER_DATA = PREFIX_URL + "&m=User&a=login";
	// http://api.yocker.haomee.cn/?m=User&a=editUserInfo&id=1&accesskey=&username=php&head_pic=
	public static final String URL_UPDATE_CURRENT_DATA = PREFIX_URL + "&m=User&a=editUserInfo";
	// http://api.yocker.haomee.cn/?m=User&a=randUsername
	public static final String URL_RAND_USERNAME = PREFIX_URL + "&m=User&a=randUsername";// 随机用户名
	// http://api.yocker.haomee.cn/?m=User&a=feedback&cont=&contact=jfdkfjkd&Luid=1&app_version=1&phone_version=&system_version=
	public static final String URL_OPINION_FEEDBACK = PREFIX_URL + "&m=User&a=feedback" + "&app_channel=" + YockerApplication.channelName_encode;// 意见反馈
	// http://api.yocker.haomee.cn/?m=Index&a=videoList&last_id=&limit=

	public static final String URL_GET_ONLINEUSERS = PREFIX_URL + "&m=User&a=getOnlineUsers";

	// http://cdn1.haomee.cn/yocker/yonghuxieyi/
	public static final String URL_USER_AGREE = "http://cdn1.haomee.cn/yocker/yonghuxieyi/";// 用户协议
	// http://api.yocker.haomee.cn/?m=User&a=badwords
	public static final String URL_BAD_WORDS = PREFIX_URL + "&m=User&a=badwords";// 藏词库
	// http://api.yocker.haomee.cn/?m=User&a=report&Luid=1&object_id=1&object_type=1&content=&type=
	public static final String URL_USER_REPORT = PREFIX_URL + "&m=User&a=report";

	public static final String URL_RECENT_CONTACTS = PREFIX_URL + "&m=User&a=getUserInfoByHx";
}
