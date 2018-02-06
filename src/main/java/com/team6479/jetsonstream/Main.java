package com.team6479.jetsonstream;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Main implements Runnable {
	private static int portNumber = 1183;
	private JFrame frame;
	private BufferedImage img;
	private JLabel label;

	public static void main(String[] args) {
		Main obj = new Main();
		boolean isStopped = false;
		SwingUtilities.invokeLater(obj);
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("Image receiving server started.");
			while (!isStopped) {
				Socket s = serverSocket.accept();
				System.out.println("Accepting incoming connection.");
				new ClientHandler(obj,s).start();
			}
			serverSocket.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		frame = new JFrame("Received Image");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(800, 600));
		frame.setLayout(new FlowLayout());
		frame.pack();
		label = new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		if (img!=null) {
			label.setIcon(new ImageIcon(img));
		}
		frame.add(label);
		frame.setVisible(true);
	}

	public void updateImage(BufferedImage newimage) {
		System.out.println("Updating Image");
		label.setIcon(new ImageIcon(newimage));
		frame.repaint();
	}

}