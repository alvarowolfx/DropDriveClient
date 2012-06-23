package br.agiratec.dropdrive.client.util;

import java.util.prefs.Preferences;


public class UserPreferences {
	private final static String HOSTNAME = "hostname";
	private final static String USER = "user";
	private final static String PASSWORD = "userPassword";
	private final static String MY_USER = "myUser";
	private final static String MY_PASSWORD = "myPassword";
	
	private final static String USER_NODE = "userPreferences";
	
	
	private static Preferences preferences = null;
	
	static{
		preferences = Preferences.userRoot();
		preferences.node(USER_NODE);
	}
	
	public UserPreferences(){
		
	}
	
	public void setPrefHostname(String value){
		preferences.put(HOSTNAME, value);
	}
	
	public void setPrefMyUser(String value){
		preferences.put(MY_USER, value);
	}
	
	public void setPrefMyPassword(String value){
		preferences.put(MY_PASSWORD, value);
	}
	
	public void setPrefUser(String value){
		preferences.put(USER, value);
	}
	
	public void setPrefPassword(String value){
		preferences.put(PASSWORD, value);
	}
	
	public String getPrefHostname(){
		return preferences.get(HOSTNAME, "http://dropdrive.agiratec.com.br");
	}
	
	public String getPrefUser(){
		return preferences.get(USER, "postgres");
	}
	
	public String getPrefMyUser(){
		return preferences.get(MY_USER, "preencha :)");
	}
	
	public String getPrefMyPassword(){
		return preferences.get(MY_PASSWORD, "");
	}
	
	public String getPrefPassword(){
		return preferences.get(PASSWORD, "");
	}
	
}
