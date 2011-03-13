package pamela.client2;

import java.util.ArrayList;

import android.app.TabActivity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class AwesomeTabActivity extends TabActivity {

	protected ArrayList<TabHost.TabSpec> tabs = new ArrayList<TabHost.TabSpec>();	
	
	public void removeCurrentTab() {
		TabHost tabHost = getTabHost();
		
		int curr = tabHost.getCurrentTab();
		//tabHost.getCurrentTabView().setVisibility(View.GONE);
		/*
		tabs.remove(curr);
		tabHost.clearAllTabs();
		tabHost.setCurrentTab(0);
		
		for( TabHost.TabSpec spec : tabs ) {
			tabHost.addTab(spec);
		}*/
	}
	
	protected void addTabSpec( TabSpec tabSpec ) {
		TabHost tabHost = getTabHost();
		
		tabs.add(tabSpec);
		tabHost.addTab(tabSpec);
		
		tabHost.getTabWidget().getChildAt(tabHost.getTabWidget().getChildCount() - 1).getLayoutParams().height = 35;
	}
	
}
