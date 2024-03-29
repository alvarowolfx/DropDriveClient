package br.agiratec.dropdrive.client.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.model.SharedFile;
import br.agiratec.dropdrive.client.model.SharedFileHeader;
import br.agiratec.dropdrive.client.socket.ChunkDownloadHandler;

public class DownloadService implements Runnable{

	private static final int DOWNLOAD_THREAD_LIMIT = Runtime.getRuntime().availableProcessors()*20;
	
	private Map<String,SharedFileHeader> incompleteFiles = new ConcurrentHashMap<String,SharedFileHeader>();
	private Map<String,List<Integer>> partsTryingToDownload = new HashMap<String,List<Integer>>();
	private Map<String,Future<Integer>> workers = new HashMap<String,Future<Integer>>();
	private ExecutorService executor = null;
	private Logger log = Logger.getLogger(DownloadService.class);
	private Random rand = new Random();
	
	public DownloadService(){		
		executor = Executors.newCachedThreadPool();	
		reloadIncompleteFiles();
	}
	
	private void reloadIncompleteFiles() {
		
		ArrayList<SharedFileHeader> files = DropDriveFS.getInstance().getIncompleteFilesInDirectory();				
		//Converte para ConcurrentHashMap
		log.info("Carregando arquivos incompletos");
		for(SharedFileHeader sf : files){
			
			incompleteFiles.put(sf.getPath(), sf);
		}
		log.info("Carregados");
	}
	
	public synchronized void addFile(SharedFile sf){
		
		log.info("Requisitando adi��o de arquivo novo");
		SharedFileHeader sfh = new SharedFileHeader();
		sfh.setMd5Hash(sf.getMd5Hash());
		sfh.setNumberOfParts(sf.getNumberOfParts());
		sfh.setChunksNumberOfFile(new HashSet<Integer>());
		sfh.setPath(sf.getPath());
		sfh.setSize(sf.getSize());
		
		incompleteFiles.put(sf.getPath(), sfh);
		
		DropDriveFS.getInstance().createIncompleteFile(sfh);
		
		log.info("Arquivo adicionado");
	}

	public void run() {
		
		while(true){
			
			if(workers.size() < DOWNLOAD_THREAD_LIMIT){
				
				reloadIncompleteFiles();
				if(!incompleteFiles.isEmpty()){
					//Gera novos workers
			
					int fileSorteado = rand.nextInt(incompleteFiles.size());
										
					SharedFileHeader sfh = incompleteFiles.get(incompleteFiles.keySet().toArray()[fileSorteado]);
					
					log.info("Arquivo escolhido "+sfh.getPath());
					
					if(!partsTryingToDownload.containsKey(sfh.getPath())){
						partsTryingToDownload.put(sfh.getPath(), new ArrayList<Integer>());
						log.info("Marcando "+sfh.getPath()+" para download");
					}
					//Pega a proxima parte faltando
					log.info("Pega a proxima parte faltando");
					int part = -1;
					
					for(int p = 1 ; p <= sfh.getNumberOfParts().intValue(); p++){
						
						if(!sfh.getChunksNumberOfFile().contains(p) && 
							!partsTryingToDownload.get(sfh.getPath()).contains(p)){
							
							part = p;
							break;
						}
					
					}
					
										
					if(part != -1){
						partsTryingToDownload.get(sfh.getPath()).add(part);
						workers.put(sfh.getPath()+":"+part,executor.submit(new ChunkDownloadHandler(sfh,part)));
						log.info("Foi escolhida a parte "+part+" do arquivo "+sfh.getPath());
					}
					
				}else{
					log.info("N�o existem arquivos para download");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {				
						e.printStackTrace();
					}
				}
			}else{
				log.info("Limite de thread foi atingido");
			}
			
			
			//Marca os trabalhos que estiverem terminados
			log.info("Marcando trabalhos terminados");
			List<String> keysToRemove = new ArrayList<String>();
			for(String key : workers.keySet()){
				
				Future<Integer> future = workers.get(key);
				log.info("Worker "+key+" est� trabalhando ?");
				if(future.isDone()){
					keysToRemove.add(key);
					log.info("Worker "+key+" terminou");
					try {
																								
						Integer result = future.get();
						if(result != -1){
							
							String k = key.split(":")[0];
							partsTryingToDownload.get(k).remove(result);
							
							SharedFileHeader sfh = incompleteFiles.get(k);
							sfh.getChunksNumberOfFile().add(result);
							
							if(sfh.getChunksNumberOfFile().size() >= sfh.getNumberOfParts()){
								//Terminou o arquivo tira ele da lista
								incompleteFiles.remove(k);
							}
							
						}else{
							String[] keyValue = key.split(":");
							partsTryingToDownload.get(keyValue[0]).remove(Integer.parseInt(keyValue[1]));
														
						}
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					} catch (ExecutionException e) {
						
						e.printStackTrace();
					} catch (NullPointerException e) {
						
						e.printStackTrace();
					}
				}
			}
			//Remove os trabalhos terminados
			log.info("Removendo workers terminados");
			for(String key : keysToRemove){
				workers.remove(key);
			}
			
			log.info("Dormindo um pouco =D");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}			
			
		}
		
	}

}
