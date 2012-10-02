package br.agiratec.dropdrive.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SharedFile implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9147598864106036967L;

	private String md5Hash;
		
	private String path;
	private Long size;
	private Long numberOfParts;	
	private Boolean complete;
	private List<String> usersOnline = new ArrayList<String>();
	private List<String> haveFileComplete = new ArrayList<String>();
	private List<String> haveFileIncomplete = new ArrayList<String>();

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

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public Long getNumberOfParts() {
		return numberOfParts;
	}

	public void setNumberOfParts(Long numberOfParts) {
		this.numberOfParts = numberOfParts;
	}

	public List<String> getUsersOnline() {
		return usersOnline;
	}

	public void setUsersOnline(List<String> usersOnline) {
		this.usersOnline = usersOnline;
	}

	public List<String> getHaveFileComplete() {
		return haveFileComplete;
	}

	public void setHaveFileComplete(List<String> haveFileComplete) {
		this.haveFileComplete = haveFileComplete;
	}

	public List<String> getHaveFileIncomplete() {
		return haveFileIncomplete;
	}

	public void setHaveFileIncomplete(List<String> haveFileIncomplete) {
		this.haveFileIncomplete = haveFileIncomplete;
	}
		
}
