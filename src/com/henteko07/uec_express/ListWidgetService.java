package com.henteko07.uec_express;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
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
		return new ListWidgetFactory();
	}
	
	private class ListWidgetFactory implements RemoteViewsFactory {

        private static final String TAG = "SampleViewFactory";

        private JSONArray jsons = new JSONArray();
        private String lastUpdate = "";

        public void onCreate() {
            Log.v(TAG, "[onCreate]");
        }

        public void onDataSetChanged() {
            Log.v(TAG, "[onDataSetChanged]");

            fetchUpdate();
        }

        public void onDestroy() {
            Log.v(TAG, "[onDestroy]");
        }

        public RemoteViews getViewAt(int position) {
            Log.v(TAG, "[getViewAt]: " + position);

            if(jsons.length() <= 0) {
                return null;
            }

            RemoteViews rv = null;

            try {
                JSONObject json = jsons.getJSONObject(position);

                if(json != null) {
                	String express_s = json.getString("class") + " : " + 
        					json.getString("data") + " : " + 
        					json.getString("time") + "時限目\n" + 
        					json.getString("subject") + "\n" + 
        					json.getString("staff");
                    
                    rv = new RemoteViews(getPackageName(), R.layout.listwidget_item);
                    rv.setTextViewText(R.id.widget_item, express_s);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }

            return rv;
        }

        public long getItemId(int position) {
            Log.v(TAG, "[getItemId]: " + position);

            return position;
        }

        public int getCount() {
            Log.v(TAG, "[getCount]");

            return jsons.length();
        }

        public RemoteViews getLoadingView() {
            Log.v(TAG, "[getLoadingView]");

            return null;
        }


        public int getViewTypeCount() {
            Log.v(TAG, "[getViewTypeCount]");

            return 1;
        }

        public boolean hasStableIds() {
            Log.v(TAG, "[hasStableIds]");

            return true;
        }

        private void fetchUpdate() {
        	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			.permitAll().build());

        	HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
			try {
				response = httpClient.execute(
				    new HttpGet("http://henteko07.com:6789")
				);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            if(response.getStatusLine().getStatusCode() != 200) {
                return;
            }

            try {
            	String json_s = EntityUtils.toString(response.getEntity());
            	JSONObject json = new JSONObject(json_s);
            	lastUpdate = json.getString("updated");
            	jsons = new JSONArray(json.getString("express"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
        	httpClient.getConnectionManager().shutdown();
        }
    }

}
