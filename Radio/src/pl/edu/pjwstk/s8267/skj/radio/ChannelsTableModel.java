package pl.edu.pjwstk.s8267.skj.radio;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ChannelsTableModel extends DefaultTableModel {
	private Vector<ChannelInfo> channels;
	public ChannelsTableModel(Vector<ChannelInfo> channels) {
		this.channels = channels;
		addColumn("Nazwa");
		addColumn("FrameRate");
		addColumn("FrameSize");
	}
	public int getRowCount() {
		if(channels == null)
			return 0;
		return channels.size();
	};
	public Object getValueAt(int row, int column) {
		switch(column) {
		case 0:
			return channels.get(row).getChannelName();
		case 1:
			return channels.get(row).getFrameRate();
		case 2:
			return channels.get(row).getFrameSize();
		}
		return null;
	};
}
