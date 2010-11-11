package pamela.client;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

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
	}
	
}