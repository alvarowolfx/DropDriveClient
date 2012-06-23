package br.agiratec.dropdrive.client.util;

import br.agiratec.dropdrive.client.frame.LoginView;

public class Launcher {
	
	public static UserPreferences preferences;

	public static UserPreferences getPreferences(){
		return preferences;
	}
	
	public Launcher(){
		preferences= new UserPreferences();
		new LoginView();
	}
	
	public static void main(String[] args) {
		new Launcher();
	}
}
