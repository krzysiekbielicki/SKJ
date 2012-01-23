package pl.edu.pjwstk.s8267.skj.radio;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server extends Thread {
	private int port;
	private Vector<RadioChannel> radioChannels;

	public Server(int port, String channelsDirecotry, int firstSendPort) {
		this.port = port;
		radioChannels = new Vector<RadioChannel>();
		File[] channels = new File(channelsDirecotry).listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});
		for(int i = 0; i < channels.length; i++) {
			RadioChannel rc = new RadioChannel(channels[i], firstSendPort+i);
			radioChannels.addElement(rc);
			rc.start();
		}
		start();
	}
	
	@Override
	public void run() {
		ServerSocket server;
		try {
			server = new ServerSocket(port);
			while(true) {
				Socket client = server.accept();
				new ServerInfo(client, radioChannels).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if(args.length == 0)
			new Server(8000, "channels/", 10000);
		else
			new Server(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
	}
}
