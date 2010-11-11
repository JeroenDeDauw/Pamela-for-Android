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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class MacList extends ListActivity {
	
	protected String url;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       Intent intent = getIntent();
       Bundle extras = intent.getExtras();
       this.url = extras.getString("url");
       
       this.showList();
    }
    
    protected void showList() {
    	setListAdapter(new ArrayAdapter<String>(this, R.layout.macaddress, this.getMacs()));
    }
    
    protected List<String> getMacs() {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
    	
        String json = "";
        
        try {
			HttpResponse response = httpClient.execute(new HttpGet(new URI(this.url)));
			json = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ArrayList<String> macs = new ArrayList<String>();
		
		int i = 0;
		
		for(String part : json.split("\""))
		{
			i++;
			
			// These will be the macs.
			if ( i % 2 == 0 ) {
				macs.add(part);
			}
		}
		
		return (List<String>)macs;
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
        case R.id.refreshButton:
        	this.showList();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }   
    
}