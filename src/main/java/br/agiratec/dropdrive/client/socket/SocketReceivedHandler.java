package br.agiratec.dropdrive.client.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.enumerado.RequestType;
import br.agiratec.dropdrive.client.enumerado.ResponseType;
import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.model.Chunk;
import br.agiratec.dropdrive.client.model.SharedFile;
import br.agiratec.dropdrive.client.service.SocketReceiver;
import br.agiratec.dropdrive.client.util.HexByteUtil;

public class SocketReceivedHandler implements Runnable{

	private Socket sock = null;
	private DataInputStream in = null;
	private DropDriveFS fs = DropDriveFS.getInstance();
	private Logger log = Logger.getLogger(SocketReceiver.class);
	
	public SocketReceivedHandler(Socket s) {
		log.info("Usuario conectado com sucesso");
		this.sock = s;
	}
	
	public void run() {
		try {
			in = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			try {
				RequestType requestType = RequestType.getRequestType(in.readByte());
				
				switch (requestType) {
				case REQUEST_BLOCK:
					requestBlock();
					return;
				default:
					break;
				}
			} catch (IOException e) {
				log.debug("Cliente desconectado");
				e.printStackTrace();
				return;
			}
		}
	}

	private void requestBlock() throws IOException {
		log.info("Resquest Block chegou");
		byte md5Array[] = new byte[16];
		if(in.available() < 0)
			return;
		for(int i = 0; i <= 15; i++){
			md5Array[i] = in.readByte();
		}
		String md5 = HexByteUtil.getHexStringFromByteArray(md5Array);
		SharedFile sharedFile = fs.fileExist(md5);
		if(sharedFile == null){
			OutputStream out = sock.getOutputStream();
			out.write(ResponseType.FILE_NOT_FOUND.getValor());
			out.flush();
			return;
		}
		log.info("Recebendo a part");
		byte chunkArray[] = new byte[4];
		for(int i = 0 ; i <= 3 ; i++){
			chunkArray[i] = in.readByte();
		}
		int chunkNumber = ByteBuffer.wrap(chunkArray).getInt();
		log.info("Part que chegou " + chunkNumber);
		Chunk chunk = fs.readChunkOfFile(sharedFile.getPath(), chunkNumber-1);
		if(chunk == null){
			OutputStream out = sock.getOutputStream();
			out.write(ResponseType.CHUNK_NOT_FOUND.getValor());
			out.flush();
			return;
		}
		log.info("Mandando chunk com " + chunk.getContent().length);
		OutputStream out = sock.getOutputStream();
		log.info("O Chunk " + chunk.getContent());
		out.write(ResponseType.RESPONSE_BLOCK.getValor());
		out.write(chunk.getContent());
		out.flush();
		log.info("Enviado o block da parte " + chunkNumber);
		
		sock.close();
	}

}
