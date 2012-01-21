package pl.edu.pjwstk.s8267.skj.radio;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Client extends JFrame {
	protected boolean playing = true;

	private int port;

	private String hostname;
	private int listenPort;

	protected String username = null;

	protected String password;
	
	private Vector<ChannelInfo> channels = new Vector<ChannelInfo>();

	private ChannelsTableModel model;

	private JTable table = null;
	private AudioPlayThread currentPlayingThread = null;

	public Client(String hostname, int port) throws IOException {
		super();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.port = port;
		this.hostname = hostname;
		pack();
		JButton b = new JButton("STOP");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentPlayingThread != null)
					currentPlayingThread.stopPlaying();
				currentPlayingThread = new AudioPlayThread(Client.this, channels.get(table.getSelectedRow()).getPort());
				currentPlayingThread.start();
			}
		});
		setLayout(new BorderLayout());
		model = new ChannelsTableModel(channels);
		table = new JTable(model);
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(b, BorderLayout.SOUTH);
		setSize(300, 100);
		setVisible(true);
		networkInfoThread.start();
	}
	
	Thread networkInfoThread = new Thread() {
		public void run() {
			try {
				Socket socket = new Socket(hostname, port);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String line;
				while((line = br.readLine()) != null) {
					System.out.println(line);
					if(line.startsWith("NetRadio")) {
						bw.write("LOGIN\n");
						bw.flush();
					} else if(line.equals("Username:")) {
						if(username == null)
							username  = JOptionPane.showInputDialog("Nazwa użytkownika:");
						bw.write(username+"\n");
						bw.flush();
					} else if(line.equals("Password:")) {
						if(password == null)
							password = JOptionPane.showInputDialog("Hasło:");
						bw.write(password+"\n");
						bw.flush();
					} else if(line.startsWith("HELLO")) {
						bw.write("LISTCHANNELS\n");
						bw.flush();
					} else if(line.equals("LOGINERROR")) {
						username = null;
						password = null;
						bw.write("LOGIN\n");
						bw.flush();
					} else if(line.equals("CHANNELS")) {
						channels.clear();
						while((line = br.readLine()) != null) {
							if(line.equals("END CHANNELS")) {
								continue;
							} else {
								channels.addElement(new ChannelInfo(line));
							}
						}
						model.fireTableDataChanged();
					}
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	};

	public AudioFormat getAudioFormat() {
		ChannelInfo channel = channels.get(table.getSelectedRow());
		return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, channel.getFrameRate(), 16, 2, channel.getFrameSize(), channel.getFrameRate(), false);
	}

	public static void main(String[] args) throws IOException {
		new Client("127.0.0.1", 8000);
	}
}
