package br.agiratec.dropdrive.client.enumerado;

public enum ResponseType {
	
	RESPONSE_BLOCK((byte)0xaa), 
	CHUNK_NOT_FOUND((byte)0xbb),
	FILE_NOT_FOUND((byte)0xcc);	
	
	private byte valor;
	ResponseType(byte valor){
		this.valor = valor;
	}
	
	public byte getValor() {
		return valor;
	}
	public void setValor(byte valor) {
		this.valor = valor;
	}
	
	public static ResponseType getResponseType(byte valor){
		if(valor == ResponseType.RESPONSE_BLOCK.valor)
			return RESPONSE_BLOCK;
		if(valor == ResponseType.CHUNK_NOT_FOUND.valor)
			return CHUNK_NOT_FOUND;
		if(valor == ResponseType.FILE_NOT_FOUND.valor)
			return FILE_NOT_FOUND;
		return null;
	}
}
