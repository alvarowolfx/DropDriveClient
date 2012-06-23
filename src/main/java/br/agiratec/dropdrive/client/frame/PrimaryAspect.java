package br.agiratec.dropdrive.client.frame;

import java.awt.Container;

import org.jdesktop.swingx.JXFrame;

public class PrimaryAspect extends JXFrame{
	private Container ct;
	
	public PrimaryAspect(){
		createGui();
			
	}
	
	public void createGui(){
		ct = this.getContentPane();
		
		
		
		
		
		ct.setVisible(true);
	}
}
