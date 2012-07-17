package br.agiratec.dropdrive.client.application;

import java.util.List;

import com.sun.jersey.api.client.ClientResponse;

public interface Communicator {
	public ClientResponse createUser(String login,String name,String password,String email,String question,String answer);
	public boolean isConnectingWithServer();
	public ClientResponse login(String login, String password);
	public ClientResponse publish(String fileName,String md5Hash,List<Integer> parts,Long size);
	public ClientResponse searchForDevices(String md5);
	public ClientResponse searchForFile(String fileName);
}
