package br.agiratec.dropdrive.client.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.model.SharedFile;


public class DropDriveServiceOrchestrator implements Runnable{
			
	private static DropDriveServiceOrchestrator instance = null;
	private static HeartBeatService heartbeat = null;
	private static FilePublisherService publisher = null;
	private static SocketReceiver receiver = null ;
	private static DownloadService downloader = null;
	
	private static Future heartbeatFuture = null;
	private static Future publisherFuture = null;
	private static Future receiverFuture = null;
	private static Future downloaderFuture = null;
	
	private static Logger log = Logger.getLogger(DropDriveServiceOrchestrator.class);
	private static ExecutorService executor = null;	
	private static boolean firstTime = true;
	
	static {

		log.info("Orquestrador iniciando");
		instance = new DropDriveServiceOrchestrator();		
	}
	
	private DropDriveServiceOrchestrator() {
		

		log.info("Heartbeat iniciando");
		heartbeat = new HeartBeatService();

		log.info("FilePublisher iniciando");
		publisher = new FilePublisherService();
				

		log.info("Downloader iniciando");
		downloader = new DownloadService();
		
		log.info("Receiver iniciando");
		receiver = new SocketReceiver();	
		
		log.info("Executor iniciando");
		
		executor = Executors.newCachedThreadPool();
		
	}

	public void initServices(){
		
		if(firstTime){
			
			heartbeatFuture = executor.submit(heartbeat);
			publisherFuture = executor.submit(publisher);
			receiverFuture = executor.submit(receiver);
			downloaderFuture = executor.submit(downloader);
			
			executor.submit(this);
			
			//Impedir a execução de novas threads
			firstTime = false;
		}
		
	}
	
	public void addFileToQueue(SharedFile sf){
		downloader.addFile(sf);
	}
	
	public static DropDriveServiceOrchestrator getInstance() {		
		return instance;
	}

	public void run() {
		
		while(true){
			
			if(downloaderFuture.isCancelled() || downloaderFuture.isDone()){			
				log.info("Levantando denovo o Downloader");
				downloader = new DownloadService();
				downloaderFuture = executor.submit(downloader);
			}
			if(publisherFuture.isCancelled()){
				log.info("Levantando denovo o Publisher");
				publisher = new FilePublisherService();
				publisherFuture = executor.submit(publisher);
			}
			if(receiverFuture.isCancelled()){
				log.info("Levantando denovo o Receiver");
				receiver = new SocketReceiver();
				receiverFuture = executor.submit(receiver);			
			}
			if(heartbeatFuture.isCancelled()){
				log.info("Levantando denovo o Hearbeat Service");
				heartbeat = new HeartBeatService();
				heartbeatFuture = executor.submit(heartbeat);
			}			
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
		}
		
	}
	
	


}
