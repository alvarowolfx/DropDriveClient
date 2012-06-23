package br.agiratec.dropdrive.client.application;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.JsonToken;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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
	
	public ClientResponse createNewPeer(String login,String name, String password, String email,
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
		System.out.println(service.getURI().getPath());
//		if(response!= null){
//			System.out.println("response: "+response.toString());
//			System.out.println("Response2: "+response.getEntity(String.class));
//		}
		return response;
	}
	
	public ClientCommunicator(){
		config = new DefaultClientConfig();
		client = Client.create(config);
		client.setConnectTimeout(5000);
		service = client.resource(getURI());
	}
	
	private static URI getURI() {
		return UriBuilder.fromUri(Launcher.getPreferences().getPrefHostname()).build();
	}

	public ClientResponse doPostOnService(){
		ClientResponse response=null;
		try{response = service.post(ClientResponse.class);}
		catch(com.sun.jersey.api.client.ClientHandlerException exception){}
		return response;
	}

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

	
	
}
