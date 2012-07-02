package br.agiratec.dropdrive.client.viewUtil;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class RichFile extends File{

	private String fileName="fileNotFound";
	private String fileExtension="notFound";
	private Long fileSize=-666l;
	
	public RichFile(String path) {
		super(path);
		if(exists()){
			fileSize = this.length()/1024;
			String fullFileName = getName();
			String[] parts = fullFileName.split("\\.");
			if(parts.length > 1){
				fileExtension=parts[parts.length-1];
			}else{
				fileExtension="";
			}
			fileName=parts[0];
			for (int i = 1; i < parts.length-1; i++) {
				fileName=fileName+"."+parts[i];
			}
		}
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public Long getFileSize() {
		return fileSize;
	}
	
	public static ArrayList<RichFile> convertToRich(ArrayList<File> files){
		ArrayList<RichFile> riches = new ArrayList<RichFile>(); 
		for (File file : files) {
			riches.add(new RichFile(file.getAbsolutePath()));
		}
		return riches;
	}
	
	
}
