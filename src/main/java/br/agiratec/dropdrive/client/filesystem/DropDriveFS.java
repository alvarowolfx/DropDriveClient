package br.agiratec.dropdrive.client.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;

import br.agiratec.dropdrive.client.model.Chunk;
import br.agiratec.dropdrive.client.util.UserPreferences;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class DropDriveFS {

	private static DropDriveFS fs = null;
	private static String workingDirectory = "";
	private static final String INCOMPLETE = ".incomplete";
	private static final Long DEFAULT_CHUNK_SIZE = 1024l*1024l;

	static {
		fs = new DropDriveFS();
	}

	private DropDriveFS() {
		workingDirectory = UserPreferences.getInstance().getPrefMyFolderDirectory();
	}

	public static DropDriveFS getInstance() {
		return fs;
	}

	public Long getSizeOfFile(String path) throws FileNotFoundException{
		File file = null;
		if(fileIsIncomplete(path)){
			file = new File(workingDirectory+path+INCOMPLETE);
		}else{
			file = new File(workingDirectory+path);
		}				
		Long saida = file.length();		
		return saida;
	}

	public String getMd5OfFile(String path) throws FileNotFoundException{		
		File file = null;
		if(fileIsIncomplete(path)){
			file = new File(workingDirectory+path+INCOMPLETE);
		}else{
			file = new File(workingDirectory+path);
		}
		String saida = "";
		try {
			saida = Files.hash(file,Hashing.md5()).toString();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		return saida;
	}
	
	public Long getNumberOfChunksOfFile(String path) throws FileNotFoundException{		
		Long size = getSizeOfFile(path);
		double sizeInMB = size/(1024.0*1024.0);		
		sizeInMB = Math.ceil(sizeInMB);				
		return (long) sizeInMB;
	}
	
	public Chunk readChunkOfFile(String path, int chunkNumber) throws FileNotFoundException{
		File file = null;
		if(fileIsIncomplete(path)){
			file = new File(workingDirectory+path+INCOMPLETE);
			//N‹o implementado ainda
		}else{			
			file = new File(workingDirectory+path);
			Chunk c = new Chunk();
			c.setChunkNumber(chunkNumber);
			int size = 0;
			if(file.length() <= (chunkNumber*DEFAULT_CHUNK_SIZE)){
				size = (int) (file.length()- ((chunkNumber-1)*DEFAULT_CHUNK_SIZE));
			}else{
				size = DEFAULT_CHUNK_SIZE.intValue();
			}
			System.out.println("Tamanho do buffer : "+size);
			byte dst[] = new byte[size];
			MappedByteBuffer mappedBuffer;
			try {
				mappedBuffer = Files.map(file);			
				
				mappedBuffer.position((chunkNumber-1)*DEFAULT_CHUNK_SIZE.intValue());
				mappedBuffer.get(dst, 0 , size);								
				
				c.setContent(dst);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			
			return c;
		}		
		return null;
	}
	
	public boolean fileIsIncomplete(String path) throws FileNotFoundException{
		File file = new File(workingDirectory+path);
		if(!file.exists()){
			file = new File(workingDirectory+path+INCOMPLETE);
			if(file.exists()){
				return true;
			}else{
				throw new FileNotFoundException();
			}
		}else{
			return false;
		}
	}
	
}
	