package edu.hm.dako.echo.server;

import edu.hm.dako.echo.benchmarking.UserInterfaceInputParameters;
import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.LoggingConnectionDecorator;
import edu.hm.dako.echo.connection.ServerSocket;
import edu.hm.dako.echo.tcp.TcpEchoServerImpl;
import edu.hm.dako.echo.tcp.TcpServerSocket;
import edu.hm.dako.echo.udp.UdpEchoServerImpl;
import edu.hm.dako.echo.udp.UdpServerSocket;

import java.util.concurrent.Executors;

/**
 * Uebernimmt die Konfiguration und Erzeugung bestimmter Server-Typen.
 * Siehe {@link edu.hm.dako.echo.benchmarking.UserInterfaceInputParameters.ImplementationType}
 * Dies beinhaltet Art des Servers und Konfiguration dessen Thread-Pool.
 */
public final class ServerFactory {

    private static final int DEFAULT_SERVER_PORT = 50000; // Standard-Port des Servers
   
    /*
     * Die Groesse des Empfangspuffers ist fuer den UDP-Server sehr wichtig. 
     * Um viele parallele Client-Threads zu bedienen, sollte der Empfangspuffer im Server gut ausgetestet werden,
     * also so gross wie noetig sein, aber nicht zu gross, da sonst Speicher verschwendet wird
     * (Testerfahrung bei UDP-Lasttests), da alle Requests ueber ein Socket empfangen werden. Bei der TCP-
     * Loesung ist das nicht so problematisch, da jeder Client ueber eine eigene Verbindung, also ueber ein eigenes 
     * Socket mit eigenem Empfangspuffer bedient wird.
     */
    private static final int SERVER_SEND_BUFFER_SIZE = 20000; // Sendepuffer des Servers in Byte
    private static final int SERVER_RECEIVE_BUFFER_SIZE = 500; // Empfangspuffer des Servers in Byte.

    
    private ServerFactory() {
    }

    public static EchoServer getServer(UserInterfaceInputParameters.ImplementationType type)
            throws Exception {
    	
        System.out.println("Echoserver " + type.toString() + " wird gestartet");
        
        switch (type) {
            case TCPMultiThreaded:
                return new TcpEchoServerImpl(Executors.newCachedThreadPool(), getDecoratedServerSocket(
                        new TcpServerSocket(DEFAULT_SERVER_PORT,
                        					SERVER_SEND_BUFFER_SIZE, 
                        					SERVER_RECEIVE_BUFFER_SIZE)));
            case UDPMultiThreaded:
                return new UdpEchoServerImpl(Executors.newCachedThreadPool(), getDecoratedServerSocket(
                        new UdpServerSocket(DEFAULT_SERVER_PORT, 
                        					SERVER_SEND_BUFFER_SIZE, 
                        					SERVER_RECEIVE_BUFFER_SIZE)));
            default:
                throw new RuntimeException("Unknown type: " + type);
        }
    }
      
    private static ServerSocket getDecoratedServerSocket(ServerSocket serverSocket) {
        return new DecoratingServerSocket(serverSocket);
    }

    /**
     * Startet den ausgewaehlten Server.
     * Muss aufgerufen werden, bevor ein Test ueber die GUI gestartet werden kann.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

    	/* Hinweis:
         * Im ImplementationType der naechsten Anweisungen muss der Server, 
    	 * der gestartet werden soll, angegeben werden
         */   	
    	//getServer(UserInterfaceInputParameters.ImplementationType.TCPMultiThreaded).start();
    	getServer(UserInterfaceInputParameters.ImplementationType.UDPMultiThreaded).start();

    }

    private static class DecoratingServerSocket implements ServerSocket {

        private final ServerSocket wrappedServerSocket;

        DecoratingServerSocket(ServerSocket wrappedServerSocket) {
            this.wrappedServerSocket = wrappedServerSocket;
        }

        @Override
        public Connection accept() throws Exception {
            return new LoggingConnectionDecorator(wrappedServerSocket.accept());
        }

        @Override
        public void close() throws Exception {
            wrappedServerSocket.close();
        }

        @Override
        public boolean isClosed() {
            return wrappedServerSocket.isClosed();
        }
    }
}
