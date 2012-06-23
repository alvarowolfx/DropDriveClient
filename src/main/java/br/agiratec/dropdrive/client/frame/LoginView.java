package br.agiratec.dropdrive.client.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextField;

import br.agiratec.dropdrive.client.application.ClientCommunicator;
import br.agiratec.dropdrive.client.util.Launcher;
import br.agiratec.dropdrive.client.util.Messages;

@SuppressWarnings("serial")
public class LoginView extends JXFrame implements FrameBefore{
	private JXLabel lblLogin,lblPassword,lblStatusConnection;
	private JXTextField txtLogin;
	private JPasswordField txpPassword;
	private JXButton btnLogin,btnExit;
	private Container ct;
	private JXPanel pnlLogin,pnlButtons,pnlStatusConnection,pnlWindow;
	
	private static final int screenWidth =Toolkit.getDefaultToolkit().getScreenSize().width; 
	private static final int screenHeight=Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int width= 400;
	private static final int height = 115;
	private ImageIcon connectionOn,connectionOff;
	private FrameBefore thisFrame = this;
	
	
	public LoginView(){
		createGui();
	}
	
	public void createGui(){
		ct = this.getContentPane();
		lblLogin = new JXLabel(Messages.getString("login"));
		lblPassword = new JXLabel(Messages.getString("password"));
		lblStatusConnection = new JXLabel();
		
		txtLogin = new JXTextField();
		txtLogin.setColumns(10);
		txpPassword = new JPasswordField(10);
		
		connectionOn = criarImageIcon("/images/ConnectionOn.png", "Connection Online");
		connectionOff = criarImageIcon("/images/ConnectionOff.png", "Connection Offline");
		lblStatusConnection.setIcon(connectionOff);
		
		setJMenuBar(prepareMenuBar());
		
		
		btnLogin = new JXButton(Messages.getString("login"));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnLogin_actionPerformed();
			}
		});
		
		btnExit = new JXButton(Messages.getString("exit"));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnCancel_actionPerformed();
			}
		});
				
		pnlLogin = new JXPanel(new GridLayout(2,2));
		pnlButtons = new JXPanel(new FlowLayout());
		pnlStatusConnection = new JXPanel(new BorderLayout());
		pnlWindow = new JXPanel(new MigLayout());
		pnlLogin.add(lblLogin);
		pnlLogin.add(txtLogin);
		pnlLogin.add(lblPassword);
		pnlLogin.add(txpPassword);
		pnlButtons.add(btnLogin);
		pnlButtons.add(btnExit);
		pnlStatusConnection.add(lblStatusConnection);
		
		
		pnlWindow.add(pnlLogin);
		pnlWindow.add(pnlStatusConnection,"span 1 2 ,wrap");
		pnlWindow.add(pnlButtons);
		
		ct.add(pnlWindow);
		setSize(width,height);
		setLocation((screenWidth-width)  / 2,(screenHeight-height) / 2);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		updateStatusConnection();
	}
	
	public void btnLogin_actionPerformed(){
		
	}
	
	public void btnCancel_actionPerformed(){
		System.exit(0);
	}
	
	public JMenuBar prepareMenuBar(){
		JMenuBar mnBar = new JMenuBar();
		JMenu menuOptions = new JMenu(Messages.getString("options"));
		JMenuItem miCreate = new JMenuItem(Messages.getString("create"));
		miCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				miCreate_actionPerformed();
			}
		});
		JMenuItem miConfig = new JMenuItem(Messages.getString("configurations"));
		miConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miConfig_actionPerformed();
			}
		});
		menuOptions.add(miCreate);
		menuOptions.add(miConfig);
		
		mnBar.add(menuOptions);
		return mnBar;
	}
	
	public void miCreate_actionPerformed(){
		CreateUserView cuv = new CreateUserView(btnLogin);
		cuv.setFrameBefore(thisFrame);
		cuv.createGui();
	}
	
	public void miConfig_actionPerformed(){
		String hostname = Launcher.getPreferences().getPrefHostname();
		String newHostName="";
		newHostName = JOptionPane.showInputDialog(Messages.getString("pleaseInsertHostName"),hostname);
		if(newHostName != null){
			if(!newHostName.startsWith("http://")){
				newHostName="http://"+newHostName;
			}
			if(JOptionPane.showConfirmDialog(null, Messages.getString("confirmNewHostName")+": "+newHostName+"?")==JOptionPane.YES_OPTION){
				Launcher.getPreferences().setPrefHostname(newHostName);
				updateStatusConnection();
			}
		}
	}
	
	public  ImageIcon criarImageIcon(String caminho, String descricao) {  
        java.net.URL imgURL = getClass().getResource(caminho);  
        if (imgURL != null) {  
            return new ImageIcon(imgURL, descricao);  
        } else {  
            System.err.println("Nao foi possivel carregar o arquivo de imagem: " + caminho);  
            return null;  
        }  
	}
	
	public void updateStatusConnection(){
		ClientCommunicator communicator = new ClientCommunicator();
		if(communicator.isConnectingWithServer()){
			lblStatusConnection.setIcon(connectionOn);
		}else{
			lblStatusConnection.setIcon(connectionOff);
		}
	}

	public void wake() {
		updateStatusConnection();
		txtLogin.setText(Launcher.getPreferences().getPrefMyUser());
		txpPassword.setText(Launcher.getPreferences().getPrefMyPassword());
	}
	
}
