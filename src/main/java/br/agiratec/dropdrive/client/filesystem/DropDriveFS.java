package br.agiratec.dropdrive.client.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.agiratec.dropdrive.client.model.Chunk;
import br.agiratec.dropdrive.client.model.SharedFile;
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
	
	public Chunk readChunkOfFile(String path, int chunkNumber) throws FileNotFoundException,IllegalArgumentException{
		if(chunkNumber >= getNumberOfChunksOfFile(path).intValue()){
			throw new IllegalArgumentException("Chunk number must be lower than file number of parts");
		}
		
		File file = null;		
		if(fileIsIncomplete(path)){
			file = new File(workingDirectory+path+INCOMPLETE);
			//N‹o implementado ainda
		}else{			
			chunkNumber += 1;
			file = new File(workingDirectory+path);
			Chunk c = new Chunk();
			c.setChunkNumber(chunkNumber);
			int size = 0;
			if(file.length() <= (chunkNumber*DEFAULT_CHUNK_SIZE)){
				size = (int) (file.length()- ((chunkNumber-1)*DEFAULT_CHUNK_SIZE));
			}else{
				size = DEFAULT_CHUNK_SIZE.intValue();
			}
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
	
	public boolean writeChunkOfFile(Chunk c){
		//Tem que colocar o sharefile que o pedao pertence
		if(c.getFile() == null){
			return false;			
		}else{
			SharedFile sFile = c.getFile();
			File file = new File(workingDirectory+sFile.getPath()+INCOMPLETE);
			if(!file.exists()){
				try {
					if(file.createNewFile()){
						if(sFile.getNumberOfParts() == 1l){
							saveSharedFileObject(sFile,file);
							convertIncompleteToComplete(file);
						}else{
							saveSharedFileObject(sFile,file);
						}
						return true;
					}
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}else{
				
				SharedFile sFileInput = readSharedFileObject(file);
				boolean exists = false;
				int numberOfParts = 0;
				for(Chunk chunk : sFileInput.getChunksOfFiles()){
					numberOfParts++;
					if(chunk.getChunkNumber().intValue() == c.getChunkNumber().intValue()){
						exists = true;
						break;
					}
				}
				if(!exists){
					sFileInput.getChunksOfFiles().add(c);
					saveSharedFileObject(sFileInput, file);
					if(sFileInput.getNumberOfParts().intValue() == sFileInput.getChunksOfFiles().size()){
						convertIncompleteToComplete(file);
					}
				}else{
					if(sFileInput.getNumberOfParts().intValue() == numberOfParts){
						convertIncompleteToComplete(file);
					}					
				}
				return true;
				
			}
		}
		return false;
	}
	
	private void convertIncompleteToComplete(File file) {
		SharedFile sFile = readSharedFileObject(file);
		
		ArrayList<Chunk> chunks = new ArrayList<Chunk>(sFile.getChunksOfFiles());
		Collections.sort(chunks,new Chunk());
		
		try {
			FileOutputStream fos = new FileOutputStream(workingDirectory+sFile.getPath());
			for(Chunk c : chunks){
				fos.write(c.getContent());
			}
			fos.flush();
			fos.close();
			
			File f = new File(workingDirectory+sFile.getPath()+INCOMPLETE);
			if(f.exists()){
				f.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
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
	
	private SharedFile readSharedFileObject(File file){
		FileInputStream fis;
		SharedFile sFile = null;
		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			sFile = (SharedFile) ois.readObject();
						
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return sFile;
		
	}
	
	private void saveSharedFileObject(SharedFile sFile,File file){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for(Chunk c : sFile.getChunksOfFiles()){
				c.setFile(null);
			}
			oos.writeObject(sFile);
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
	