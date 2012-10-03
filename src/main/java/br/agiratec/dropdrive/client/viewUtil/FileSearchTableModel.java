package br.agiratec.dropdrive.client.viewUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import com.google.common.collect.Sets;

import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.model.SharedFile;

public class FileSearchTableModel extends AbstractTableModel{

	private List<SharedFile> files = new ArrayList<SharedFile>();		
	
	public FileSearchTableModel(List<SharedFile> files){
		
		this.setFiles(files);		
		generateHash();					
	}
	
	private void generateHash(){
		List<SharedFile> filesThatIHave = DropDriveFS.getInstance().getFilesInDirectoryForPublish();
		Set<String> md5s = new HashSet<String>();
		for(SharedFile sf : filesThatIHave){
			md5s.add(sf.getMd5Hash());
		}
		
		
		//Marca os arquivos que eu j‡ adicionei		
		for(SharedFile sf : files){
			if(md5s.contains(sf.getMd5Hash())){
				sf.setComplete(true);				
			}else{
				sf.setComplete(false);
			}
		}
	}
	
	public void addTableModelListener(TableModelListener arg0) {}

	public Class<?> getColumnClass(int column) {
		return String.class;
	}

	public int getColumnCount() {
		return 7;
	}

	public String getColumnName(int column) {
		switch(column){
			case 0: return "Filename";
			case 1: return "MD5";
			case 2: return "Number of parts";
			case 3: return "Size(KB)";
			case 4: return "Users Online";
			case 5: return "Users with complete file";
			case 6: return "Users with incomplete file";			
			default : return "";
		}
	}

	public int getRowCount() {
		return getFiles().size();
	}

	public Object getValueAt(int row, int column) {
		switch (column) {
			case 0: return getFiles().get(row).getPath();
			case 1: return getFiles().get(row).getMd5Hash();
			case 2: return getFiles().get(row).getNumberOfParts();
			case 3: return getFiles().get(row).getSize();
			case 4: return getFiles().get(row).getUsersOnline().size();
			case 5: return getFiles().get(row).getHaveFileComplete().size();
			case 6: return getFiles().get(row).getHaveFileIncomplete().size();
			default:
				break;
			}
		return "";
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void removeTableModelListener(TableModelListener arg0) {}

	public void setValueAt(Object arg0, int arg1, int arg2) {
		
	}

	public List<SharedFile> getFiles() {
		return files;
	}

	public void setFiles(List<SharedFile> files) {
		this.files = files;
	}

}
