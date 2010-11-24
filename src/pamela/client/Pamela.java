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

import java.util.ArrayList;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Pamela extends AwesomeTabActivity {

	protected static int tabId = 0;
	
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
	    intent.setClass(this, MacListActivity.class);
	    
	    intent.putExtra("url", url);
	    intent.putExtra("id", tabId);
	    
	    tabId++;
	    
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    TabHost.TabSpec spec = tabHost.newTabSpec(name)
	    	.setIndicator(name)
	    	.setContent(intent);
	    
	    super.addTabSpec(spec);
	}
	
}