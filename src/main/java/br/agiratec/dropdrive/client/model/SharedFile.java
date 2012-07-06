package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SharedFile implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9147598864106036967L;

	private String md5Hash;
		
	private String path;
	private Long size;
	private Long numberOfParts;	
	
	private Set<Chunk> chunksOfFiles = new HashSet<Chunk>();

	public String getMd5Hash() {
		return md5Hash;
	}

	public void setMd5Hash(String md5Hash) {
		this.md5Hash = md5Hash;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getNumberOfParts() {
		return numberOfParts;
	}

	public void setNumberOfParts(Long numberOfParts) {
		this.numberOfParts = numberOfParts;
	}

	public Set<Chunk> getChunksOfFiles() {
		return chunksOfFiles;
	}

	public void setChunksOfFiles(Set<Chunk> chunksOfFiles) {
		this.chunksOfFiles = chunksOfFiles;
	}
	
	
	
}
