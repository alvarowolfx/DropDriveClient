package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Chunk implements Serializable,Comparator<Chunk>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2754809355207008222L;

	private Integer chunkNumber;
		
	private byte[] content; 
				
	private Set<Device> owners = new HashSet<Device>();
			
	private SharedFile file;

	public Integer getChunkNumber() {
		return chunkNumber;
	}

	public void setChunkNumber(Integer chunkNumber) {
		this.chunkNumber = chunkNumber;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public Set<Device> getOwners() {
		return owners;
	}

	public void setOwners(Set<Device> owners) {
		this.owners = owners;
	}

	public SharedFile getFile() {
		return file;
	}

	public void setFile(SharedFile file) {
		this.file = file;
	}

	public int compare(Chunk c1, Chunk c2) {		
		return c1.chunkNumber.compareTo(c2.chunkNumber);
	}
		
	
}
