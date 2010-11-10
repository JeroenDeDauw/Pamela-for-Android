package pamela.client;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PamelaAdapter extends ArrayAdapter<PamelaItem> {

	int resource;
	String response;
	Context context;
	
	//Initialize adapter
	public PamelaAdapter(Context context, int resource, List<PamelaItem> items) {
		super(context, resource, items);
		this.resource=resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout itemView;
		// Get the current PamelaItem.
		PamelaItem item = getItem(position);

		// Inflate the view.
		if(convertView==null)
		{
			itemView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, itemView, true);
		}
		else
		{
			itemView = (LinearLayout) convertView;
		}
		
		TextView itemText =(TextView)itemView.findViewById(R.id.txtItemText);

		//Assign the appropriate data from our alert object above.
		itemText.setText(item.toString());

		itemView.addView(itemText);
		
		return itemView;
	}
	
}
