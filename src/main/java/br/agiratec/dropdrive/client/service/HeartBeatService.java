package br.agiratec.dropdrive.client.service;




import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.application.ClientCommunicator;

import com.sun.jersey.api.client.ClientResponse;

public class HeartBeatService implements Runnable{

	//A cada 1 minuto ele sinaliza ao servidor
	private final long HEARTBEAT_TIME_MS = 60000;
	private ClientCommunicator client = new ClientCommunicator();
	private Logger log = Logger.getLogger(HeartBeatService.class);
			
	public void run() {		
		while(true){
						
			try {
				ClientResponse response = client.heartbeat();
				if(response != null && response.getStatus() == 200){
					log.info("Usu‡rio publicado  com sucesso !! ");
				}
				Thread.sleep(HEARTBEAT_TIME_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
