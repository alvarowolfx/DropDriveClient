package br.agiratec.dropdrive.client.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

import br.agiratec.dropdrive.client.socket.SocketReceivedHandler;
import br.agiratec.dropdrive.client.util.UserPreferences;
import de.htwg_konstanz.in.uce.hp.parallel.target.HolePunchingTarget;

public class SocketReceiver implements Runnable {

	static Logger logger = Logger.getLogger(SocketReceiver.class);
	private HolePunchingTarget localTarget = null;
	
	public SocketReceiver() {		
		initReceiver();
	}

	public void run() {
		
		while(true){
			
			try {
				Socket sock = localTarget.accept();
				
				logger.debug("Alguem se conectou");
				logger.debug(sock.getInetAddress());				
				Thread t = new Thread(new SocketReceivedHandler(sock));
				t.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

	private void initReceiver() {
		
		// Cria socket com o mediador e avisa que est‡ aberto a ser requisitado
		SocketAddress mediatorRegisterSocketAddress = new InetSocketAddress("www.agiratec.com.br", 10110);
		
		//Pega um identificar que deve ser unico no sistema
		String computerIdentificator = UserPreferences.getInstance().getComputerIdentifier();
		
		//Cadastra com o mediador sua abertura 
		localTarget = new HolePunchingTarget(mediatorRegisterSocketAddress, computerIdentificator);
		try {
			logger.debug("Tentando startar o hole punching com ID "+computerIdentificator);
			localTarget.start();
			logger.debug("Iniciado com sucesso");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
