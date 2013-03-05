package com.henteko07.uec_express;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ButtonWidgetService extends Service {
	private final String BUTTON_CLICK_ACTION = "BUTTON_CLICK_ACTION";

	@Override
	public void onStart(Intent intent, int startId) {

		// ボタンが押された時に発行されるインテントを準備する
		Intent buttonIntent = new Intent();
		buttonIntent.setAction(BUTTON_CLICK_ACTION);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				buttonIntent, 0);
		RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.listwidget_layout);
		remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
		// ボタンが押された時に発行されたインテントの場合は文字を変更する
		if (BUTTON_CLICK_ACTION.equals(intent.getAction())) {
			//更新ボタンが押されたので、強制的にonUpdate()を呼び出す
			Context context = this.getApplicationContext();
			AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
			ComponentName widgetComponent = new ComponentName(context,ListWidgetProvider.class);
			int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
			ListWidgetProvider listwidgetp = new ListWidgetProvider();
			listwidgetp.onUpdate(context, widgetManager, widgetIds);
			
			Toast.makeText(this, "更新を確認しました", Toast.LENGTH_SHORT).show();
		}

		// AppWidgetの画面更新
		ComponentName thisWidget = new ComponentName(this,
				ListWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, remoteViews);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
