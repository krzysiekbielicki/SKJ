package pl.edu.pjwstk.s8267.skj.radio;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8265102518296579925L;

	protected boolean playing = true;

	private int port;

	private String hostname;

	protected static String username = null;

	protected static String password;
	
	private Vector<ChannelInfo> channels = new Vector<ChannelInfo>();

	private ChannelsTableModel model;

	private JTable table = null;
	private AudioPlayThread currentPlayingThread = null;

	private JButton b;

	protected int currentPlaying = -1;

	public Client(String hostname, int port) throws IOException {
		super();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.port = port;
		this.hostname = hostname;
		pack();
		b = new JButton("PLAY");
		b.setEnabled(false);
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentPlayingThread != null)
					currentPlayingThread.stopPlaying();
				if(table.getSelectedRow() != -1 && table.getSelectedRow() != currentPlaying) {
					currentPlayingThread = new AudioPlayThread(Client.this, channels.get(table.getSelectedRow()).getPort());
					currentPlayingThread.start();
					currentPlaying = table.getSelectedRow();
				} else {
					currentPlaying = -1;
				}
				if(table.getSelectedRow() != currentPlaying) {
					b.setText("PLAY");
				b.setEnabled(true);
				} else if(table.getSelectedRow() == -1 && currentPlaying == -1)
					b.setEnabled(false);
				else {
					b.setText("STOP");
					b.setEnabled(true);
				}
			}
		});
		JButton b1 = new JButton("Refresh");
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new NetworkInfoThread(Client.this.hostname, Client.this.port, channels, Client.this.table).start();
			}
		});
		setLayout(new BorderLayout());
		model = new ChannelsTableModel(channels);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(table.getSelectedRow() != currentPlaying) {
					b.setText("PLAY");
				b.setEnabled(true);
				} else if(table.getSelectedRow() == -1 && currentPlaying == -1)
					b.setEnabled(false);
				else {
					b.setText("STOP");
					b.setEnabled(true);
				}
			}
		});
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(b, BorderLayout.SOUTH);
		add(b1, BorderLayout.NORTH);
		setSize(300, 300);
		setVisible(true);
		new NetworkInfoThread(hostname, port, channels, table).start();
	}
	
	public AudioFormat getAudioFormat() {
		ChannelInfo channel = channels.get(table.getSelectedRow());
		return new AudioFormat(channel.getEncoding(), channel.getFrameRate(), 16, 2, channel.getFrameSize(), channel.getFrameRate(), false);
	}

	public static void main(String[] args) throws IOException {
		if(args.length == 0)
			new Client("127.0.0.1", 8000);
		else
			new Client(args[0], Integer.parseInt(args[1]));
	}
}
