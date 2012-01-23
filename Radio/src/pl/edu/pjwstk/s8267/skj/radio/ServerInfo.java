package pl.edu.pjwstk.s8267.skj.radio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;

public class ServerInfo extends Thread {

	private Socket client;
	private boolean login;
	private Vector<RadioChannel> radioChannels;

	public ServerInfo(Socket client, Vector<RadioChannel> radioChannels) {
		this.client = client;
		this.radioChannels = radioChannels;
	}

	@Override
	public void run() {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			bw.write("NetRadio v1.0\n");
			bw.flush();
			String line;
			while((line = br.readLine()) != null) {
				if(line.equals("LOGIN")) {
					bw.write("Username:\n");
					bw.flush();
					String username = br.readLine();
					bw.write("Password:\n");
					bw.flush();
					String password = br.readLine();
					if(username.equals("user") && password.equals("pass")) {
						bw.write("HELLO "+username+"\n");
						login = true;
					} else {
						bw.write("LOGINERROR\n");
						login = false;
					}
					bw.flush();
				} else if(line.equals("LISTCHANNELS")) {
					if(login) {
						bw.write("CHANNELS\n");
						for(int i = 0; i < radioChannels.size(); i++) {
							RadioChannel rc = radioChannels.get(i);
							String encoding = null;
							if(rc.getEncoding() == AudioFormat.Encoding.ALAW)
								encoding = "ALAW";
							else if(rc.getEncoding() == AudioFormat.Encoding.PCM_FLOAT)
								encoding = "PCM_FLOAT";
							else if(rc.getEncoding() == AudioFormat.Encoding.PCM_SIGNED)
								encoding = "PCM_SIGNED";
							else if(rc.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED)
								encoding = "PCM_UNSIGNED";
							else if(rc.getEncoding() == AudioFormat.Encoding.ULAW)
								encoding = "ULAW";
							bw.write(rc.getChannelName()+"\t"+(10000+i)+"\t"+rc.getFrameRate()+"\t"+rc.getFrameSize()+"\t"+encoding+"\n");
						}
						bw.write("END CHANNELS\n");
					} else {
						bw.write("LOGINERROR\n");
					}
					bw.flush();
				} else {
					bw.write("BYE\n");
					bw.flush();
					client.close();
					return;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

}
