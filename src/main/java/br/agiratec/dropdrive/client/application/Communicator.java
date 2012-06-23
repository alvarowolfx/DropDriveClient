package br.agiratec.dropdrive.client.application;

import com.sun.jersey.api.client.ClientResponse;

public interface Communicator {
	public ClientResponse createNewPeer(String login,String name,String password,String email,String question,String answer);
	public boolean isConnectingWithServer();
}
