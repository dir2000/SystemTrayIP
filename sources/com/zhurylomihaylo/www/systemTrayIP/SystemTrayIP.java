package com.zhurylomihaylo.www.systemTrayIP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SystemTrayIP {

	private TrayIcon trayIcon;

	public static void main(String[] args) {
		try {
			new SystemTrayIP().doAllActions();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), Messages.getString("SystemTrayIP.Ups"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}
	}

	void doAllActions() {
		if (!SystemTray.isSupported()) {
			throw new RuntimeException(Messages.getString("SystemTrayIP.SystemTrayIsNotSupported")); //$NON-NLS-1$
		}

		PopupMenu popup = new PopupMenu();
		MenuItem exitItem = new MenuItem(Messages.getString("SystemTrayIP.Exit")); //$NON-NLS-1$
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String optionYes = Messages.getString("SystemTrayIP.Yes"); //$NON-NLS-1$
				String optionNo = Messages.getString("SystemTrayIP.No"); //$NON-NLS-1$
				String [] options = {optionYes, optionNo};
				int result = JOptionPane.showOptionDialog(null, Messages.getString("SystemTrayIP.AreYouSureToExit"), null, JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
						JOptionPane.QUESTION_MESSAGE, null, options, optionYes);
				if (result == 0)
					System.exit(0);
			}
		});
		popup.add(exitItem);

		SystemTray tray = SystemTray.getSystemTray();

		Image image = new ImageIcon(getClass().getResource("ip_address.png")).getImage(); //$NON-NLS-1$

		trayIcon = new TrayIcon(image, Messages.getString("SystemTrayIP.LeftClickMouseToDetermineYourIPAddress"), popup); //$NON-NLS-1$
		trayIcon.setImageAutoSize(true);
		
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);
				if (arg0.getButton() == MouseEvent.BUTTON1)
					showIP();
			}
		});
		
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			throw new RuntimeException(Messages.getString("SystemTrayIP.TrayIconCouldNotBeAdded")); //$NON-NLS-1$
		}
	}

	private void showIP() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002); //$NON-NLS-1$
			String ip = socket.getLocalAddress().getHostAddress();
			trayIcon.displayMessage(Messages.getString("SystemTrayIP.MyIPAddress"), ip, TrayIcon.MessageType.INFO); //$NON-NLS-1$
		} catch (SocketException | UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}
