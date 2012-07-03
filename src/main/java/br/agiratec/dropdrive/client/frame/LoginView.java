package br.agiratec.dropdrive.client.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.ws.rs.core.Response;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextField;

import br.agiratec.dropdrive.client.application.ClientCommunicator;
import br.agiratec.dropdrive.client.application.CommunicatorFactory;
import br.agiratec.dropdrive.client.util.Launcher;
import br.agiratec.dropdrive.client.util.Messages;
import br.agiratec.dropdrive.client.util.UserPreferences;

import com.sun.jersey.api.client.ClientResponse;

@SuppressWarnings("serial")
public class LoginView extends JXFrame implements FrameBefore{
	private JXLabel lblLogin,lblPassword,lblStatusConnection,lblRemember;
	private JXTextField txtLogin;
	private JPasswordField txpPassword;
	private JXButton btnLogin,btnExit;
	private Container ct;
	private JXPanel pnlLogin,pnlButtons,pnlStatusConnection,pnlWindow;
	private JCheckBox cbRemember;
	
	private static final int screenWidth =Toolkit.getDefaultToolkit().getScreenSize().width; 
	private static final int screenHeight=Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int width= 400;
	private static final int height = 115;
	private ImageIcon connectionOn,connectionOff;
	private FrameBefore thisFrame = this;
	
	/**
	* Este é o construtor padrão da classe
	*@author Igor Maldonado Floor
	*/
	public LoginView(){
		createGui();
	}
	
	/**
	* Utilizado para montar a interface Grafica
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void createGui(){
		ct = this.getContentPane();
		lblLogin = new JXLabel(Messages.getString("login"));
		lblPassword = new JXLabel(Messages.getString("password"));
		lblStatusConnection = new JXLabel();
		lblRemember = new JXLabel(Messages.getString("remember"));
		
		txtLogin = new JXTextField();
		txtLogin.setColumns(10);
		txpPassword = new JPasswordField(10);
		cbRemember = new JCheckBox();
		cbRemember.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cbRemember_actionPerformed();
			}
		});
		
		
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
				
		pnlLogin = new JXPanel(new GridLayout(3,2));
		pnlButtons = new JXPanel(new FlowLayout());
		pnlStatusConnection = new JXPanel(new BorderLayout());
		pnlWindow = new JXPanel(new MigLayout());
		pnlLogin.add(lblLogin);
		pnlLogin.add(txtLogin);
		pnlLogin.add(lblPassword);
		pnlLogin.add(txpPassword);
		pnlLogin.add(lblRemember);
		pnlLogin.add(cbRemember);
		pnlButtons.add(btnLogin);
		pnlButtons.add(btnExit);
		pnlStatusConnection.add(lblStatusConnection);
		
		
		pnlWindow.add(pnlLogin);
		pnlWindow.add(pnlStatusConnection,"span 1 2 ,wrap");
		pnlWindow.add(pnlButtons);
		
		rememberingMyLogin();
		
		ct.add(pnlWindow);
		setSize(width,height);
		setLocation((screenWidth-width)  / 2,(screenHeight-height) / 2);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		updateStatusConnection();
	}
	
	/**
	* Este metodo implementa a Action do botao btnLogin
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void btnLogin_actionPerformed(){
		if(txtLogin.getText().length() > 0 && txpPassword.getPassword().length > 0){
			if(loginSucessful()){
				if(cbRemember.isSelected()){
					doRememberMe();
				}else{
					doNotRememberMe();
				}
				dispose();
				new PrimaryAspect();
			}else{
				JOptionPane.showMessageDialog(null, Messages.getString("loginFail"));
				txpPassword.setBorder(BorderFactory.createLineBorder(Color.red));
			}
		}else{
			JOptionPane.showMessageDialog(null, Messages.getString("login/passwordEmpty"));
		}
		
	}
	
	/**
	* Este metodo implementa a action do botao btnCancel
	* @return void
	* @author Igor Maldonado Floor
	*/
	public void btnCancel_actionPerformed(){
		System.exit(0);
	}
	
	/**
	* Este metodo mota a JMenubar para ser colocada no frame :)
	* @return JMenuBar
	* @author Igor Maldonado Floor
	*/
	private JMenuBar prepareMenuBar(){
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
	
	/**
	* Este metodo implementa a Action do JMenuItem miCreate
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void miCreate_actionPerformed(){
		CreateUserView cuv = new CreateUserView(btnLogin);
		cuv.setFrameBefore(thisFrame);
		cuv.createGui();
	}
	
	/**
	* Este metodo implementa a Action do JMenuItem miConfig
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void miConfig_actionPerformed(){
		String hostname = UserPreferences.getInstance().getPrefHostname();
		String newHostName="";
		newHostName = JOptionPane.showInputDialog(Messages.getString("pleaseInsertHostName"),hostname);
		if(newHostName != null){
			if(!newHostName.startsWith("http://")){
				newHostName="http://"+newHostName;
			}
			if(JOptionPane.showConfirmDialog(null, Messages.getString("confirmNewHostName")+": "+newHostName+"?")==JOptionPane.YES_OPTION){
				UserPreferences.getInstance().setPrefHostname(newHostName);
				updateStatusConnection();
			}
		}
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
	
	/**
	* Este metodo Atualiza a imageIcon que representa a conexao com o servidor
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void updateStatusConnection(){
		ClientCommunicator communicator = CommunicatorFactory.getCommunicator();
		if(communicator.isConnectingWithServer()){
			lblStatusConnection.setIcon(connectionOn);
		}else{
			lblStatusConnection.setIcon(connectionOff);
		}
	}
	
	/**
	* Metodo que implementa o JCheckBox cbRemember
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void cbRemember_actionPerformed(){
		if(cbRemember.isSelected()){
			doRememberMe();
		}else{
			doNotRememberMe();
		}
	}

	/**
	* Este método é chamado quando o frame que cria User fecha, e este frame atualiza o campo login e senha
	* @return void
	* @author Igor Maldonado Floor
	*/
	public void wake() {
		updateStatusConnection();
		txtLogin.setText(UserPreferences.getInstance().getPrefMyUser());
		txpPassword.setText(UserPreferences.getInstance().getPrefMyPassword());
	}
	
	/**
	* Este metodo verifica se a combinação login/password é valida, para tal ele verifica com o servidor.
	* @return boolean
	* @author Igor Maldonado Floor
	*/
	private boolean loginSucessful(){
		ClientCommunicator communicator = CommunicatorFactory.getCommunicator();
		@SuppressWarnings("deprecation")
		ClientResponse response = communicator.login(txtLogin.getText(), txpPassword.getText());
		if(response != null && response.getStatus() == Response.Status.OK.getStatusCode()){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	* Metodo chamado para verficar se nas preferences esta selecionado para lembrar o usuario, caso esteja, ela puxa o login e a senha do usuario.
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void rememberingMyLogin(){
		UserPreferences preferences = UserPreferences.getInstance();
		if(preferences.getPrefRememberMe().equals("true")){
			txtLogin.setText(preferences.getPrefMyUser());
			txpPassword.setText(preferences.getPrefMyPassword());
			cbRemember.setSelected(true);
		}
	}
	
	/**
	* Metodo utilizado para que o programa se lembre do login e senha
	* @return void
	* @author Igor Maldonado Floor
	*/
	@SuppressWarnings("deprecation")
	private void doRememberMe(){
		UserPreferences preferences = UserPreferences.getInstance();
		preferences.setPrefMyUser(txtLogin.getText());
		preferences.setPrefMyPassword(txpPassword.getText());
		preferences.setPrefRememberMe("true");
	}
	
	/**
	* Metodo utilizado para que o programa nao se lembre do login e senha
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void doNotRememberMe(){
		UserPreferences preferences = UserPreferences.getInstance();
		preferences.setPrefMyUser("");
		preferences.setPrefMyPassword("");
		preferences.setPrefRememberMe("false");
	}
	
}
