package pl.edu.pjwstk.s8267.skj.radio;

public class ChannelInfo {

	private int frameSize;
	private float frameRate;
	private String channelName;
	private int port;

	public ChannelInfo(String line) {
		String[] data = line.split("\t");
		channelName = data[0];
		port = Integer.parseInt(data[1]);
		frameRate = Float.parseFloat(data[2]);
		frameSize = Integer.parseInt(data[3]);
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
	
	
	
}
