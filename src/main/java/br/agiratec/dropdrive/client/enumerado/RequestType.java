package br.agiratec.dropdrive.client.enumerado;

public enum RequestType {
	
	REQUEST_BLOCK((byte)0xaa);	
	
	private byte valor;
	RequestType(byte valor){
		this.valor = valor;
	}
	
	public byte getValor() {
		return valor;
	}
	
	public void setValor(byte valor) {
		this.valor = valor;
	}
	
	public static RequestType getRequestType(byte valor){
		if(valor == RequestType.REQUEST_BLOCK.valor){
			return REQUEST_BLOCK;
		}
		return null;
	}
}
