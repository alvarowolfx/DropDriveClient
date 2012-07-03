package br.agiratec.dropdrive.client.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTextField;
import org.json.JSONException;
import org.json.JSONObject;

import br.agiratec.dropdrive.client.application.ClientCommunicator;
import br.agiratec.dropdrive.client.application.CommunicatorFactory;
import br.agiratec.dropdrive.client.util.Launcher;
import br.agiratec.dropdrive.client.util.Messages;
import br.agiratec.dropdrive.client.util.UserPreferences;

import com.sun.jersey.api.client.ClientResponse;

@SuppressWarnings("serial")
public class CreateUserView extends JXDialog{
	private Container ct;
	private JXTextField txtLogin,txtName,txtPassword,txtEmail,txtQuestion,txtAnswer;
	private JXLabel lblLogin,lblName,lblPassword,lblEmail,lblQuestion,lblAnswer;
	private FrameBefore frameBefore;
	private JXButton btnRegister,btnCancel;
	
	private JXPanel pnlCreate,pnlButtons;
	
	
	private static final int screenWidth =Toolkit.getDefaultToolkit().getScreenSize().width; 
	private static final int screenHeight=Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final int width= 400;
	private static final int height = 250;

	/**
	* Este Ž o construtor Default da classe
	*@author Igor Maldonado Floor
	*/
	public CreateUserView(JComponent component){
		super(component);
	}
	
	/**
	* Este metodo tem por fun‹o montar a interface gr‡fica.
	* @return void
	* @author Igor Maldonado Floor
	*/
	public void createGui(){
		ct = this.getContentPane();
		
		lblLogin = new JXLabel(Messages.getString("login")+"*");
		lblName = new JXLabel(Messages.getString("name")+"*");
		lblPassword = new JXLabel(Messages.getString("password")+"*");
		lblEmail = new JXLabel(Messages.getString("email")+"*");
		lblQuestion = new JXLabel(Messages.getString("question")+"*");
		lblAnswer = new JXLabel(Messages.getString("answer")+"*");
		
		txtLogin = new JXTextField();
		txtLogin.setColumns(10);
		
		txtName = new JXTextField();
		txtName.setColumns(10);
		
		txtPassword = new JXTextField();
		txtPassword.setColumns(10);
		
		txtEmail = new JXTextField();
		txtEmail.setColumns(10);
		
		txtQuestion = new JXTextField();
		txtQuestion.setColumns(10);
		
		txtAnswer = new JXTextField();
		txtAnswer.setColumns(10);
		
		btnRegister = new JXButton(Messages.getString("register"));
		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRegister_actionPerformed();	
			}
		});
		
		btnCancel = new JXButton(Messages.getString("cancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCancel_actionPerformed();
			}
		});
		
		
		pnlCreate = new JXPanel(new GridLayout(6,2));
		pnlButtons = new JXPanel(new FlowLayout());
		
		pnlCreate.add(lblLogin);
		pnlCreate.add(txtLogin);
		pnlCreate.add(lblName);
		pnlCreate.add(txtName);
		pnlCreate.add(lblPassword);
		pnlCreate.add(txtPassword);
		pnlCreate.add(lblEmail);
		pnlCreate.add(txtEmail);
		pnlCreate.add(lblQuestion);
		pnlCreate.add(txtQuestion);
		pnlCreate.add(lblAnswer);
		pnlCreate.add(txtAnswer);
		
		pnlButtons.add(btnRegister);
		pnlButtons.add(btnCancel);
		
		ct.add(pnlCreate,BorderLayout.NORTH);
		ct.add(pnlButtons,BorderLayout.CENTER);
		
		setModal(true);
		setSize(width,height);
		setLocation((screenWidth-width)  / 2,(screenHeight-height) / 2);
		pack();
		this.setVisible(true);
		
	}
	
	/**
	* Este metodo implementa a Action do botao btnRegister
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void btnRegister_actionPerformed(){
		String login = txtLogin.getText();
		String name = txtName.getText();
		String password = txtPassword.getText();
		String email = txtEmail.getText();
		String question = txtQuestion.getText();
		String answer = txtAnswer.getText();
		if(login.length()>0 && name.length()>0 && password.length() >0 && email.length() >0 && question.length() >0
				&& answer.length()>0){
			ClientCommunicator communicator = CommunicatorFactory.getCommunicator();
			processResponse(communicator.createUser(login, name, password, email, question, answer));
		}else{
			String message = "";
			if(login.length()==0){
				message+=Messages.getString("login")+" "+Messages.getString("required")+"\n";
			}
			if(name.length()==0){
				message+=Messages.getString("name")+" "+Messages.getString("required")+"\n";
			}
			if(password.length()==0){
				message+=Messages.getString("password")+" "+Messages.getString("required")+"\n";
			}
			if(email.length()==0){
				message+=Messages.getString("email")+" "+Messages.getString("required")+"\n";
			}
			if(question.length()==0){
				message+=Messages.getString("question")+" "+Messages.getString("required")+"\n";
			}
			if(answer.length()==0){
				message+=Messages.getString("answer")+" "+Messages.getString("required")+"\n";
			}
			JOptionPane.showMessageDialog(null,message);
		}
	}
	
	/**
	* Este metodo implementa a Action do btnCancel
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void btnCancel_actionPerformed(){
		dispose();
	}
	
	/**
	* Este metodo tenta criar um novo User, para tal ele chama a classe ClientCommunicator, e baseando na ClientResponse, analisamos se o User foi criado com sucesso ou nao
	* @return void
	* @author Igor Maldonado Floor
	*/
	private void processResponse(ClientResponse response){
		if(response != null){
			if(response.getStatus() == 200){
				JOptionPane.showMessageDialog(null, Messages.getString("peerCreatedWithSucess"));
				UserPreferences.getInstance().setPrefMyUser(txtLogin.getText());
				UserPreferences.getInstance().setPrefMyPassword(txtPassword.getText());
				dispose();
				frameBefore.wake();
			}else{
				String msg="Problems Creating your peer founds:\n";
				try {
					JSONObject jo = new JSONObject(response.getEntity(String.class)).getJSONObject("meta");
					msg+="Error Type: "+jo.getString("errorType")+"\n";
					msg+="Error Detail: "+jo.getString("errorDetail")+"\n";
				} catch (JSONException e) {System.err.println(e.toString());}
				JOptionPane.showMessageDialog(null, msg);
			}
		}else{
			JOptionPane.showMessageDialog(null, Messages.getString("connectionProblem"));
		}
		
		
	}
	
	//Setter
	public void setFrameBefore(FrameBefore frameBefore){
		this.frameBefore = frameBefore;
	}
	
}
