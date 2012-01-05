package pl.edu.pjwstk.s8267.skj.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ProxyThread extends Thread {

	private Socket client;
	private InputStream fromClient;
	private OutputStream toClient;
	private InputStream fromServer;
	private OutputStream toServer;
	private MessageDigest md;
	

	public ProxyThread(Socket client) throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
		this.client = client;
		try {
			this.fromClient = client.getInputStream();
			this.toClient = client.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static final String[] methods = {"GET", "POST", "STATUS", "DELETE", "PUT"};
	
	@Override
	public void run() {
		StringBuilder header = new StringBuilder();
		URL url = null;
		String ifModifiedSince;
		boolean inHeader = false;
		Socket server = null;
		try {
			byte[] buffer = new byte[4096];
			while(true) {
				int len = fromClient.read(buffer);
				if(len <= 0) continue;
				String head = new String(buffer, 0, len);
				int newRequest = -1;//head.indexOf("GET");
				for(String method : methods) {
					newRequest = head.indexOf(method);
					if(newRequest > -1) break;
				}
				if(newRequest > -1) {
					ifModifiedSince = null;
					header = new StringBuilder();
					inHeader = true;
					if(newRequest > 0) {
						toServer.write(buffer, 0, newRequest);
						toServer.flush();
					}
					head = head.substring(newRequest);
					int line = head.indexOf("\r\n");
					if(line > 0) {
						String requestUri = head.substring(0, line).split(" ")[1];
						url = new URL(requestUri);
						header.append(head.replaceFirst(requestUri, url.getFile()));
					}
				} else {
					if(inHeader)
						header.append(new String(buffer, 0, len));
					else {
						if(toServer != null) {
							toServer.write(buffer, 0, newRequest);
							toServer.flush();
						}
					}
				}
				if(header.indexOf("\r\n\r\n") >= 0) {
					int k = header.indexOf("If-Modified-Since");
					md.update(url.toString().getBytes());
					BigInteger bigInt = new BigInteger(1,md.digest());
					String hashtext = bigInt.toString(16);
					// Now we need to zero pad it if you actually want the full 32 chars.
					while(hashtext.length() < 32 ){
					  hashtext = "0"+hashtext;
					}
					String p = "cache/"+hashtext+".bin";
					/*if(url.getFile().equals("/"))
						if(url.getPath().equals("/"))
							p = url.getHost()+".bin";
						else
							p = url.getHost()+url.getPath()+".bin";
					else
						p = url.getHost()+url.getPath()+url.getFile()+".bin";*/
					
					if(/*k<0 && */new File(new File("/tmp/"), p).exists()) {
						InputStream is = new FileInputStream(new File(new File("/tmp/"), p));
						byte[] b = new byte[4096];
						while(true) {
							int s = is.read(b);
							if(s == -1) break;
							toClient.write(b, 0, s);
						}
						toClient.flush();
						is.close();
					} else {
						server = new Socket(InetAddress.getByName(url.getHost()), 80);
						System.out.println("Pobieram adres "+url.getHost());
						System.out.println("Adres "+url.getHost()+" to "+server.getInetAddress().getHostAddress());
						File f = new File(new File("/tmp/"), p);
						f.getParentFile().mkdirs();
						
						ServerReaderThread srt = new ServerReaderThread(server, client, f);
						srt.start();
						toServer = server.getOutputStream();
						toServer.write(header.toString().getBytes());
						toServer.flush();
					}
					
					inHeader = false;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Watek: "+url);
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