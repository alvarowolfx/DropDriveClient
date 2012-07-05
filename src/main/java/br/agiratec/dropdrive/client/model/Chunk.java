package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Chunk implements Serializable{

	@Id
	@GeneratedValue
	private Long id;
	private Integer chunkNumber;
	
	@Transient
	private byte[] content; 
			
	@ManyToMany(mappedBy = "chunksOfFiles")
	private Set<Device> owners = new HashSet<Device>();
		
	@ManyToOne
	private SharedFile file;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getChunkNumber() {
		return chunkNumber;
	}
	public void setChunkNumber(Integer chunkNumber) {
		this.chunkNumber = chunkNumber;
	}
	@XmlElement
	public Set<Device> getOwners() {
		return owners;
	}
	public void setOwners(Set<Device> owners) {
		this.owners = owners;
	}
	
	@XmlElement
	public SharedFile getFile() {
		return file;
	}
	public void setFile(SharedFile file) {
		this.file = file;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	
}
