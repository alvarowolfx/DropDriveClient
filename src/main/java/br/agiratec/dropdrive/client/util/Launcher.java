package br.agiratec.dropdrive.client.util;

import br.agiratec.dropdrive.client.frame.LoginView;
import br.agiratec.dropdrive.client.socket.SocketReceiver;

public class Launcher {
	
	public Launcher(){
		new LoginView();
		new SocketReceiver();		
	}
	
	public static void main(String[] args) {
		new Launcher();
	}
	

}
