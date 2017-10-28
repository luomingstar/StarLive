package com.a99live.zhibo.live.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.a99live.zhibo.live.activity.WelcomeActivity;
import com.a99live.zhibo.live.utils.Tools;

/**
 * Created by JJGCW on 2017/3/3.
 */

public class ClickNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        // Toast.makeText(context, "通知被点击了", Toast.LENGTH_SHORT).show();
        Intent realIntent = intent.getParcelableExtra("realIntent");
        if (realIntent != null) {
            //未运行
            if (Tools.isAppInPhone(context,context.getPackageName())<2){
                Intent intent1 = new Intent(context, WelcomeActivity.class);
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivities(new Intent[]{intent1,realIntent});
            }else{
//                Bundle bundle = realIntent.getExtras();
//                if (bundle.containsKey("url")){//h5
//
//                }else{//进直播
//                    String eventContent = "你关注的主播正在直播";
//                    if (bundle.containsKey("eventcontent")){
//                        eventContent = bundle.getString("eventcontent");
//                    }
//                    String room_id = bundle.getString("room_id");
//                    String ucode = bundle.getString("ucode");
//
//                    EventBus.getDefault().post(new ShowLiveDialogEvent(room_id, ucode, eventContent));
//                    return;
//                }
                context.startActivity(realIntent);
            }
        }else{
            //未运行
            if (Tools.isAppInPhone(context,context.getPackageName())<2){
                Intent intent1 = new Intent(context, WelcomeActivity.class);
                intent1.setAction(Intent.ACTION_VIEW);
                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                Intent intent2 = new Intent(context, SearchActivity.class);
//                intent2.setAction(Intent.ACTION_VIEW);
//                intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }else{
//                Intent intent1 = new Intent(context, SearchActivity.class);
//                intent1.setAction(Intent.ACTION_VIEW);
//                intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent1);
            }
        }
    }
}
