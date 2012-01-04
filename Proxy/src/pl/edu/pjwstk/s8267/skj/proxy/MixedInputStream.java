package pl.edu.pjwstk.s8267.skj.proxy;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MixedInputStream extends DataInputStream {

	public MixedInputStream(InputStream in) {
		super(in);
	}
	
	public String readLine2() throws IOException {
		StringBuilder sb = new StringBuilder("");
		byte c;
		while((c = readByte()) != '\r') {
			sb.append((char)c);
		}
		return sb.toString().trim();
	}

}
