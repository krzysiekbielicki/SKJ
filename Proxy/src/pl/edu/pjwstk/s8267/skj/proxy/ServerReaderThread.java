package pl.edu.pjwstk.s8267.skj.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

public class ServerReaderThread extends Thread {
	private Socket server;
	private InputStream fromServer;
	private OutputStream toClient;
	private FileOutputStream writer;

	public ServerReaderThread(Socket server, Socket client, File f) throws IOException {
		this.server = server;
		this.fromServer = server.getInputStream();
		this.toClient = client.getOutputStream();
		writer = new FileOutputStream(f);
	}

	@Override
	public void run() {
		byte[] buffer = new byte[4096];
		while(true) {
			try {
				int n = fromServer.read(buffer);
				if(n == -1) {
					server.close();
					break;
				}
				if(n > 0) {
					writer.write(buffer, 0, n);
					toClient.write(buffer, 0, n);
					toClient.flush();
				}
			} catch (Exception e) {
				try {
					server.close();
					writer.close();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				break;
			}
		}
	}
}
