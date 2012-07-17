package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SharedFileHeader implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4095922077173770097L;
	private String md5Hash;
	private String path;
	private Long size;
	private Long numberOfParts;	
	
	private Set<Integer> chunksNumberOfFile = new HashSet<Integer>();

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

	public Set<Integer> getChunksNumberOfFile() {
		return chunksNumberOfFile;
	}

	public void setChunksNumberOfFile(Set<Integer> chunksNumberOfFile) {
		this.chunksNumberOfFile = chunksNumberOfFile;
	}	
	
}
