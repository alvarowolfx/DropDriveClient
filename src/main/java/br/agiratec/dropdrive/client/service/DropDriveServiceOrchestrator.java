package br.agiratec.dropdrive.client.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	
	private static ExecutorService executor = null;	
	private static boolean firstTime = true;
	
	static {
		instance = new DropDriveServiceOrchestrator();		
	}
	
	private DropDriveServiceOrchestrator() {
		heartbeat = new HeartBeatService();
		publisher = new FilePublisherService();
		receiver = new SocketReceiver();	
		downloader = new DownloadService();
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
			
			if(downloaderFuture.isCancelled()){
				downloaderFuture = executor.submit(downloader);
			}
			if(publisherFuture.isCancelled()){
				publisherFuture = executor.submit(publisher);
			}
			if(receiverFuture.isCancelled()){
				receiverFuture = executor.submit(receiver);			
			}
			if(heartbeatFuture.isCancelled()){
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
