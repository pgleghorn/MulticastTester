package com.oracle.support;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * @author Phil Gleghorn
 *
 */
public class MulticastTester {

	private void doWork(InetAddress groupip, InetAddress ifip, int port,
			int ttl, String msg, long sleep) {
		try {
			MulticastSocket s = new MulticastSocket(port);
			s.joinGroup(groupip);
			s.setInterface(ifip);
			s.setTimeToLive(ttl);

			System.out.println("Transmitting \"" + msg + "\" every " + sleep
					+ "ms from interface " + ifip.getHostAddress() + " to multicast group " + groupip.getHostAddress() + ":" + port + " with ttl=" + ttl);
			while (true) {
				DatagramPacket data = new DatagramPacket(msg.getBytes(),
						msg.length(), groupip, port);
				s.send(data);

				byte[] buffer = new byte[10 * 1024];
				data = new DatagramPacket(buffer, buffer.length);

				s.receive(data);
				String str = new String(buffer).replaceAll("[^\\p{Print}]", "");  // strip gibberish
				System.out.println("Received....: " + str);
//						+ (new String(buffer, 0, data.getLength())));
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length != 6) {
			System.out.println("Multicast Tester, phil.gleghorn@oracle.com");
			System.out
					.println("Usage: MulticastTester <groupip> <interfaceip> <port> <ttl> <message> <sleep>");
			
			System.out
					.println(" e.g.: MulticastTester 230.0.0.0 192.168.0.5 5544 4 \"Hello whirled\" 5000");
			System.out.println("");
			System.out.println("Args were: ");
			for (int i = 0; i < args.length; i++) {
				System.out.println("arg[" + i + "] = " + args[i]);
			}

			System.exit(0);
		}
		MulticastTester mt = new MulticastTester();
		try {
			mt.doWork(InetAddress.getByName(args[0]),
					InetAddress.getByName(args[1]), Integer.parseInt(args[2]),
					Integer.parseInt(args[3]), args[4], Long.parseLong(args[5]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

}
