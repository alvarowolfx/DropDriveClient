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
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import br.agiratec.dropdrive.client.util.Launcher;
import br.agiratec.dropdrive.client.util.UserPreferences;
import br.agiratec.dropdrive.client.viewUtil.FileTableModel;

@SuppressWarnings("serial")
public class PrimaryAspect extends JXFrame{
	private Container ct;
	private JXPanel pnlWindow,pnlCommandBar,pnlFootInformation,pnlLeftSide;
	private JXButton btnChoseMyFolder;
	private JFileChooser flcMyFolderDirectory;
	private JXTable tblFiles;
	private JScrollPane scpFiles;
	private FileTableModel modelFile;
	
	private static final int screenWidth =Toolkit.getDefaultToolkit().getScreenSize().width; 
	private static final int screenHeight=Toolkit.getDefaultToolkit().getScreenSize().height;
	private  UserPreferences preferences = Launcher.getPreferences();
	private File sharedDirectory = new File(Launcher.getPreferences().getPrefMyFolderDirectory());
	
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
		
		btnChoseMyFolder = new JXButton(criarImageIcon("/images/Open.png", "Escolher Arquivos"));
		btnChoseMyFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnChoseMyFolder_actionPerformed();
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
		
		pnlCommandBar.add(btnChoseMyFolder);
		
		pnlLeftSide.add(scpFiles,BorderLayout.CENTER);
		
		
		pnlWindow.add(pnlCommandBar,"dock north");
		pnlWindow.add(pnlFootInformation,"dock south");
		pnlWindow.add(pnlLeftSide,"dock west");
		customizeTblFiles();
		ct.add(pnlWindow,BorderLayout.CENTER);
		setExtendedState(JXFrame.MAXIMIZED_BOTH);
		setVisible(true);
	}
	
	/**
	* Este metodo retorna um list de arquivos que obedece a condicao de nao ser diretorio e nem estar oculto
	* @return List<File>
	* @author Igor Maldonado Floor
	*/
	public ArrayList<File> getFilesInDirectory(){
		File file = sharedDirectory;
		ArrayList<File> filesList = new ArrayList<File>();
		if(file.isDirectory()){
			File[] files = file.listFiles();
			
			for (int i = 0; i < files.length; i++) {
				if(!files[i].isDirectory() && !files[i].isHidden()){
					filesList.add(files[i]);
				}
			}
		}
		return filesList;
	}
	
	/**
	* Este metodo atualiza a Gui
	* @return void
	* @author Igor Maldonado Floor
	*/
	public void updateView(){
		//atualiza a table de arquivos
		modelFile.setFiles(getFilesInDirectory());
		modelFile.fireTableDataChanged();
		tblFiles.updateUI();
	}
	
	
	/**
	* Este metodo implementa a action do botao btnChoseMyFolder
	* @return void
	* @author Igor Maldonado Floor
	*/
	public void btnChoseMyFolder_actionPerformed(){
		String myFolderDirectory = preferences.getPrefMyFolderDirectory();
		File file = new File(myFolderDirectory);
		if(file.isDirectory()){
			flcMyFolderDirectory.setCurrentDirectory(file);
		}
		if(flcMyFolderDirectory.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			file = flcMyFolderDirectory.getSelectedFile();
			preferences.setPrefMyFolderDirectory(file.getAbsolutePath());
			sharedDirectory = file;
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
	
	/**
	* Este metodo retorna uma imagem criada baseado na url passada para ele
	* @return ImageIcon
	* @author Igor Maldonado Floor
	*/
	public  ImageIcon criarImageIcon(String caminho, String descricao) {  
        java.net.URL imgURL = getClass().getResource(caminho);  
        if (imgURL != null) {  
            return new ImageIcon(imgURL, descricao);  
        } else {  
            System.err.println("Nao foi possivel carregar o arquivo de imagem: " + caminho);  
            return null;  
        }  
	}
	
}
