package br.agiratec.dropdrive.client.application;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import br.agiratec.dropdrive.client.util.Launcher;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class ClientCommunicator implements Communicator{

	ClientConfig config;
	Client client;
	WebResource service;
		
	/**
	* Metodo utilizado para criar um novo peer para a aplicação.
	* @return ClientResponse
	* @author Igor Maldonado Floor
	*/
	public ClientResponse createUser(String login,String name, String password, String email,
			String question, String answer) {
		ClientResponse response=null;
		try{response = service.path("api")
				.path("v1")
				.path("user")
				.path("new")
					.queryParam("login", login)
					.queryParam("username", name)
					.queryParam("password", password)
					.queryParam("email", email)
					.queryParam("question", question)
					.queryParam("answer",answer).accept(MediaType.APPLICATION_JSON)
						.post(ClientResponse.class);}
		catch(com.sun.jersey.api.client.ClientHandlerException exception){}
//		if(response!= null){
//			System.out.println("response: "+response.toString());
//			System.out.println("Response2: "+response.getEntity(String.class));
//		}
		return response;
	}
	
	public ClientResponse publishSharedFile(){
		return null;
	}
	
	
	/**
	* Metodo que verifica se é possivel conectar com o servidor
	* @return boolean
	* @author Igor Maldonado Floor
	*/
	public boolean isConnectingWithServer() {
			ClientResponse response=null;
			try{response = service.path("api").path("v1").path("user").path("new").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);}
			catch(com.sun.jersey.api.client.ClientHandlerException exception){}
			if(response != null){
				if(response.getStatus() != 404){
					return true;
				}else{
					return false;	
				}
			}else{
				return false;
			}
	}

	public ClientResponse login(String login, String password) {
		ClientResponse response=null;
		try{response = service.path("api")
				.path("v1")
				.path("auth")
					.queryParam("login", login)
					.queryParam("password", password).accept(MediaType.APPLICATION_JSON)
						.get(ClientResponse.class);}
		catch(com.sun.jersey.api.client.ClientHandlerException exception){}
//		if(response!= null){
//			System.out.println("response: "+response.toString());
//			System.out.println("Response2: "+response.getEntity(String.class));
//		}
		return response;
	}

	public ClientResponse publish(String user, String device, String fileName,
			String MD5) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientResponse searchForDevices(String md5) {
		// TODO Auto-generated method stub
		return null;
	}

	public ClientResponse searchForFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	* Construtor padrão da classe
	*@author Igor Maldonado Floor
	*/
	protected ClientCommunicator(){
		config = new DefaultClientConfig();
		client = Client.create(config);
		client.setConnectTimeout(5000);
		service = client.resource(getURI());
	}
	
	/**
	* Metodo que pega dos preferences o caminho para o servidor 
	* @return URI
	* @author Igor Maldonado Floor
	*/
	private static URI getURI() {
		return UriBuilder.fromUri(Launcher.getPreferences().getPrefHostname()).build();
	}


	
	
}
