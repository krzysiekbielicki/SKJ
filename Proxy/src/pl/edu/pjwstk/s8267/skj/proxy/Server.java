package pl.edu.pjwstk.s8267.skj.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class Server {
	public Server(int port, String cacheDir) throws IOException, NoSuchAlgorithmException {
		ServerSocket server = new ServerSocket(port);
		System.out.println("[S] Serwer jest urucomiony");
		while(true) {
			System.out.println("[S] Serwer słucha na "+port);
			Socket client = server.accept();
			System.out.println("[S] Serwer zaakceptował połączenie");
			ProxyThread pt = new ProxyThread(client, cacheDir);
			pt.start();
		}
	}
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		if(args.length == 0)
			new Server(8080, "/tmp/");
		else if(args.length == 1)
			new Server(8080, args[0]);
		else
			new Server(Integer.parseInt(args[0]), args[1]);
	}
}
