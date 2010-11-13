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
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

public class Pamela extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    
	    addTab( "0x20", "http://www.0x20.be/pam/macs" );
	    addTab( "hsbxl", "http://hackerspace.be/pam/macs" );
	}
	
	protected void addTab( String name, String url ) 
	{
		addTab( name, url, R.drawable.icon );
	}
	
	protected void addTab( String name, String url, int icon )
	{
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
	        	//this.refreshList();
	        	Toast.makeText(this, R.string.listrefreshed, Toast.LENGTH_SHORT).show();
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
 	
	
}