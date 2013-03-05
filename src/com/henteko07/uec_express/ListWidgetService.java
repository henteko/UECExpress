package com.henteko07.uec_express;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {
	public class WidgetItem {
		String mText;

		public WidgetItem(String text) {
			mText = text;
		}
	}

	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		List<WidgetItem> data = new ArrayList<WidgetItem>();
		
		String json_s = intent.getStringExtra("json");
		
		JSONObject rootObject;
		try {
			rootObject = new JSONObject(json_s);
			JSONArray eventArray = rootObject.getJSONArray("express");
	        
	        for (int i = 0; i < eventArray.length(); i++) {
	            JSONObject jsonObject = eventArray.getJSONObject(i);
	            Log.d("JSONSampleActivity", jsonObject.getString("class"));
	            String express_s = jsonObject.getString("class") + " : " + 
	            					jsonObject.getString("data") + " : " + 
	            					jsonObject.getString("time") + "時限目\n" + 
	            					jsonObject.getString("subject") + "\n" + 
	            					jsonObject.getString("staff");
	            data.add(new WidgetItem(express_s));
	        }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return new ListRemoteViewFactory(this.getApplicationContext(), intent,
				data);
	}
	

	class ListRemoteViewFactory implements
			RemoteViewsService.RemoteViewsFactory {
		private List<WidgetItem> mData = new ArrayList<WidgetItem>();
		private Context mContext;

		public ListRemoteViewFactory(Context context, Intent intent,
				List<WidgetItem> data) {
			mContext = context;
			mData = data;
		}

		public void onCreate() {
		}

		public void onDestroy() {
			mData.clear();
		}

		public void onDataSetChanged() {
		}

		public int getCount() {
			return mData.size();
		}

		public long getItemId(int position) {
			return position;
		}

		public RemoteViews getLoadingView() {
			// 読み込み中用のView を返す
			return null;
		}

		public RemoteViews getViewAt(int position) {
			// ListView の子ビューに表示するRemoteViews を取得
			RemoteViews views = new RemoteViews(mContext.getPackageName(),
					R.layout.listwidget_item);
			// RemoteViews 内のTextView に表示する文字を指定
			views.setTextViewText(R.id.widget_item, mData.get(position).mText);
			// タップされたアイテムを識別するための情報を持つインテントを作成
			Intent fillInIntent = new Intent();
			fillInIntent.putExtra(ListWidgetProvider.EXTRA_ITEM, position);
			// RemoteViews がタップされたときに
			// PendingItentTemplate + 指定されたIntent の情報
			// を持ったIntent を発行
			views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
			return views;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public boolean hasStableIds() {
			return true;
		}

	}

}
