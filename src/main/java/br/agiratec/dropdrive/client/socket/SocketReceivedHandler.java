package br.agiratec.dropdrive.client.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReceivedHandler implements Runnable{

	private Socket sock = null;
	
	public SocketReceivedHandler(Socket s) {
		this.sock = s;
	}
	
	public void run() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			try {

				String msg = reader.readLine();
				System.out.println(msg);
				if(msg == null){
					System.out.println("Cliente desconectado");
					return;
				}

			} catch (IOException e) {
				System.out.println("Cliente desconectado");
				return;
			}
		}
	}

}
