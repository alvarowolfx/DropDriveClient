package br.agiratec.dropdrive.client.util;

import java.net.InetAddress;

import br.agiratec.dropdrive.client.frame.LoginView;

public class Launcher {
	
	private static UserPreferences preferences;

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
	
	public static String getComputerName(){
		String nomeComputador="";
		try{  
		      nomeComputador=InetAddress.getLocalHost().getHostName();  
		    }catch (Exception e){  
		      e.printStackTrace();  
		    }  
		return nomeComputador;
	}
}
