package edu.hm.dako.echo.udp;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ServerSocket;
import edu.hm.dako.echo.server.EchoServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class UdpEchoServerImpl implements EchoServer {

    private static Log log = LogFactory.getLog(UdpEchoServerImpl.class);

    private final ExecutorService executorService;

    private ServerSocket socket;

    public UdpEchoServerImpl(ExecutorService executorService, ServerSocket socket) {
        this.executorService = executorService;
        this.socket = socket;
    }

    @Override
    public void start() {
        PropertyConfigurator.configureAndWatch("log4j.server.properties", 60 * 1000);
        System.out.println("Echoserver wartet auf Clients...");
        while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
            try {
                // Auf ankommende Verbindungsaufbauwuensche warten
                Connection connection = socket.accept();

                // Neuen Workerthread starten
                executorService.submit(new EchoWorker(connection));
            } catch (Exception e) {
                log.error("Exception beim Entgegennehmen von Verbindungswuenschen: " + e);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("EchoServer beendet sich");
        Thread.currentThread().interrupt();
        socket.close();
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("Das Beenden des ExecutorService wurde unterbrochen");
            e.printStackTrace();
        }
    }

    private class EchoWorker implements Runnable {

        private final Connection con;

        long startTime;

        private EchoWorker(Connection con) {
            this.con = con;
        }

        @Override
        public void run() {
            try {
                echo();
            } catch (Exception e) {
            	System.out.println(Thread.currentThread().getName() + 
            			 " Verbindung wird geschlossen wegen schwerwiegenden Problemen: " + e);
                closeConnection();
                throw new RuntimeException(e);
            }
            log.debug(Thread.currentThread().getName() + " hat EchoRequest bearbeitet");
            closeConnection();
        }

        private void echo() throws Exception {   
            EchoPDU receivedPdu = (EchoPDU) con.receive();
            startTime = System.nanoTime();
            con.send(EchoPDU.createServerEchoPDU(receivedPdu, startTime));
            if (receivedPdu.getLastRequest()) {
                log.debug("Letzter Request des Clients " + receivedPdu.getClientName());
            }
        }

        private void closeConnection() {          
        	try {
                con.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
