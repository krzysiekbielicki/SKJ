package pl.edu.pjwstk.s8267.skj.radio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class ChannelInfo {

	private int frameSize;
	private float frameRate;
	private String channelName;
	private int port;
	private Encoding encoding;

	public ChannelInfo(String line) {
		String[] data = line.split("\t");
		channelName = data[0];
		port = Integer.parseInt(data[1]);
		frameRate = Float.parseFloat(data[2]);
		frameSize = Integer.parseInt(data[3]);
		String e = data[4];
		if(e.equals("ALAW"))
			encoding = AudioFormat.Encoding.ALAW;
		else if(e.equals("PCM_FLOAT"))
			encoding = AudioFormat.Encoding.PCM_FLOAT;
		else if(e.equals("PCM_SIGNED"))
			encoding = AudioFormat.Encoding.PCM_SIGNED;
		else if(e.equals("PCM_UNSIGNED"))
			encoding = AudioFormat.Encoding.PCM_UNSIGNED;
		else if(e.equals("ULAW"))
			encoding = AudioFormat.Encoding.ULAW;
	}

	public int getFrameSize() {
		return frameSize;
	}

	public float getFrameRate() {
		return frameRate;
	}

	public String getChannelName() {
		return channelName;
	}

	public int getPort() {
		return port;
	}
	
	public Encoding getEncoding() {
		return encoding;
	}
	
}
