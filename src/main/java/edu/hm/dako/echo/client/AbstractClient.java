package edu.hm.dako.echo.client;


import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.common.SharedClientStatistics;
import edu.hm.dako.echo.connection.ConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basis fuer konkrete Client-Implementierungen.
 */
public abstract class AbstractClient implements Runnable {

    private static Log log = LogFactory.getLog(AbstractClient.class);

    protected String threadName;

    protected int clientNumber;

    protected int messageLength;

    protected int numberOfMessagesToSend;

    protected int serverPort;

    protected int localPort;

    protected String remoteServerAddress;
    
    protected int responseTimeout;
    
    protected int numberOfRetries;

    /**
     * Denkzeit des Clients zwischen zwei Requests in ms
     */
    protected int clientThinkTime;

    /**
     * Gemeinsame Daten der Threads
     */
    protected SharedClientStatistics sharedData;

    protected ConnectionFactory connectionFactory;

    /**
     * @param serverPort             Port des Servers
     * @param remoteServerAddress    Adresse des Servers
     * @param clientNumber           Laufende Nummer des Test-Clients
     * @param messageLength          Laenge einer Nachricht
     * @param numberOfMessagesToSend Anzahl zu sendender Nachrichten je Thread
     * @param clientThinkTime        Denkzeit des Test-Clients
     * @param sharedData             Gemeinsame Daten der Threads
     * @param connectionFactory      Der zu verwendende Client
     */
    public AbstractClient(int serverPort, String remoteServerAddress, int clientNumber, int messageLength,
                          int numberOfMessagesToSend, int clientThinkTime, 
                          int numberOfRetries, int responseTimeout, 
                          SharedClientStatistics sharedData,
                          ConnectionFactory connectionFactory) {
        this.serverPort = serverPort;
        this.remoteServerAddress = remoteServerAddress;
        this.clientNumber = clientNumber;
        this.messageLength = messageLength;
        this.numberOfMessagesToSend = numberOfMessagesToSend;
        this.clientThinkTime = clientThinkTime;
        this.numberOfRetries = numberOfRetries;
        this.responseTimeout = responseTimeout;
        this.sharedData = sharedData;
        Thread.currentThread().setName("EchoClient-" + String.valueOf(clientNumber + 1));
        threadName = Thread.currentThread().getName();
        this.connectionFactory = connectionFactory;
    }

    /**
     * Synchronisation mit allen anderen Client-Threads:
     * Warten, bis alle Clients angemeldet sind und dann
     * erst mit der Lasterzeugung beginnen
     *
     * @throws InterruptedException falls sleep unterbrochen wurde
     */
    protected void waitForOtherClients() throws InterruptedException {
        sharedData.getStartSignal().countDown();
        sharedData.getStartSignal().await();
    }

    /**
     * Aufbau einer EchoPDU
     *
     * @param messageNumber Fortlaufende Nachrichtennummer 
     */
    protected EchoPDU constructEchoPDU(int messageNumber) {
        EchoPDU pdu = new EchoPDU();
        pdu.setClientName(Thread.currentThread().getName());
        
        // Nachrichtenzaehler wird als Folgenummer benutzt
        pdu.setSequenceNumber(messageNumber);
        
        String echoMsg = "";
        for (int j = 0; j < messageLength; j++) {
            echoMsg += "+";
        }
        pdu.setMessage(echoMsg);

        // Letzter Request?
        if (messageNumber == (numberOfMessagesToSend - 1)) {
            pdu.setLastRequest(true);
        }
        return pdu;
    }

    /**
     * Nacharbeit nach Empfang einer EchoPDU vom Server
     *
     * @param messageNumber Fortlaufende Nachrichtennummer
     * @param receivedPDU Empfangene PDU
     * @param rtt Round Trip Time fuer den Echo-Request
     */
    protected final void postReceive(int messageNumber, EchoPDU receivedPdu, long rtt) {
        // Response-Zaehler erhoehen
        sharedData.incrReceivedMsgCounter(clientNumber, rtt, receivedPdu.getServerTime());

        log.debug(threadName + ": RTT fuer Request " + (messageNumber + 1) + ": " + rtt + " ns");
        log.debug(threadName + ": Echo-Nachricht empfangen von  " + receivedPdu.getServerThreadName() + ":"
                + receivedPdu.getMessage());
        log.debug(Thread.currentThread().getName() + ", Benoetigte Serverzeit: " + receivedPdu.getServerTime()  + " ns");
    }
}