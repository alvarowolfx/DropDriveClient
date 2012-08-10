package br.agiratec.dropdrive.client.socket;

import java.util.List;
import java.util.concurrent.Callable;

import br.agiratec.dropdrive.client.model.SharedFileHeader;

public class ChunkDownloadHandler implements Callable<Integer>{

	
	private SharedFileHeader sFile;
	private int part;
	private List<String> users;

	public ChunkDownloadHandler(SharedFileHeader sFile, int part) {
		super();
		this.sFile = sFile;
		this.part = part;
	}

	/**
	* Implementação de como deve se baixar uma parte de um peer
	* 
	* @return -1 se houve algum erro ou retorna o numero da parte se houve sucesso
	* @author Alvaro Viebrantz
	*/
	public Integer call() throws Exception {
		
		return null;
	}

}
