package br.agiratec.dropdrive.client.application;


public class CommunicatorFactory {
	private static DropDriveServiceClient communicator;
	
	private CommunicatorFactory(){
		
	}
	
	public static DropDriveServiceClient getCommunicator(){
		if(communicator == null){
			communicator = new DropDriveServiceClient();
		}
		return communicator;
	}
	
	
}
