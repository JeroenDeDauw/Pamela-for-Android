/*
	Copyright 2010 by Jeroen De Dauw

    This file is part of Pamela for Android.

    Pamela for Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    It is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this code.  If not, see <http://www.gnu.org/licenses/>.
*/

package pamela.client.widgets;

import java.util.ArrayList;

import pamela.client.R;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PamelaWidgetConfigure extends Activity {

    private static final String PREFS_NAME = "pamela.client.widgets.PamelaWidgetProvider";
    private static final String PREF_PREFIX_KEY = "pamela_";
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	private EditText mPamelaUrl;

	
    public PamelaWidgetConfigure() {
        super();
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
       super.onCreate(icicle);
       
       // Set the result to CANCELED.  This will cause the widget host to cancel
       // out of the widget placement if they press the back button.
       setResult(RESULT_CANCELED);

       // Set the view layout resource to use.
       setContentView(R.layout.appwidget_configure);
       
       // Find the EditText
       mPamelaUrl = (EditText)findViewById(R.id.txtUrl);
       
       // Bind the action for the save button.
       findViewById(R.id.btnSave).setOnClickListener(mOnClickListener);
       
       Intent intent = getIntent();
       Bundle extras = intent.getExtras();
       
       if (extras != null) {
    	   mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
       }
       
       // If they gave us an intent without the widget id, just bail.
       if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
           finish();
       }
    }	
	
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
    	public void onClick(View v) {
    		final Context context = PamelaWidgetConfigure.this;

            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.
            String url = mPamelaUrl.getText().toString();
            saveUrlPref(context, mAppWidgetId, url);
    		
            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            PamelaWidgetProvider.updateAppWidget(context, appWidgetManager,mAppWidgetId, url);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
    	}
    };
    

    // Write the url to the SharedPreferences object for this widget
    static void saveUrlPref(Context context, int appWidgetId, String url) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, url);
        prefs.commit();
    }

    // Read the url from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadUrlPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String url = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (url != null) {
            return url;
        } else {
            return context.getString(R.string.whitespaceurl);
        }
    }
    
    static void deleteTitlePref(Context context, int appWidgetId) {
    }

    static void loadAllTitlePrefs(Context context, ArrayList<Integer> appWidgetIds,
            ArrayList<String> texts) {
    }

    
}
