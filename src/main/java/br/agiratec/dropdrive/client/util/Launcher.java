package br.agiratec.dropdrive.client.util;

import javax.swing.SwingUtilities;

import br.agiratec.dropdrive.client.frame.LoginView;
import br.agiratec.dropdrive.client.frame.PrimaryAspect;
import br.agiratec.dropdrive.client.service.DropDriveServiceOrchestrator;
import br.agiratec.dropdrive.client.service.SocketReceiver;

public class Launcher {
	
	public Launcher(){
		SwingUtilities.invokeLater(new Runnable() {			
			public void run() {
				new PrimaryAspect();				
			}
		});		
		
		DropDriveServiceOrchestrator.getInstance().initServices();
	}
	
	public static void main(String[] args) {
		new Launcher();
	}
	

}
