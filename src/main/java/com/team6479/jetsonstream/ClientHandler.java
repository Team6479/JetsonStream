package com.team6479.jetsonstream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

public class ClientHandler extends Thread {
	private Main main = null;
	private Socket socket = null;

	public ClientHandler(Main m, Socket s) {
		main = m;
		socket = s;
	}

	public void run() {
		try {
			//System.out.println("Client handler thread started.");
			DataInputStream in = new DataInputStream(socket.getInputStream());
			//reuse these
			byte[] buf = new byte[262144];
			ByteArrayInputStream arrayStream = new ByteArrayInputStream(buf);
			while (true) {
				BufferedImage img = null;
				try {
					int read = in.read(buf);
					// make sure we read till end of image
					while (read> 0 && buf[read-2] != -1 && buf[read-1] != -39) {
						System.out.println("Attempting to read until EOI: "+ read+":"+ buf[read-2]+" "+buf[read-1]);
						if (in.available()>0) {
							read = in.read(buf,read,262144-read);
							System.out.println("After attempting to read until EOI: "+read+":"+ buf[read-2]+" "+buf[read-1]);
						} else {
							break;
						}
					}
					arrayStream.read(buf, 0, 262144);
					// EOI found
					img=ImageIO.read(arrayStream);
					if (img!=null) {
						main.updateImage(img);
					}
					if (socket.isConnected()) {
						socket.getOutputStream().write(0x01);
						socket.getOutputStream().flush();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					break;
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			try {
				socket.close();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
