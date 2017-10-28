package com.a99live.zhibo.live.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.event.ShowLiveDialogEvent;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.view.ShowWeb;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MessageReceiver extends XGPushBaseReceiver {
//	private Intent intent = new Intent("com.a99live.zhibo.a99live.activity");
	public static final String LogTag = "livelog";
	private static int notificationId = 0;
	private static int requestId = 0;

	private void show(Context context, String text) {
//		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	// 通知展示
	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) {
			return;
		}
//		XGNotification notific = new XGNotification();
//		notific.setMsg_id(notifiShowedRlt.getMsgId());
//		notific.setTitle(notifiShowedRlt.getTitle());
//		notific.setContent(notifiShowedRlt.getContent());
//		// notificationActionType==1为Activity，2为url，3为intent
//		notific.setNotificationActionType(notifiShowedRlt
//				.getNotificationActionType());
//		// Activity,url,intent都可以通过getActivity()获得
//		notific.setActivity(notifiShowedRlt.getActivity());
////                        notific.setActivity("com.a99live.zhibo.live.activity.WelcomeActivity");
//		notific.setUpdate_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//				.format(Calendar.getInstance().getTime()));
//		NotificationService.getInstance(context).save(notific);
//		Intent intent = new Intent("com.cn.developer.xgdemo.activity.UPDATE_LISTVIEW");
////                        Intent intent = new Intent();
////                        intent.setClass(context, LivePlayerActivity.class);
//		context.sendBroadcast(intent);
//		show(context, "您有1条新消息, " + "通知被展示 ， " + notifiShowedRlt.toString());

	}

	@Override
	public void onUnregisterResult(Context context, int errorCode) {
		if (context == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "反注册成功";
		} else {
			text = "反注册失败" + errorCode;
		}
		Log.d(LogTag, text);
		show(context, text);

	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "\"" + tagName + "\"设置成功";
		} else {
			text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
		}
		Log.d(LogTag, text);
		show(context, text);

	}

	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
		if (context == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = "\"" + tagName + "\"删除成功";
		} else {
			text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
		}
		Log.d(LogTag, text);
		show(context, text);

	}

	// 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
		if (context == null || message == null) {
			return;
		}
		String text = "";
		if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			// 通知在通知栏被点击啦。。。。。
			// APP自己处理点击的相关动作
			// 这个动作可以在activity的onResume也能监听，请看第3点相关内容
			text = "通知被打开 :" + message;
		} else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
			// 通知被清除啦。。。。
			// APP自己处理通知被清除后的相关动作
			text = "通知被清除 :" + message;
		}
//		Toast.makeText(context, "广播接收到通知被点击:" + message.toString(),
//				Toast.LENGTH_SHORT).show();
		// 获取自定义key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// key1为前台配置的key
				if (!obj.isNull("key")) {
					String value = obj.getString("key");
					Log.d(LogTag, "get custom value:" + value);
				}
				// ...
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// APP自主处理的过程。。。
		// APP自主处理消息的过程...

		Log.d(LogTag, text);
		show(context, text);
	}

	@Override
	public void onRegisterResult(Context context, int errorCode,
			XGPushRegisterResult message) {
		// TODO Auto-generated method stub
		if (context == null || message == null) {
			return;
		}
		String text = "";
		if (errorCode == XGPushBaseReceiver.SUCCESS) {
			text = message + "注册成功";
			// 在这里拿token
			String token = message.getToken();
		} else {
			text = message + "注册失败，错误码：" + errorCode;
		}
		Log.d(LogTag, text);
		show(context, text);
	}

	// 消息透传
	@Override
	public void onTextMessage(Context context, XGPushTextMessage message) {
		// TODO Auto-generated method stub
		String text = "收到消息:" + message.toString();
		// 获取自定义key-value
		String customContent = message.getCustomContent();
		if (customContent != null && customContent.length() != 0) {
			try {
				JSONObject obj = new JSONObject(customContent);
				// key1为前台配置的key
				if (!obj.isNull("key")) {
					String value = obj.getString("key");
					Log.d(LogTag, "get custom value:" + value);
				}
				// ...
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

//		String customContent = message.getCustomContent();
		ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(customContent);
		if (listMapByJson.size() > 0){
			Map<String, String> map = listMapByJson.get(0);
			if (map.containsKey("ext")){
				String ext = map.get("ext");
				ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(ext);
				Map<String, String> map1 = listMapByJson1.get(0);
				String bus_type = "0";
				if (map1.containsKey("bus_type")) {
					bus_type = map1.get("bus_type");
				}
				if ("live_follow".equals(bus_type)) {//进直播的推送
					String room_id = map1.get("room_id");
					String ucode = map1.get("ucode");
					if (Tools.isAppInPhone(context,context.getPackageName())<2){
//
						Intent mainIntent = new Intent(context, LivePlayerActivity.class);
						mainIntent.setAction(Intent.ACTION_VIEW);
						mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
						mainIntent.putExtra("room_id", room_id);
						mainIntent.putExtra("ucode", ucode);
//						mainIntent.putExtra("eventcontent",message.getContent());

						Intent intent = new Intent(context,ClickNotificationReceiver.class);
						intent.putExtra("realIntent",mainIntent);

						PendingIntent mainPendingIntent = PendingIntent.getBroadcast(context, requestId++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
						//获取NotificationManager实例
						NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						//实例化NotificationCompat.Builde并设置相关属性
						NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context)
								//设置小图标
								.setSmallIcon(R.mipmap.ic_launcher)
								.setDefaults(Notification.DEFAULT_ALL)
								.setPriority(Notification.PRIORITY_MAX)
								//点击通知后自动清除
								.setAutoCancel(true)
								//设置通知标题
								.setContentTitle(message.getTitle())
								//设置通知内容
								.setContentText(message.getContent())
								.setContentIntent(mainPendingIntent);
						//设置通知时间，默认为系统发出通知的时间，通常不用设置
						//.setWhen(System.currentTimeMillis());
						//通过builder.build()方法生成Notification对象,并发送通知,id=1
						notifyManager.notify(notificationId++, myBuilder.build());
//
//
//
					} else {
						EventBus.getDefault().post(new ShowLiveDialogEvent(room_id, ucode, message.getContent()));
						return;
					}
				}else if("admin_push".equals(bus_type)){
					String url = map1.get("url");
					if (!TextUtils.isEmpty(url)) {
						goWebView(context, message, url);
					}
				}else{//普通推送
						Intent mainIntent = new Intent(context, ClickNotificationReceiver.class);
						PendingIntent mainPendingIntent = PendingIntent.getBroadcast(context, requestId++, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						//获取NotificationManager实例
						NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						//实例化NotificationCompat.Builde并设置相关属性
						NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context)
								//设置小图标
								.setSmallIcon(R.mipmap.ic_launcher)
								.setDefaults(Notification.DEFAULT_ALL)
								.setPriority(Notification.PRIORITY_MAX)
								//点击通知后自动清除
								.setAutoCancel(true)
								//设置通知标题
								.setContentTitle(message.getTitle())
								//设置通知内容
								.setContentText(message.getContent())
								.setContentIntent(mainPendingIntent);
						//设置通知时间，默认为系统发出通知的时间，通常不用设置
						//.setWhen(System.currentTimeMillis());
						//通过builder.build()方法生成Notification对象,并发送通知,id=1
						notifyManager.notify(notificationId++, myBuilder.build());
//
//

				}
			}
		}else{
				//获取NotificationManager实例
			Intent clickIntent = new Intent(context,ClickNotificationReceiver.class);


			NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			//实例化NotificationCompat.Builde并设置相关属性
			NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context)
					//设置小图标
					.setSmallIcon(R.mipmap.ic_launcher)
					.setDefaults(Notification.DEFAULT_ALL)
					.setPriority(Notification.PRIORITY_MAX)
					//点击通知后自动清除
					.setAutoCancel(true)
					//设置通知标题
					.setContentTitle(message.getTitle())
					//设置通知内容
					.setContentText(message.getContent())
					.setContentIntent(PendingIntent.getBroadcast(context,requestId++,clickIntent,PendingIntent.FLAG_UPDATE_CURRENT));
			//设置通知时间，默认为系统发出通知的时间，通常不用设置
			//.setWhen(System.currentTimeMillis());
			//通过builder.build()方法生成Notification对象,并发送通知,id=1
			Notification notify = myBuilder.build();
			notifyManager.notify(notificationId++, notify);
		}
	}

	private void goWebView(Context context ,XGPushTextMessage message,String url) {
		Intent mainIntent = new Intent(context, ShowWeb.class);
//						mainIntent.setAction(Intent.ACTION_MAIN);
//						mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mainIntent.putExtra("url", url);
		mainIntent.setAction(Intent.ACTION_VIEW);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
		Intent intent = new Intent(context,ClickNotificationReceiver.class);
		intent.putExtra("realIntent",mainIntent);
		PendingIntent mainPendingIntent = PendingIntent.getBroadcast(context, requestId++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//获取NotificationManager实例
		NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		//实例化NotificationCompat.Builde并设置相关属性
		NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context)
				//设置小图标
				.setSmallIcon(R.mipmap.ic_launcher)
				.setDefaults(Notification.DEFAULT_ALL)
				.setPriority(Notification.PRIORITY_MAX)
				.setShowWhen(true)
				//点击通知后自动清除
				.setAutoCancel(true)
				//设置通知标题
				.setContentTitle(message.getTitle())
				//设置通知内容
				.setContentText(message.getContent())
				.setContentIntent(mainPendingIntent);
		//设置通知时间，默认为系统发出通知的时间，通常不用设置
		//.setWhen(System.currentTimeMillis());
		//通过builder.build()方法生成Notification对象,并发送通知,id=1
		notifyManager.notify(notificationId++, myBuilder.build());




//		if (Tools.isAppInPhone(context, context.getPackageName()) < 2) { //未运行
//			//获取PendingIntent
//			Intent intent = new Intent(context,WelcomeActivity.class);
////						intent.setAction(Intent.ACTION_MAIN);
////						intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//			Intent mainIntent = new Intent(context, ShowWeb.class);
////						mainIntent.setAction(Intent.ACTION_MAIN);
////						mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//			mainIntent.putExtra("url", url);
//			Intent[] intents = new Intent[]{intent, mainIntent};
//			PendingIntent mainPendingIntent = PendingIntent.getActivities(context, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);
//			//获取NotificationManager实例
//			NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//			//实例化NotificationCompat.Builde并设置相关属性
//			NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context)
//					//设置小图标
//					.setSmallIcon(R.mipmap.ic_launcher)
//					.setDefaults(Notification.DEFAULT_SOUND)
//					.setShowWhen(true)
//					//点击通知后自动清除
//					.setAutoCancel(true)
//					//设置通知标题
//					.setContentTitle(message.getTitle())
//					//设置通知内容
//					.setContentText(message.getContent())
//					.setContentIntent(mainPendingIntent);
//			//设置通知时间，默认为系统发出通知的时间，通常不用设置
//			//.setWhen(System.currentTimeMillis());
//			//通过builder.build()方法生成Notification对象,并发送通知,id=1
//			notifyManager.notify(6, myBuilder.build());
//
//
//			return;
//
//		} else {
//			Intent mainIntent = new Intent(context, ShowWeb.class);
////						mainIntent.setAction(Intent.ACTION_MAIN);
////						mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//			mainIntent.putExtra("url", url);
////			Intent[] intents = new Intent[]{intent, mainIntent};
//			PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//			//获取NotificationManager实例
//			NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//			//实例化NotificationCompat.Builde并设置相关属性
//			NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(context)
//					//设置小图标
//					.setSmallIcon(R.mipmap.ic_launcher)
//					//点击通知后自动清除
//					.setAutoCancel(true)
//					//设置通知标题
//					.setContentTitle(message.getTitle())
//					//设置通知内容
//					.setContentText(message.getContent())
//					.setContentIntent(mainPendingIntent);
//			//设置通知时间，默认为系统发出通知的时间，通常不用设置
//			//.setWhen(System.currentTimeMillis());
//			//通过builder.build()方法生成Notification对象,并发送通知,id=1
//			notifyManager.notify(7, myBuilder.build());
//
//			return;
//		}


	}

}
