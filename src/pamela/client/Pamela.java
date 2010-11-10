package pamela.client;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Pamela extends ListActivity {
	
	// ListView that will hold our items references back to main.xml.
	ListView lstTest;
	
	// Array Adapter that will hold our ArrayList and display the items on the ListView.
	PamelaAdapter arrayAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.showList();
    }
    
    protected void showList() {
        setListAdapter(new ArrayAdapter<String>(this, R.layout.main, this.getListItems()));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
                Toast.LENGTH_SHORT).show();
          }
        });    	
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
    
    protected String[] getListItems() {
    	return new String[] { Double.toString(Math.random()), Double.toString(Math.random()), Double.toString(Math.random()) };
    }
    
}