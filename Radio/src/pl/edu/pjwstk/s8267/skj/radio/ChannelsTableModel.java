package pl.edu.pjwstk.s8267.skj.radio;

import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.swing.table.DefaultTableModel;

public class ChannelsTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3263373410470031908L;
	private Vector<ChannelInfo> channels;
	public ChannelsTableModel(Vector<ChannelInfo> channels) {
		this.channels = channels;
		addColumn("Nazwa");
		addColumn("FrameRate");
		addColumn("FrameSize");
		addColumn("Encoding");
	}
	public int getRowCount() {
		if(channels == null)
			return 0;
		return channels.size();
	};
	public Object getValueAt(int row, int column) {
		if(row >= channels.size())
			return null;
		switch(column) {
		case 0:
			return channels.get(row).getChannelName();
		case 1:
			return channels.get(row).getFrameRate();
		case 2:
			return channels.get(row).getFrameSize();
		case 3:
			AudioFormat.Encoding encoding = channels.get(row).getEncoding();
			if(encoding == AudioFormat.Encoding.ALAW)
				return "ALAW";
			else if(encoding == AudioFormat.Encoding.PCM_FLOAT)
				return "PCM_FLOAT";
			else if(encoding == AudioFormat.Encoding.PCM_SIGNED)
				return "PCM_SIGNED";
			else if(encoding == AudioFormat.Encoding.PCM_UNSIGNED)
				return "PCM_UNSIGNED";
			else if(encoding == AudioFormat.Encoding.ULAW)
				return "ULAW";
		}
		return null;
	};
}
