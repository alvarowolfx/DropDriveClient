package br.agiratec.dropdrive.client.application;

import com.sun.jersey.api.client.ClientResponse;

public interface Communicator {
	public ClientResponse createUser(String login,String name,String password,String email,String question,String answer);
	public boolean isConnectingWithServer();
	public ClientResponse login(String login, String password);
	public ClientResponse publish(String fileName,String md5Hash,Long size,Boolean complete);
	public ClientResponse heartbeat();
	public ClientResponse searchForDevices(String md5);
	public ClientResponse searchForFile(String fileName);
}
