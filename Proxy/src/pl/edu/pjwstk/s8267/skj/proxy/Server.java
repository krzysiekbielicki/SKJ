package pl.edu.pjwstk.s8267.skj.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static final int PORT = 8080;
	public Server() throws IOException {
		ServerSocket server = new ServerSocket(PORT);
		System.out.println("[S] Serwer jest urucomiony");
		while(true) {
			System.out.println("[S] Serwer słucha na "+PORT);
			Socket client = server.accept();
			System.out.println("[S] Serwer zaakceptował połączenie");
			ProxyThread pt = new ProxyThread(client);
			pt.start();
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Server();
	}
}
