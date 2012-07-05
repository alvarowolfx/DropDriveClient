package br.agiratec.dropdrive.client.util;

import javax.swing.SwingUtilities;

import br.agiratec.dropdrive.client.frame.LoginView;
import br.agiratec.dropdrive.client.socket.SocketReceiver;

public class Launcher {
	
	public Launcher(){
		SwingUtilities.invokeLater(new Runnable() {			
			public void run() {
				new LoginView();				
			}
		});		
		new SocketReceiver();		
	}
	
	public static void main(String[] args) {
		new Launcher();
	}
	

}
