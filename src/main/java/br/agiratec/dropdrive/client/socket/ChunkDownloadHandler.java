package br.agiratec.dropdrive.client.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.application.DropDriveServiceClient;
import br.agiratec.dropdrive.client.enumerado.RequestType;
import br.agiratec.dropdrive.client.enumerado.ResponseType;
import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.model.Chunk;
import br.agiratec.dropdrive.client.model.SharedFile;
import br.agiratec.dropdrive.client.model.SharedFileHeader;
import br.agiratec.dropdrive.client.util.HexByteUtil;
import br.agiratec.dropdrive.client.util.ResponseConverterUtil;
import br.agiratec.dropdrive.client.util.UserPreferences;
import de.htwg_konstanz.in.uce.hp.parallel.source.HolePunchingSource;
import de.htwg_konstanz.in.uce.hp.parallel.source.TargetNotRegisteredException;

public class ChunkDownloadHandler implements Callable<Integer>{

	private static final int DOWNLOAD = 0;
	private static final int DOWNLOADING = 1;
	private static final int CHECKING = 2;
	private int estado = DOWNLOAD;
	private static final Long DEFAULT_CHUNK_SIZE = 1024l*1024l;
	
	private Socket sock;
	private SharedFileHeader sFile;
	private Integer part;
	private List<String> users;
	private DropDriveFS fs = DropDriveFS.getInstance();
	private Logger log = Logger.getLogger(ChunkDownloadHandler.class);

	public ChunkDownloadHandler(SharedFileHeader sFile, int part) {
		super();
		this.sFile = sFile;
		this.part = part;
	}

	/**
	* Implementao de como deve se baixar uma parte de um peer
	* 
	* @return -1 se houve algum erro ou retorna o numero da parte se houve sucesso
	* @author Rodrigo Bussolo
	*/
	public Integer call() throws Exception {
		SocketAddress mediatorRegisterSocketAddress = new InetSocketAddress("www.agiratec.com.br", 10110);
		HolePunchingSource source = new HolePunchingSource();
		try {
			SharedFile sf = null;
			DropDriveServiceClient client = new DropDriveServiceClient();
			ArrayList<SharedFile> sharedFiles = (ArrayList<SharedFile>) ResponseConverterUtil.jsonSearchResponseToList(client.searchForFile(sFile.getPath()));
			for (SharedFile sharedFile : sharedFiles) {
				if(sharedFile.getMd5Hash().equals(sFile.getMd5Hash())){
					sf = sharedFile;
					break;
				}
			}
			Random rand = new Random();
			List<String> usersOnline = sf.getUsersOnline();
			int randomUser;
			String userChossed = null;
			String user = null;
			while(usersOnline.size() > 0){
				randomUser = rand.nextInt(sf.getUsersOnline().size());
				user = usersOnline.get(randomUser);
				if(!user.equals(UserPreferences.getInstance().getComputerIdentifier())){
					userChossed = user;
					break;
				}
				usersOnline.remove(user);
			}
			if(userChossed == null){
				return -1;
			}
			
			sock = source.getSocket(userChossed,mediatorRegisterSocketAddress);
			log.debug(sock.getLocalAddress());
			log.info("Localizado o cliente para download!");
			
			while(true){
				switch (estado) {
				case DOWNLOAD:					
					requestBlock();
					estado = DOWNLOADING;
					break;
				case DOWNLOADING:
					return responseBlock();
				case CHECKING:
					break;
				default:
					break;
				}
				
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (TargetNotRegisteredException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private Integer responseBlock() throws IOException {
		log.info("Enviando response block");
		DataInputStream in = new DataInputStream(sock.getInputStream());		
		ResponseType response = ResponseType.getResponseType(in.readByte());
		log.info("Response chegou com sucesso");
		switch (response) {
		case RESPONSE_BLOCK:
			log.info("Recebendo parte "+part+" do arquivo");
			Chunk chunk = new Chunk();
			chunk.setChunkNumber(part);
			int size = 0;
			if(sFile.getSize() <= (part*DEFAULT_CHUNK_SIZE)){
				size = (int) (sFile.getSize()- ((part-1)*DEFAULT_CHUNK_SIZE));
			}else{
				size = DEFAULT_CHUNK_SIZE.intValue();
			}
			log.info("Tamanho da parte "+part+" = "+size);
			
			byte content[] = new byte[size];
			int i = 0;
			while(i < (int)size ){
				
				//log.info("Chegou o byte "+i+ "da parte "+part);
				content[i] = in.readByte();
				i++;
				
			}
			chunk.setContent(content);
			fs.writeChunkOfFile(sFile, chunk);
				
			sock.close();
			return part;
		case CHUNK_NOT_FOUND:
			log.info("Parte "+part+" do arquivo n�o foi encontrada.");
			sock.close();
			return -1;
		case FILE_NOT_FOUND:
			sock.close();
			return -1;
		default:
			break;
		}
		return 0;
	}

	private void requestBlock() throws IOException {
		
		OutputStream out = sock.getOutputStream();
		out.write(RequestType.REQUEST_BLOCK.getValor());
		byte md5Array[] = HexByteUtil.getByteArrayFromHexString(sFile.getMd5Hash());
		log.info("Enviando o md5");
		out.write(md5Array);		
		log.info("Enviado o md5 e agora enviando parte");		
		byte[] arrayPart = ByteBuffer.allocate(4).putInt(part).array();				
		out.write(arrayPart);		
		log.info("Enviando a parte requerida");
		out.flush();
		log.info("Request enviada da parte "+part);
	}

}