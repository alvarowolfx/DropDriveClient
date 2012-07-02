package br.agiratec.dropdrive.client.viewUtil;

import java.io.File;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class FileTableModel extends AbstractTableModel{

	public  ArrayList<RichFile>files = new ArrayList<RichFile>();
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}
	
	public FileTableModel(){
		
	}
	
	public FileTableModel(ArrayList<File> files){
		this.files=RichFile.convertToRich(files);
		
	}

	public int getColumnCount() {
		return 3;
	}

	public String getColumnName(int column) {
		switch(column){
		case 0: return "FileName";
		case 1: return "FileType";
		case 2: return "Size(KB)";
		default : return "";
		}
	}

	public int getRowCount() {
		return files.size();
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0: return files.get(row).getFileName();
		case 1: return files.get(row).getFileExtension();
		case 2: return files.get(row).getFileSize();
		default:
			break;
		}
		return "";
	}

	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
	
	public void setFiles(ArrayList<File> files){
		this.files=RichFile.convertToRich(files);
	}
}
