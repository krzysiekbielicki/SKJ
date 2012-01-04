package pl.edu.pjwstk.s8267.skj.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class ProxyThread extends Thread {

	private Socket client;
	private BufferedReader clientReader;
	private BufferedWriter clientWriter;

	public ProxyThread(Socket client) {
		this.client = client;
		try {
			this.clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.clientWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		StringBuilder header = new StringBuilder();
		String line;
		URL url = null;
		try {
			while((line = clientReader.readLine()) != null) {
				header.append(line+"\n");
				if(line.startsWith("GET")) {
					url = new URL(line.split(" ")[1]);
				} else if(line.equals("")) {
					break;
				}
			}
			if(url != null) {
				Socket remote = null;
				StringBuilder response = new StringBuilder();
				try {
					System.out.println("Pobieram adres "+url.getHost());
					remote = new Socket(InetAddress.getByName(url.getHost()), 80);
					System.out.println("Adres "+url.getHost()+" to "+remote.getInetAddress().getHostAddress());
					//TODO: To trzeba wrzucić do osobnego wątku
					BufferedWriter remoteWriter = new BufferedWriter(new OutputStreamWriter(remote.getOutputStream()));
					MixedInputStream remoteReader = new MixedInputStream(remote.getInputStream());
					System.out.println("[S] Wysyłam nagłówek:\n"+header.toString());
					//TODO: Tu trzeba słać wszystko a nie tylko nagłówek
					remoteWriter.write(header.toString());
					remoteWriter.flush();
					
					clientWriter.write(response.toString().getBytes());
					System.out.println("[S] Otrzymano od zdalnego serwera:\n"+response.toString()+"\n");
							int t = remoteReader.read(buffer);
							clientWriter.write(buffer);
							clientWriter.flush();
						}
					
				} catch(IOException e) {
				} finally {
					remote.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.run();
	}

}