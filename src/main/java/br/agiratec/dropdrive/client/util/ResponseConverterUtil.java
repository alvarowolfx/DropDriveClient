package br.agiratec.dropdrive.client.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.agiratec.dropdrive.client.model.SharedFile;

import com.sun.jersey.api.client.ClientResponse;

public class ResponseConverterUtil {

	public static List<SharedFile> jsonSearchResponseToList(ClientResponse response){
		String jsonString = null;		
		List<SharedFile> files = null;	
		try {
			jsonString = response.getEntity(String.class);
			JSONObject json = new JSONObject(jsonString);
			
			
			JSONObject jsonResponse = json.getJSONObject("response");
			JSONArray jsonFiles = jsonResponse.getJSONArray("files");			
			files =  new ArrayList<SharedFile>();
			int len = jsonFiles.length();
			for (int i = 0; i < len; i++) {
				SharedFile sf = new SharedFile();
				
				JSONObject jsonFile = jsonFiles.getJSONObject(i);
				sf.setMd5Hash(jsonFile.getString("md5"));
				sf.setPath(jsonFile.getJSONArray("filenames").join(",").replaceAll("\"", ""));
				sf.setSize(jsonFile.getLong("size"));
				if(sf.getSize() % (1024l*1024l) > 0)
					sf.setNumberOfParts(1l+sf.getSize()/(1024l*1024l));
				else
					sf.setNumberOfParts(sf.getSize()/(1024l*1024l));
				
				int usersOnlineLen = jsonFile.getJSONArray("onlineUsers").length();
				for (int j = 0; j < usersOnlineLen; j++) {					
					sf.getUsersOnline().add(jsonFile.getJSONArray("onlineUsers").getString(j));					
				}	
				
				int haveFileIncompleteLen = jsonFile.getJSONArray("haveFileIncomplete").length();
				for (int j = 0; j < haveFileIncompleteLen; j++) {					
					sf.getHaveFileIncomplete().add(jsonFile.getJSONArray("haveFileIncomplete").getString(j));					
				}	
				
				int haveFileCompleteLen = jsonFile.getJSONArray("haveFileComplete").length();
				for (int j = 0; j < haveFileCompleteLen; j++) {					
					sf.getHaveFileComplete().add(jsonFile.getJSONArray("haveFileComplete").getString(j));					
				}	
				
				files.add(sf);
			}
													
		} catch (JSONException e) {			
			e.printStackTrace();
			return null;
		}
		
		
		
		return files;
	}
}
