package edu.hm.dako.echo.udp;

import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;

import java.io.IOException;
import java.net.SocketException;

public class UdpServerSocket implements ServerSocket {

    private UdpSocket socket;
    
    public UdpServerSocket(int serverPort, int sendBufferSize, int receiveBufferSize) throws SocketException {
    	this.socket = new UdpSocket(serverPort, sendBufferSize, receiveBufferSize);
    }

    @Override
    public Connection accept() throws Exception {
        return new UdpServerConnection(socket);
    }

    @Override    
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}
