package br.agiratec.dropdrive.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import br.agiratec.dropdrive.client.filesystem.DropDriveFS;
import br.agiratec.dropdrive.client.util.SwingUtil;
import br.agiratec.dropdrive.client.util.UserPreferences;
import br.agiratec.dropdrive.client.viewUtil.FileTableModel;

@SuppressWarnings("serial")
public class PrimaryAspect extends JXFrame{
	private Container ct;
	private JXPanel pnlWindow,pnlCommandBar,pnlFootInformation,pnlLeftSide;
	private JXButton btnChoseMyFolder,btnSearchAndDownload;
	private JFileChooser flcMyFolderDirectory;
	private JXTable tblFiles;
	private JScrollPane scpFiles;
	private FileTableModel modelFile;
	
	private static final int screenWidth =Toolkit.getDefaultToolkit().getScreenSize().width; 
	private static final int screenHeight=Toolkit.getDefaultToolkit().getScreenSize().height;	
	
	/**
	* Construtor default da classe
	*@author Igor Maldonado Floor
	*/
	public PrimaryAspect(){
		createGui();
	}
	
	/**
	* Este metodo cria a interface grafica da classe
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void createGui(){
		ct = this.getContentPane();
		
		tblFiles = new JXTable();
		modelFile = new FileTableModel(getFilesInDirectory());
		tblFiles.setModel(modelFile);
		scpFiles = new JScrollPane(tblFiles);
		
		flcMyFolderDirectory = new JFileChooser();
		flcMyFolderDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		btnChoseMyFolder = new JXButton(SwingUtil.criarImageIcon("/images/Open.png", "Escolher Arquivos"));
		btnChoseMyFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnChoseMyFolder_actionPerformed();
			}
		});
		btnSearchAndDownload = new JXButton(SwingUtil.criarImageIcon("/images/search-24-ns.png", "Buscar arquivos"));
		btnSearchAndDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				SwingUtilities.invokeLater(new Runnable() {
					 public void run() {
						 new SearchAndDownloadView(pnlCommandBar);				
					 }
				 });
				
			}
		});
		
		pnlWindow = new JXPanel(new MigLayout());
		pnlCommandBar = new JXPanel(new MigLayout());
		pnlCommandBar.setPreferredSize(new Dimension(screenWidth, screenHeight*8/100));
		pnlCommandBar.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		pnlFootInformation = new JXPanel(new MigLayout());
		pnlFootInformation.setPreferredSize(new Dimension(screenWidth, screenHeight*70/100));
		pnlFootInformation.setBorder(BorderFactory.createLineBorder(Color.green));
		pnlLeftSide = new JXPanel(new BorderLayout());
		pnlLeftSide.setPreferredSize(new Dimension(screenWidth*25/100, screenHeight));
		pnlLeftSide.setBorder(BorderFactory.createLineBorder(Color.red));
		
		pnlCommandBar.add(btnSearchAndDownload);
		pnlCommandBar.add(btnChoseMyFolder);
		
		pnlLeftSide.add(scpFiles,BorderLayout.CENTER);
		
		
		pnlWindow.add(pnlCommandBar,"dock north");
		pnlWindow.add(pnlFootInformation,"dock south");
		pnlWindow.add(pnlLeftSide,"dock west");
		customizeTblFiles();
		ct.add(pnlWindow,BorderLayout.CENTER);
		//setExtendedState(JXFrame.MAXIMIZED_BOTH);
		setSize(new Dimension(1280,800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
		
	public ArrayList<File> getFilesInDirectory(){
		return DropDriveFS.getInstance().getFilesInDirectory();
	}
	
	/**
	* Este metodo atualiza a Gui
	* @author Igor Maldonado Floor
	*/
	public void updateView(){
		//atualiza a table de arquivos
		modelFile.setFiles(DropDriveFS.getInstance().getFilesInDirectory());
		modelFile.fireTableDataChanged();
		tblFiles.updateUI();
	}
	
	
	/**
	* Este metodo implementa a action do botao btnChoseMyFolder
	* @return void
	* @author Igor Maldonado Floor
	*/
	public void btnChoseMyFolder_actionPerformed(){
		String myFolderDirectory = UserPreferences.getInstance().getPrefMyFolderDirectory();
		File file = new File(myFolderDirectory);
		if(file.isDirectory()){
			flcMyFolderDirectory.setCurrentDirectory(file);
		}
		if(flcMyFolderDirectory.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			file = flcMyFolderDirectory.getSelectedFile();
			UserPreferences.getInstance().setPrefMyFolderDirectory(file.getAbsolutePath());
			DropDriveFS.getInstance().refreshWorkingDirectory();
			modelFile.setFiles(new ArrayList<File>(getFilesInDirectory()));
			updateView();
		}
		
	}
	
	public void customizeTblFiles(){
		
		DefaultTableCellRenderer cAlign = new DefaultTableCellRenderer();
		cAlign.setHorizontalAlignment(SwingConstants.CENTER);
		for (int count = 0; count < tblFiles.getModel().getColumnCount(); count++) {
			tblFiles.getColumnModel().getColumn(count).setCellRenderer(cAlign);
		}
		cAlign = new DefaultTableCellRenderer();
		cAlign.setHorizontalAlignment(SwingConstants.LEFT);
		tblFiles.getColumnModel().getColumn(0).setCellRenderer(cAlign);
		
		tblFiles.getColumnModel().getColumn(0).setPreferredWidth(120);
		tblFiles.getColumnModel().getColumn(1).setPreferredWidth(30);
		tblFiles.getColumnModel().getColumn(2).setPreferredWidth(30);
	}
	

}
