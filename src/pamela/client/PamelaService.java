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

package pamela.client;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PamelaService extends Activity {

	private static final String TAG = "PamelaService";
	
    // This is our state data that is stored when freezing.
	private static final String ORIGINAL_NAME = "O_o";
	private static final String ORIGINAL_URL = "http://www.";
	
    // The different distinct states the activity can be run in.
    protected static final int STATE_EDIT = 0;
    protected static final int STATE_INSERT = 1;
	
    protected int state;
    private Uri uri;
    
    protected String originalName;
    protected String originalUrl;
    
    protected EditText txtName;
    protected EditText txtUrl;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity.
        setContentView(R.layout.pamelaservice);

        // The text vies for the pamela service, identified by its ID in the XML file.
        txtName = (EditText) findViewById(R.id.txtName);
        txtUrl = (EditText) findViewById(R.id.txtUrl);        
        
        final Intent intent = getIntent();

        // Do some setup based on the action being performed.
        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            // Requested to edit: set that state, and the data being edited.
        	state = STATE_EDIT;
            uri = intent.getData();
        } else if (Intent.ACTION_INSERT.equals(action)) {
        	// FIXME: not working...
        	txtName.setText(R.string.default_service_name);
        	txtUrl.setText(R.string.default_service_url);
        	
            // Requested to insert: set that state, and create a new entry
            // in the container.
        	state = STATE_INSERT;
        	uri = getContentResolver().insert(intent.getData(), null);

            // If we were unable to create a new note, then just finish
            // this activity.  A RESULT_CANCELED will be sent back to the
            // original activity if they requested a result.
            if (uri == null) {
                Log.e(TAG, "Failed to insert new note into " + getIntent().getData());
                finish();
                return;
            }

            // The new entry was created, so assume all will end well and
            // set the result to be returned.
            setResult(RESULT_OK, (new Intent()).setAction(uri.toString()));

        } else {
            // Whoops, unknown action!  Bail.
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        // If an instance of this activity had previously stopped, we can
        // get the original text it started with.
        if (savedInstanceState != null) {
        	originalName = savedInstanceState.getString(ORIGINAL_NAME);
        	originalUrl = savedInstanceState.getString(ORIGINAL_URL);
        }
        
        Button button = (Button)findViewById(R.id.btnSave);
        button.setOnClickListener(onSaveButtonClicked);
    }
    
	// Create an anonymous implementation of OnClickListener
	private OnClickListener onSaveButtonClicked = new OnClickListener() {
	    public void onClick(View v) {
	    	saveService();
	    }
	};
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
    	outState.putString(ORIGINAL_NAME, originalName);
    	outState.putString(ORIGINAL_URL, originalUrl);
    }
    
    
    private  final void saveService() {
        // Get out updates into the provider.
        ContentValues values = new ContentValues();

        // Bump the modification time to now.
        values.put(PamelaColumns.MODIFIED_DATE, System.currentTimeMillis());

        String name = txtName.getText().toString().trim();
        String url = txtUrl.getText().toString().trim();
        
        if (name.length() == 0) {
            Toast.makeText(this, R.string.name_cannot_be_mpty, Toast.LENGTH_SHORT).show();
            return;
        }
        
        // TODO: have some fancy regex match here
        if (url.length() == 0) {
            Toast.makeText(this, R.string.url_must_be_valid, Toast.LENGTH_SHORT).show();
            return;
        }        
        
        // Set creation time when inserting a new service.
        if (state == STATE_INSERT) {
            values.put(PamelaColumns.CREATED_DATE, System.currentTimeMillis());
        }

        // Write our text back into the provider.
        values.put(PamelaColumns.NAME, name);
        values.put(PamelaColumns.URL, url);

        // Commit all of our changes to persistent storage. When the update completes
        // the content provider will notify the cursor of the change, which will
        // cause the UI to be updated.
        try {
            getContentResolver().update(uri, values, null, null);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }
	
    private final void cancelService() {
        if (state == STATE_EDIT) {
            // Put the original info back into the database.
            ContentValues values = new ContentValues();
            values.put(PamelaColumns.NAME, originalName);
            values.put(PamelaColumns.URL, originalUrl);
            
            getContentResolver().update(uri, values, null, null);
        } else if (state == STATE_INSERT) {
            // We inserted a new item, make sure to delete it.
        	deleteService();
        }
    	
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private final void deleteService() {
    	getContentResolver().delete(uri, null, null);
    }
}
