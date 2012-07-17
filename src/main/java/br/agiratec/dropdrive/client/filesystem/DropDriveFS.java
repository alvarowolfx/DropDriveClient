package br.agiratec.dropdrive.client.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.util.OS;

import br.agiratec.dropdrive.client.model.Chunk;
import br.agiratec.dropdrive.client.model.SharedFileHeader;
import br.agiratec.dropdrive.client.util.UserPreferences;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class DropDriveFS {

	private static DropDriveFS fs = null;
	private static String workingDirectory = "";
	private static final String INCOMPLETE = ".incomplete";
	private static final Long DEFAULT_CHUNK_SIZE = 1024l*1024l;
	private static final String OCULT_FILE_STRING = OS.isWindows() ? "_" : ".";

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
			SharedFileHeader sfh = loadFileDescriptor(path);
			return sfh.getMd5Hash();			
		}
		file = new File(workingDirectory+path);
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
			SharedFileHeader sfh = loadFileDescriptor(path);
			if(sfh.getChunksNumberOfFile().contains(new Integer(chunkNumber))){
				file = new File(workingDirectory+path+INCOMPLETE);			
			}else{
				return null;
			}
		}else{			
			file = new File(workingDirectory+path);
		}
		chunkNumber += 1;
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
	
	public boolean writeChunkOfFile(SharedFileHeader sFile,Chunk c){
		
		File file = new File(workingDirectory+sFile.getPath()+INCOMPLETE);
		if(!file.exists()){
			
			createSharedFile(sFile,file);			
			sFile.getChunksNumberOfFile().add(c.getChunkNumber());
			saveFileDescriptor(sFile);
			
			writeChunk(file,c);
			if(sFile.getNumberOfParts() == 1l){
				convertIncompleteToComplete(file);
			}
			
			return true;			
		}else{
						
			boolean exists = false;
			int numberOfParts = 0;
			for(Integer nChunk : sFile.getChunksNumberOfFile()){
				numberOfParts++;
				if(nChunk.intValue() == c.getChunkNumber().intValue()){
					exists = true;
					break;
				}
			}
			if(!exists){
				sFile.getChunksNumberOfFile().add(c.getChunkNumber());
				saveFileDescriptor(sFile);
				writeChunk(file,c);
				if(sFile.getNumberOfParts().intValue() == sFile.getChunksNumberOfFile().size()){
					convertIncompleteToComplete(file);
				}
			}else{
				if(sFile.getNumberOfParts().intValue() == numberOfParts){
					convertIncompleteToComplete(file);
				}					
			}
			return true;
			
		}
	
	}
	
	private void writeChunk(File file, Chunk c) {
		int number = c.getChunkNumber()-1;		
		
		System.out.println("escrevendo chunck numero "+number);
		try{
									
			RandomAccessFile f = new RandomAccessFile(file.getAbsolutePath(), "rw");
																
			f.seek(number*DEFAULT_CHUNK_SIZE.intValue());
			f.write(c.getContent());
			
			f.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
		
	}

	private void createSharedFile(SharedFileHeader sFile, File novo) {				
		try {
			
				
			RandomAccessFile f = new RandomAccessFile(novo.getAbsolutePath(), "rw");			
	        f.setLength(sFile.getSize());
	        f.close();
			
		} catch (IOException e) {	
			e.printStackTrace();
		}
	}

	private void saveFileDescriptor(SharedFileHeader sFile) {
		
		Properties descriptor = new Properties();
		descriptor.put("md5Hash",sFile.getMd5Hash());
		descriptor.put("numberOfParts",Long.toString(sFile.getNumberOfParts()));
		descriptor.put("size",Long.toString(sFile.getSize()));
		descriptor.put("parts",Arrays.toString(sFile.getChunksNumberOfFile().toArray()));
		
		try {
			File arquivo = new File(workingDirectory+OCULT_FILE_STRING+sFile.getPath());
			if(!arquivo.exists()){
				arquivo.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(arquivo);
			descriptor.store(fos,"Descriptor of Incomplete file in DropDrive");			
			fos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private SharedFileHeader loadFileDescriptor(String path){
		
		Properties descriptor = new Properties();
		
		File arquivo = new File(workingDirectory+OCULT_FILE_STRING+path);
		FileReader fr;
		try {
			fr = new FileReader(arquivo);
			descriptor.load(fr);
				
			SharedFileHeader sfh = new SharedFileHeader();
		
			sfh.setMd5Hash(descriptor.getProperty("md5Hash"));	
			sfh.setNumberOfParts(Long.decode(descriptor.getProperty("numberOfParts")));
			sfh.setSize(Long.decode(descriptor.getProperty("size")));
			String set = descriptor.getProperty("parts");			
			
			
			String sets[] = set.replace("[","").replace("]","").replace(" ","").split(",");
			HashSet<Integer> parts = new HashSet<Integer>();
			for(int i = 0 ; i < sets.length ; i++){
				parts.add(Integer.decode(sets[i]));
			}
			
			return sfh;
			
			
		} catch (FileNotFoundException e) {				
			e.printStackTrace();				
		} catch (IOException e) {
			e.printStackTrace();				
		}
		return null;
		
	}

	private void convertIncompleteToComplete(File file) {
		
		File saida = new File(file.getAbsolutePath().replace(INCOMPLETE, ""));
		file.renameTo(saida);
		file.delete();
		
		File descriptor = new File(OCULT_FILE_STRING+workingDirectory+file.getName());
		descriptor.delete();
						
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
	