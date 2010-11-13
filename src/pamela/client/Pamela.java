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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

public class Pamela extends TabActivity {

	protected ArrayList<ListView> macLists = new ArrayList<ListView>();
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    addTab( "0x20", "http://www.0x20.be/pam/macs" );
	    addTab( "hsbxl", "http://hackerspace.be/pam/macs" );
	}
	
	protected void addTab( String name, String url ) {
		addTab( name, url, R.drawable.icon );
	}
	
	protected void addTab( String name, String url, int icon ) {
	    TabHost tabHost = getTabHost();  // The activity TabHost

	    Intent intent = new Intent();
	    intent.setClass(this, MacList.class);
	    intent.putExtra("url", url);
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    TabHost.TabSpec spec = tabHost.newTabSpec(name)
	    	.setIndicator(name)
	    	.setContent(intent);
	    
	    tabHost.addTab(spec);
	    
	    // Well, this is definitely one way to get to the tab height.. :D
	    tabHost.getTabWidget().getChildAt(tabHost.getTabWidget().getChildCount() - 1).getLayoutParams().height = 35;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.btnRefresh:
	        	Pamela.this.refreshList( (ListView)getTabHost().getTabContentView().getFocusedChild() );
	        	return true;
	        case R.id.btnSearch:
	        	this.onSearchRequested();
	        	return true;
	        case R.id.btnAdd:
	        	
	        	return true;	        	
	        case R.id.btnEdit:
	        	
	        	return true;
	        case R.id.btnRemove:
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage(R.string.confirmremove)
	        	       .setCancelable(false)
	        	       .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   //getTabHost().getTabContentView().removeView(getTabHost().getTabContentView().getFocusedChild());
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
 	
    protected void showList( ListView macList ) {
    	macList.setAdapter(new ArrayAdapter<String>(this, R.layout.macaddress, this.getMacs()));
    }
    
    protected void refreshList( ListView macList ) {
    	refreshList( macList, true );
    }
    
    protected void refreshList( ListView macList, boolean notify ) {
    	showList( macList );
    	
    	if ( notify ) {
    		Toast.makeText(this, R.string.listrefreshed, Toast.LENGTH_SHORT).show();
    	}
    }
    
    protected List<String> getMacs( String url ) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
    	
        String json = "";
        
        try {
			HttpResponse response = httpClient.execute(new HttpGet(new URI(url)));
			json = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ArrayList<String> macs = new ArrayList<String>();
		
		int i = 0;
		
		for(String part : json.split("\"")) {
			i++;
			
			// These will be the macs.
			// Assuming no lamefag put's an " in his name :)
			if ( i % 2 == 0 ) {
				macs.add(part);
			}
		}
		
		return (List<String>)macs;
    }
    
}