package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Device implements Serializable{
	
	private Long id;
	private String lastRealIPAdress;
	private String name;
	

	private Peer deviceOwner;
	

	private Set<Chunk> chunksOfFiles = new HashSet<Chunk>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLastRealIPAdress() {
		return lastRealIPAdress;
	}
	public void setLastRealIPAdress(String lastRealIPAdress) {
		this.lastRealIPAdress = lastRealIPAdress;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement
	public Peer getDeviceOwner() {
		return deviceOwner;
	}
	public void setDeviceOwner(Peer deviceOwner) {
		this.deviceOwner = deviceOwner;
	}
	
	@XmlElement
	public Set<Chunk> getChunksOfFiles() {
		return chunksOfFiles;
	}
	public void setChunksOfFiles(Set<Chunk> chunksOfFiles) {
		this.chunksOfFiles = chunksOfFiles;
	}

}
