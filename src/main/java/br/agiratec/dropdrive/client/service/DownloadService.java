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
import java.util.logging.Logger;

import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.model.SharedFile;
import br.agiratec.dropdrive.client.model.SharedFileHeader;
import br.agiratec.dropdrive.client.socket.ChunkDownloadHandler;

public class DownloadService implements Runnable{

	private static final int DOWNLOAD_THREAD_LIMIT = Runtime.getRuntime().availableProcessors()*4;
	
	private Map<String,SharedFileHeader> incompleteFiles = new ConcurrentHashMap<String,SharedFileHeader>();
	private Map<String,List<Integer>> partsTryingToDownload = new HashMap<String,List<Integer>>();
	private Map<String,Future<Integer>> workers = new HashMap<String,Future<Integer>>();
	private ExecutorService executor = null;
	private Logger log = Logger.getLogger(getClass().getName());
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
	
	public void addFile(SharedFile sf){
		
		log.info("Requisitando adição de arquivo novo");
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
				
				if(!incompleteFiles.isEmpty()){
					//Gera novos workers				
					int fileSorteado = rand.nextInt(incompleteFiles.size()+1);
					SharedFileHeader sfh = incompleteFiles.get(incompleteFiles.keySet().toArray()[fileSorteado]);
					
					if(!partsTryingToDownload.containsKey(sfh.getPath())){
						partsTryingToDownload.put(sfh.getPath(), new ArrayList<Integer>());						
					}
					//Pega a proxima parte faltando
					int part = -1;
					if(sfh.getChunksNumberOfFile().isEmpty()){
						part = 1;
					}else{
						
						for(int p = 1 ; p <= sfh.getNumberOfParts().intValue(); p++){
							
							if(!sfh.getChunksNumberOfFile().contains(p) && 
								!partsTryingToDownload.get(sfh.getPath()).contains(p)){
								
								part = p;
								break;
							}
						
						}
					}	
					
					if(part != -1){
						partsTryingToDownload.get(sfh.getPath()).add(part);
						workers.put(sfh.getPath()+part,executor.submit(new ChunkDownloadHandler(sfh,part)));
					}
					
				}else{
					log.info("Não existem arquivos para download");
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
			List<String> keysToRemove = new ArrayList<String>();
			for(String key : workers.keySet()){
				
				Future<Integer> future = workers.get(key);
				if(future.isDone()){
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
						keysToRemove.add(key);
						e.printStackTrace();
					} catch (ExecutionException e) {
						keysToRemove.add(key);
						e.printStackTrace();
					}
				}
			}
			//Remove os trabalhos terminados
			for(String key : keysToRemove){
				workers.remove(key);
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}			
			
		}
		
	}

}
