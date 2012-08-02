package br.agiratec.dropdrive.client.application;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import br.agiratec.dropdrive.client.util.UserPreferences;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
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
	
	/**
	* Método para publicação de arquivos no servidor do DropDrive
	* 
	* @param fileName Nome do arquivo está sendo publicado.
	* @param md5Hash  Hash do arquivo que está sendo publicado.
	* @param size	  Tamanho do arquivo em bytes.
	* @param complete Arquivo está complete ou não.
	* @return response Dados de resposta do servidor.
	* @author Alvaro Viebrantz
	*/
	public ClientResponse publish(String fileName,String md5Hash,Long size,Boolean complete){
		
		ClientResponse response=null;
		try{ 
			service.path("api")
				   .path("v1")
				   .path("publish")
				   		.queryParam("deviceID",UserPreferences.getInstance().getComputerIdentifier())
				   		.queryParam("fileName",fileName)
				   		.queryParam("md5", md5Hash)
				   		.queryParam("size", Long.toString(size))
				   		.queryParam("complete",Boolean.toString(complete))
				   			.accept(MediaType.APPLICATION_JSON)
				   				.post(ClientResponse.class);											
			
		}catch(ClientHandlerException exception){}

		return response;
	}
	
	
	/**
	* Metodo que verifica se é possivel conectar com o servidor
	* @return boolean
	* @author Alvaro Viebrantz 
	*/
	public boolean isConnectingWithServer() {
			ClientResponse response=null;
			try{
				response = service.path("api").accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			}catch(com.sun.jersey.api.client.ClientHandlerException exception){
				exception.printStackTrace();
			}
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
					.queryParam("deviceID",UserPreferences.getInstance().getComputerIdentifier())
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
	
	/**
	* Função responsável em sinalizar ao servidor de que o cliente continua interessado na troca de arquivos
	* 
	* @return response Dados de resposta do servidor.
	* @author Alvaro Viebrantz
	*/
	public ClientResponse heartbeat() {
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
	public ClientCommunicator(){
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
		return UriBuilder.fromUri(UserPreferences.getInstance().getPrefHostname()).build();
	}
		
	
}
