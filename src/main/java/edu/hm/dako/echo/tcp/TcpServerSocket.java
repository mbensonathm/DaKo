package edu.hm.dako.echo.tcp;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;

import java.io.IOException;
import java.net.BindException;

public class TcpServerSocket implements ServerSocket {

    private static java.net.ServerSocket serverSocket;
    int sendBufferSize;
    int receiveBufferSize;

    /**
     * Erzeugt ein TCP-Serversocket und bindet es an einen Port.
     *
     * @param  port Portnummer, die verwendet werden soll
     * @param  sendBufferSize Groesse des Sendepuffers in Byte
     * @param  receiveBufferSize Groesse des Empfangspuffers in Byte
     * @exception  BindException Port schon belegt
     * @exception  IOException I/O-Fehler bei der Dovket-Erzeugung
     */
    public TcpServerSocket(int port,  int sendBufferSize, int receiveBufferSize) throws BindException, IOException {
    	this.sendBufferSize = sendBufferSize;
    	this.receiveBufferSize= receiveBufferSize;
    	try {
    	   serverSocket = new java.net.ServerSocket(port);
       } catch (BindException e) {
			System.out.println("Port " + port +" auf dem Rechner schon in Benutzung, Bind Exception: "+ e);
			throw e;
       } catch (IOException e) {
    	   System.out.println("Schwerwiegender Fehler beim Anlegen einesTCP-Sockets mit Portnummer " + port + ": " + e);
    	   throw e;
       }
    }

    @Override
    public Connection accept() throws IOException {
        return new TcpConnection(serverSocket.accept(), sendBufferSize, receiveBufferSize, false, true);
    }

    @Override
    public void close() throws IOException {
    	System.out.println("Serversocket wird geschlossen, lokaler Port: " 
        		+ serverSocket.getLocalPort());
        serverSocket.close();
    }

    @Override
    public boolean isClosed() {
        return serverSocket.isClosed();
    }
}
