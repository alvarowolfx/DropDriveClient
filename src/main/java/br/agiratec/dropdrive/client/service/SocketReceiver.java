package br.agiratec.dropdrive.client.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import br.agiratec.dropdrive.client.socket.SocketReceivedHandler;
import br.agiratec.dropdrive.client.util.UserPreferences;
import de.htwg_konstanz.in.uce.hp.parallel.target.HolePunchingTarget;

public class SocketReceiver implements Runnable {

	private HolePunchingTarget localTarget = null;
	
	public SocketReceiver() {		
		initReceiver();
	}

	public void run() {
		
		while(true){
			
			try {
				Socket sock = localTarget.accept();
				
				System.out.println("Alguem se conectou");				
				System.out.println(sock.getInetAddress());				
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
			System.out.println("Tentando startar o hole punching com ID "+computerIdentificator);
			localTarget.start();
			System.out.println("Iniciado com sucesso");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
