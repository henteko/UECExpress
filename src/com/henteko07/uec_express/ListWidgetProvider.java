package com.henteko07.uec_express;

import java.io.ByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ListWidgetProvider extends AppWidgetProvider {
	public static final String EXTRA_ITEM = "com.henteko07.uec_express.TOAST_ACTION";
	public static final String TOAST_ACTION = "com.henteko07.uec_express.EXTRA_ITEM";

	@Override
	public void onReceive(Context context, Intent intent) {
		// ListView の子ビューがタップされたときに発行されるブロードキャストを
		// 拾って、トーストを表示
		if (intent.getAction().equals(TOAST_ACTION)) {
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
			Toast.makeText(context, "Touched view" + viewIndex,
					Toast.LENGTH_SHORT).show();
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.listwidget_layout);

		// 1. ListView に紐付けるRemoteViewsService（ここではListWidgetService）
		// を指定するためのインテント
		Intent intent = new Intent(context, ListWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		// テスト用
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.permitAll().build());

		HttpClient httpClient = new DefaultHttpClient();
		StringBuilder uri = new StringBuilder("http://henteko07.com:6789");
		HttpGet request = new HttpGet(uri.toString());
		HttpResponse httpResponse = null;

		try {
			httpResponse = httpClient.execute(request);
		} catch (Exception e) {
			Log.d("JSONSampleActivity", "Error Execute : " + e);
		}

		int status = httpResponse.getStatusLine().getStatusCode();

		if (HttpStatus.SC_OK == status) {
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				httpResponse.getEntity().writeTo(outputStream);
				String json_s = outputStream.toString(); // JSONデータ
				intent.putExtra("json", json_s);

				JSONObject rootObject;
				try {
					rootObject = new JSONObject(json_s);
					String last_updated = rootObject.getString("updated");
					views.setTextViewText(R.id.last_updated, last_updated);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				Log.d("JSONSampleActivity", "Error");
			}
		} else {
			Log.d("JSONSampleActivity", "Status" + status);
		}

		// 2. 1 で作ったインテントをListView のID を指定してセット
		views.setRemoteAdapter(R.id.list_view, intent);
		// 3. ListView のデータが空のときに表示するビュー
		views.setEmptyView(R.id.list_view, R.id.empty_view);
		// 4. ListView の子ビューがタップされたときに発行するブロードキャストのひな型をセット
		Intent toastIntent = new Intent(context, ListWidgetProvider.class);
		toastIntent.setAction(ListWidgetProvider.TOAST_ACTION);
		PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context,
				0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setPendingIntentTemplate(R.id.list_view, toastPendingIntent);
		// アプリケーションウィジェットを更新
		appWidgetManager.updateAppWidget(appWidgetIds, views);
		
		// サービスの起動
        Intent button_intent = new Intent(context, ButtonWidgetService.class);
        context.startService(button_intent);
	}

}
