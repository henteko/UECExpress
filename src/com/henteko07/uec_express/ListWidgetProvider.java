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
	public static final String ACTION_CLICK = "com.henteko07.uec_express.ACTION_CLICK";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(TOAST_ACTION)) {
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
			Toast.makeText(context, "Touched view" + viewIndex,
					Toast.LENGTH_SHORT).show();
		}else if(ACTION_CLICK.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if(appWidgetId != 0) {
                AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view);
            }
        }
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		for(int appWidgetId : appWidgetIds) {
            Intent remoteViewsFactoryIntent = new Intent(context, ListWidgetService.class);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.listwidget_layout);
            rv.setRemoteAdapter(R.id.list_view, remoteViewsFactoryIntent);
    		rv.setEmptyView(R.id.list_view, R.id.empty_view);
            setOnButtonClickPendingIntent(context, rv, appWidgetId);
            
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
	}
	
	private void setOnButtonClickPendingIntent(Context ctx, RemoteViews rv, int appWidgetId) {
        Intent btnClickIntent = new Intent(ACTION_CLICK);
        btnClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent btnClickPendingIntent = PendingIntent.getBroadcast(
            ctx,
            0,
            btnClickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

        rv.setOnClickPendingIntent(R.id.update, btnClickPendingIntent);
    }

}
