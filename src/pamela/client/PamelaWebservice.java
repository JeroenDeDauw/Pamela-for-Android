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

public class PamelaWebservice {

	protected String url;
	
	public PamelaWebservice( String url ) {
		this.url = url;
	}
	
    protected List<String> getMacs() {
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
