package edu.hm.dako.echo.tcp;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.Socket;


public class TcpConnectionFactory implements ConnectionFactory {

	// Test: Zaehlt die Verbindungsaufbauversuche, bis eine Verbindung vom Server angenommen wird
	private long connectionTryCounter = 0;

	public Connection connectToServer(String remoteServerAddress,
			int serverPort, int localPort, int sendBufferSize, int receiveBufferSize) throws IOException {

		TcpConnection connection = null;
		boolean connected = false;
		InetAddress localAddress = null; // Es wird der "localhost" fuer die lokale IP-Adresse verwendet
			
		while (!connected) {
			try {
				//Test
				connectionTryCounter++;
				
				connection = new TcpConnection(new Socket(remoteServerAddress, serverPort, localAddress, localPort), 
					sendBufferSize, receiveBufferSize, false, true);
				connected = true;
	
			} catch (BindException e) {
				
				// Lokaler Port schon verwendet
				System.out.println("BindException beim Verbindungsaufbau: " + e.getMessage());			
				// try again
				
			} catch (IOException e) {
				
				//System.out.println("IOException beim Verbindungsaufbau: " + e.getMessage());			
				// try again
			
			} catch (Exception e) {
				
				// Test
				//System.out.println("Sonstige Exception beim Verbindungsaufbau " + e.getMessage());			
				// try again
			}
		}

		// Test
		System.out.println("Verbindungsaufbauversuche bis die Verbindung zum Server stand: " + connectionTryCounter);	
		return connection;
	}

}
