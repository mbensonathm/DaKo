package edu.hm.dako.echo.udp;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;

import java.net.InetAddress;



public class UdpClientConnectionFactory implements ConnectionFactory {

	// Maximale Wartezeit beim Empfang einer Nachricht in Millisekunden.
	// Wenn in dieser Zeit keine Nachricht kommt, wird das Empfangen abgebrochen.
	// Mit verschiedenen Einstellungen experimentieren.
	private final int defaultResponseTimeout = 5000;
	
    @Override
    public Connection connectToServer(String remoteServerAddress, int serverPort, int localPort, int sendBufferSize, int receiveBufferSize) throws Exception {
    	UdpSocket udpSocket = new UdpSocket(localPort, sendBufferSize, receiveBufferSize);
        udpSocket.setRemoteAddress(InetAddress.getByName(remoteServerAddress));
        udpSocket.setRemotePort(serverPort);
        return new UdpClientConnection(udpSocket, defaultResponseTimeout);
    }
}
