package br.agiratec.dropdrive.client.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTextField;

import br.agiratec.dropdrive.client.application.DropDriveServiceClient;
import br.agiratec.dropdrive.client.model.SharedFile;
import br.agiratec.dropdrive.client.service.DropDriveServiceOrchestrator;
import br.agiratec.dropdrive.client.util.ResponseConverterUtil;
import br.agiratec.dropdrive.client.util.SwingUtil;
import br.agiratec.dropdrive.client.viewUtil.FileSearchTableModel;

import com.sun.jersey.api.client.ClientResponse;

public class SearchAndDownloadView extends JDialog{

	
	private JXButton btnAddToList,btnClose,btnSearch;
	private JXTextField txtSearch;
	private JXTable tblArquivos;
	private JScrollPane scpArquivos;
	private JXLabel lblSearch;
	private JXPanel pnlNorth,pnlCenter,pnlSouth;
	private FileSearchTableModel searchModel;
	private DropDriveServiceClient client;
	
	public SearchAndDownloadView(JComponent content) {
		//super(content);
		client = new DropDriveServiceClient();
		initGUI(content.getX(),content.getY(),content.getWidth());
			
	}

	private void initGUI(int x,int y, int width) {
		
		Container ct = this.getContentPane();
		
		pnlNorth = new JXPanel();
		lblSearch = new JXLabel("Search :");
		txtSearch = new JXTextField("type your search here");
		txtSearch.setColumns(30);
		btnSearch = new JXButton(SwingUtil.criarImageIcon("/images/search-24-ns.png", "Search"));
		
		
		pnlNorth.add(lblSearch);
		pnlNorth.add(txtSearch);
		pnlNorth.add(btnSearch);
		
		pnlCenter = new JXPanel();
		tblArquivos = new JXTable();
		searchModel = new FileSearchTableModel(new ArrayList<SharedFile>());
		tblArquivos.setModel(searchModel);
		scpArquivos = new JScrollPane(tblArquivos);		
		scpArquivos.setPreferredSize(new Dimension(width-100,500));
		
		pnlCenter.add(scpArquivos);
		
		
		pnlSouth = new JXPanel();
		btnAddToList = new JXButton("Add to list",SwingUtil.criarImageIcon("/images/badge-square-plus-24-ns.png", "Add"));
		btnClose = new JXButton("Close", SwingUtil.criarImageIcon("/images/badge-square-cross-24-ns.png", "Close"));
		
		pnlSouth.add(btnAddToList);
		pnlSouth.add(btnClose);
		
		ct.setLayout(new BorderLayout());
		ct.add(pnlNorth,BorderLayout.NORTH);
		ct.add(pnlCenter,BorderLayout.CENTER);
		ct.add(pnlSouth,BorderLayout.SOUTH);
		
		initActions();	
		
		this.setModal(true);
		//this.setSize(new Dimension(width,600));		
		this.setBounds(x+50, y+50, width-100, 500);
		this.setResizable(false);		
		
		
		this.setVisible(true);
		
	}
	
	private void initActions() {
		System.out.println("Iniciando action");		
		btnSearch.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Foi clicado");
				ClientResponse response = client.searchForFile(txtSearch.getText());
				if(response.getStatus() != 200){
					System.out.println("Deu erro "+response.getStatus());
					JOptionPane.showMessageDialog(null, "Nenhum arquivo encontrado");	
					searchModel = new FileSearchTableModel(new ArrayList<SharedFile>());
					tblArquivos.setModel(searchModel);
					updateView();
					return;
				}
				System.out.println("Status "+response.getStatus());
				List<SharedFile> result = ResponseConverterUtil.jsonSearchResponseToList(response);
				if(result == null){
					JOptionPane.showMessageDialog(null, "Nenhum arquivo encontrado");	
					searchModel = new FileSearchTableModel(new ArrayList<SharedFile>());
					tblArquivos.setModel(searchModel);
					updateView();
					return;
				}else{
					System.out.println("Atualizando tabela");
					searchModel = new FileSearchTableModel(result);
					tblArquivos.setModel(searchModel);
					updateView();
				}
				
			}			
			
		});
		
		btnAddToList.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				
				int selectedRow = tblArquivos.getSelectedRow();
				if(selectedRow >= 0){
					SharedFile sf = searchModel.getFiles().get(selectedRow);
					
					if(!sf.getComplete()){
						DropDriveServiceOrchestrator.getInstance().addFileToQueue(sf);					
						JOptionPane.showMessageDialog(null, " Arquivo adicionado a fila de download !!!");
					}else{
						JOptionPane.showMessageDialog(null, " O arquivo escolhido j‡ est‡ baixando !!!");
					}
					
				}else{
					JOptionPane.showMessageDialog(null, "Selecione um arquivo !!!");
				}
				
				
			}
		});
		
		btnClose.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent evt) {
				
				dispose();
				
			}
			
		});
		
	}
	
	public void updateView(){
						
		searchModel.fireTableDataChanged();
		tblArquivos.revalidate();
		tblArquivos.updateUI();
	}
	
}
