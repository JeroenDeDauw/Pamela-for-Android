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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MacListActivity extends ListActivity {
	
	protected PamelaWebservice pamela;
	protected int tabId;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       
       Intent intent = getIntent();
       
       if (intent.getData() == null) {
           intent.setData(PamelaColumns.CONTENT_URI);
       }
       
       Bundle extras = intent.getExtras();
       
       this.pamela = new PamelaWebservice( extras.getString("url") );
       this.tabId = extras.getInt("id");
       
       this.showList();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }     
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.btnRefresh:
	        	this.refreshList();
	        	return true;
	        case R.id.btnSearch:
	        	// TODO
	        	showNotImplemented();
	        	//this.onSearchRequested();
	        	return true;
	        case R.id.btnAdd:
	        	// TODO
	        	startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
	        	//showNotImplemented();
	        	return true;	        	
	        case R.id.btnEdit:
	        	startActivityForResult(new Intent(Intent.ACTION_EDIT), tabId);
	        	// TODO
	        	//showNotImplemented();
	        	return true;
	        case R.id.btnRemove:
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage(R.string.confirmremove)
	        	       .setCancelable(false)
	        	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   ((Pamela)getParent()).removeCurrentTab(); 
	        	           }
	        	       })
	        	       .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	           }
	        	       });
	        	AlertDialog alert = builder.create();
	        	alert.show();
	        	return true;	        	
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
    
    protected void showNotImplemented() {
    	Toast.makeText(this, R.string.notImplementedYet, Toast.LENGTH_SHORT).show();
    }
    
    protected void showList() {
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.macaddress, this.pamela.getMacs()));
    }
    
    protected void refreshList() {
    	refreshList( true );
    }
    
    protected void refreshList( boolean notify ) {
    	showList();
    	
    	if ( notify ) {
    		Toast.makeText(this, R.string.listrefreshed, Toast.LENGTH_SHORT).show();
    	}
    }
    
}