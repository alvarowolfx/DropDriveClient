package br.agiratec.dropdrive.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.application.DropDriveServiceClient;
import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.model.SharedFile;

public class FilePublisherService implements Runnable{

	private DropDriveServiceClient client = new DropDriveServiceClient();
	private Map<String,Boolean> publishedFiles = new HashMap<String,Boolean>();
	private Logger log = Logger.getLogger(FilePublisherService.class);
		
	public FilePublisherService() {
		iniciarArquivosPublicados();
	}
	
	private void iniciarArquivosPublicados() {
		
		List<SharedFile> files = DropDriveFS.getInstance().getFilesInDirectoryForPublish();
		
		for(SharedFile sf : files){
			
			client.publish(sf.getPath(), sf.getMd5Hash(), sf.getSize(), sf.getComplete());			
			publishedFiles.put(sf.getPath(),true);
			
		}
		
	}

	private void verificarMudancas() {
		
		long init = System.currentTimeMillis();
		Set<SharedFile> files = new HashSet<SharedFile>(DropDriveFS.getInstance().getFilesInDirectoryForPublish());
		log.info("Tempo decorrido para pegar as informações dos arquivos : "+(System.currentTimeMillis() - init)+" ms"); 
		
		
		
		List<String> keysToRemove = new ArrayList<String>();
		
		for(String key : publishedFiles.keySet()){
			publishedFiles.put(key, false);
		}
		for(SharedFile sf : files){
			
			if(!publishedFiles.containsKey(sf.getPath())){

				client.publish(sf.getPath(), sf.getMd5Hash(), sf.getSize(), sf.getComplete());
				
			}
			publishedFiles.put(sf.getPath(),true);
									
		}
		
		for(String key : publishedFiles.keySet()){
			
			if(publishedFiles.get(key) == false){
				//Unpublish file
				//Provavelmente foi deletado
				keysToRemove.add(key);
			}
			
		}
		
		for(String key : keysToRemove){
			publishedFiles.remove(key);
		}

		
	}
	
	private void debugMap() {
		
		System.out.println("Nome do arquivo \t Publicado ? ");
		for(String name : publishedFiles.keySet()){
			System.out.println(name + "\t " + publishedFiles.get(name));
		}
	}
	
	public void run() {
		
		while(true){
						
			try {
				long init = System.currentTimeMillis();
				verificarMudancas();
				log.info("Tempo decorrido para verificar mudanças : "+(System.currentTimeMillis() - init)+" ms"); 
				debugMap();
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}



}
