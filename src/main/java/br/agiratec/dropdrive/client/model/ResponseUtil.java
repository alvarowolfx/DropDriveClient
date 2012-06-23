package br.agiratec.dropdrive.client.model;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ResponseUtil {
		
		
	public ResponseBuilder buildResponse(){		
		return new ResponseBuilder();		
	}
	
	
	@XmlRootElement
	@JsonWriteNullProperties(false)
	class Meta {
		
		public Integer code; 
		public String errorType;
		public String errorDetail;
		public String message;
			
	}
	
	@XmlRootElement
	class Response {
		
		public JSONObject content;
		
		public Response(String content) throws JSONException{
			this.content = new JSONObject(content);
		}
		
		public Response(){
			
		}
	}
	
	public class ResponseBuilder{
		
		private Meta meta = new Meta();
		private Response response = new Response();
		
		public ResponseBuilder withCode(int code){
			this.meta.code = code;
			return this;
		}
		
		public ResponseBuilder withMessage(String msg){
			this.meta.message = msg;
			return this;
		}
		
		public ResponseBuilder withError(String errorType,String errorDetail){
			this.meta.errorDetail = errorDetail;
			this.meta.errorType = errorType;
			return this;
		}
		
		public ResponseBuilder withResponse(String content) throws JSONException{
			this.response = new Response(content); 
			return this;
		}
		
		public ResponseBuilder withResponse(JSONObject content){
			this.response.content = content;
			return this;
		}
		
		public ResponseBuilder withResponse(Object entity,String entityName){
			ObjectMapper mapper = new ObjectMapper();
			try {
				JSONObject json = new JSONObject();
				json.put(entityName,new JSONObject(mapper.writeValueAsString(entity)));
				this.response.content = json;
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this;
		}
		
		public String build(){
			JSONObject response = new JSONObject();
			ObjectMapper mapper = new ObjectMapper();
			String saida = null;
			try {
				response.put("meta",new JSONObject(mapper.writeValueAsString(meta)));
				response.put("response",new JSONObject(this.response.content.toString(2)));
				saida = response.toString(2);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			return saida;
		}
	}
	
}
