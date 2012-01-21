package pl.edu.pjwstk.s8267.skj.radio;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class RadioChannel extends Thread {

	private int port;
	private File channelDirectory;
	private float frameRate;
	private int frameSize;

	public RadioChannel(File channelDirectory, int port) {
		this.port = port;
		this.channelDirectory = channelDirectory;
	}
	
	@Override
	public void run() {
		try {
			MulticastSocket ds = new MulticastSocket();
			//InetAddress group = InetAddress.getByName("230.0.0.1");
			InetAddress group = InetAddress.getByName("127.0.0.1");
			AudioInputStream stream;
			int i = 0;
			File[] files = channelDirectory.listFiles();
			while(true) {
	            stream = AudioSystem.getAudioInputStream(files[i]);
	            frameRate = stream.getFormat().getFrameRate();
	            frameSize = stream.getFormat().getFrameSize();
	            //Clip c = AudioSystem.getClip();
	            //c.open(stream);
	            //c.start();
	            int size = (int) (frameRate*frameSize/4);
	            byte[] buffer = new byte[size];
	            while(true) {
	            	int len = stream.read(buffer);
	            	if(len == -1)
	            		break;
					ds.send(new DatagramPacket(buffer, len, group, port));
					try {
						Thread.sleep(8);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	            i++;
	            if(i >= files.length)
	            	i = 0;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public float getFrameRate() {
		return frameRate;
	}
	
	public int getFrameSize() {
		return frameSize;
	}
	
	public String getChannelName() {
		return channelDirectory.getName();
	}

}
