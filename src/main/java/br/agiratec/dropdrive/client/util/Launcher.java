package br.agiratec.dropdrive.client.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.frame.PrimaryAspect;
import br.agiratec.dropdrive.client.service.DropDriveServiceOrchestrator;

public class Launcher {

	public Launcher() {
		
		
		SwingUtilities.invokeLater(new Runnable() {
			 public void run() {
				 new PrimaryAspect();				
			 }
		 });
		
		DropDriveServiceOrchestrator.getInstance().initServices();		
		 

	}

	public static void testesFS() {

		UserPreferences.getInstance().setPrefMyFolderDirectory(
				"/Users/alvaroviebrantz/Documents/Faculdade/DropDrive/");
		UserPreferences.getInstance().setPrefHostname(
				"http://dropdrive.agiratec.com.br/");
		final DropDriveFS fs = DropDriveFS.getInstance();
		String path = "eclipse-jee-juno-macosx-cocoa-x86_64.tar.gz";
		// String path = "metodologia.pages";
		// String path = "cupom.txt";
		// String path = "img.jpeg";

		Long init = System.currentTimeMillis();
		try {
			System.out
					.println("Hash MD5 do arquivo : " + fs.getMd5OfFile(path));
			Long tam = fs.getSizeOfFile(path);
			Long parts = fs.getNumberOfChunksOfFile(path);
			System.out.println("Arquivo est� incompleto ? "
					+ fs.fileIsIncomplete(path));
			System.out.println("Tamanho do arquivo : " + tam + " bytes");
			System.out.println("Numero de partes do arquivo : " + parts
					+ " partes");
			/*
			 * ClientCommunicator client = new ClientCommunicator();
			 * 
			 * System.out.println(client.isConnectingWithServer() ? "Online" :
			 * "Offline"); client.login("alvarowolfx","123456");
			 * 
			 * client.publish(path,
			 * fs.getMd5OfFile(path),tam,!fs.fileIsIncomplete(path));
			 * 
			 * //System.out.println(new String(c.getContent()));
			 * 
			 * SharedFileHeader file = new SharedFileHeader();
			 * file.setPath("teste"+path);
			 * file.setNumberOfParts(fs.getNumberOfChunksOfFile(path));
			 * file.setMd5Hash(fs.getMd5OfFile(path)); file.setSize(tam);
			 * 
			 * 
			 * 
			 * for(int i=0;i < file.getNumberOfParts().intValue() ; i++){ Chunk
			 * c = fs.readChunkOfFile(path, i); fs.writeChunkOfFile(file,c); }
			 * 
			 * Thread t1 = new Thread(new Runnable() {
			 * 
			 * public void run() { try { fs.readChunkOfFile(path, 0);
			 * System.out.println("Thread 1 terminou de ler"); } catch
			 * (FileNotFoundException e) { e.printStackTrace(); } catch
			 * (IllegalArgumentException e) { e.printStackTrace(); } } });
			 * 
			 * Thread t2 = new Thread(new Runnable() {
			 * 
			 * public void run() { try { fs.readChunkOfFile(path, 1);
			 * System.out.println("Thread 2 terminou de ler"); } catch
			 * (FileNotFoundException e) { e.printStackTrace(); } catch
			 * (IllegalArgumentException e) { e.printStackTrace(); } } });
			 * 
			 * t1.start(); t2.start();
			 */
			System.out.println((System.currentTimeMillis() - init) + " ms");

		} catch (FileNotFoundException e) {
			System.out.println("Arquivo n�o encontrado.");
		}
	}
	
	static Logger logger = Logger.getLogger(Launcher.class);
	public static void main(String[] args) {
		clean();
		PropertyConfigurator.configure("src/main/java/br/agiratec/dropdrive/client/log/System.properties");
		logger.debug("Startando Launcher");
		new Launcher();
				
		
	}
	
	public static void clean(){
		File file = new File("src/main/java/br/agiratec/dropdrive/client/log/System.log");
		file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
