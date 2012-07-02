package br.agiratec.dropdrive.client.application;


public class CommunicatorFactory {
	private static ClientCommunicator communicator;
	
	private CommunicatorFactory(){
		
	}
	
	public static ClientCommunicator getCommunicator(){
		if(communicator == null){
			communicator = new ClientCommunicator();
		}
		return communicator;
	}
	
	
}
