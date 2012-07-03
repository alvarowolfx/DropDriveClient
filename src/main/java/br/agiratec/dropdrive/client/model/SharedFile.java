package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class SharedFile implements Serializable{

	@Id
	@GeneratedValue
	private Long id;
	private String md5Hash;
	
	@ElementCollection
	private List<String> names;
	private Long size;
		
	@OneToMany(mappedBy = "file")
	private Set<Chunk> chunksOfFiles = new HashSet<Chunk>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMd5Hash() {
		return md5Hash;
	}

	public void setMd5Hash(String md5Hash) {		
		this.md5Hash = md5Hash;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public void addName(String name){
		this.names.add(name);
	}
	
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	@XmlElement
	public Set<Chunk> getChunksOfFiles() {
		return chunksOfFiles;
	}

	public void setChunksOfFiles(Set<Chunk> chunksOfFiles) {
		this.chunksOfFiles = chunksOfFiles;
	}
	
}
