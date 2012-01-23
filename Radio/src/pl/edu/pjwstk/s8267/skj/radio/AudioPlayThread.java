package pl.edu.pjwstk.s8267.skj.radio;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayThread extends Thread {
	private Client client;
	private int port;
	private boolean playing = true;
	public AudioPlayThread(Client c, int port) {
		this.client = c;
		this.port = port;
	}
	public void run() {
		SourceDataLine sourceDataLine = null;
		MulticastSocket socket = null;
		try {
			socket = new MulticastSocket(port);
			InetAddress address = InetAddress.getByName("230.0.0.1");
			socket.joinGroup(address);
			AudioFormat af = client.getAudioFormat();
			
			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, af);
			sourceDataLine = (SourceDataLine) AudioSystem
					.getLine(dataLineInfo);
			sourceDataLine.open(af);
			float frameRate = af.getFrameRate();
            int frameSize = af.getFrameSize();
            //Clip c = AudioSystem.getClip();
            //c.open(stream);
            //c.start();
            int size = (int) (frameRate*frameSize/4);
            byte[] buffer = new byte[size];
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            sourceDataLine.start();
			while (playing) {
				try {
					socket.receive(p);
					sourceDataLine.write(buffer, 0, buffer.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sourceDataLine.drain();
			sourceDataLine.stop();
			sourceDataLine.close();
			socket.close();
		}
	}
	
	public void stopPlaying() {
		playing = false;
	}
}
