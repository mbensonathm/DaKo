package edu.hm.dako.echo.udp;

import edu.hm.dako.echo.common.EchoPDU;
import edu.hm.dako.echo.common.ExceptionHandler;
import edu.hm.dako.echo.common.SharedClientStatistics;
import edu.hm.dako.echo.connection.Connection;
import edu.hm.dako.echo.connection.ConnectionFactory;
import edu.hm.dako.echo.client.AbstractClient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.SocketTimeoutException;

/**
 * Erzeugt fuer einen Client-Thread ein UDP-Socket ueber den eine logische
 * Verbindung zum Server aufgebaut wird. Ueber diese logische Verbindung werden
 * alle Echo-Nachrichten gesendet. Nachdem alle Nachrichten versendet wurden,
 * wird die logische Verbindung wieder abgebaut.
 */
public class UdpEchoClientImpl extends AbstractClient {

	final static int LOCAL_SEND_BUFFER_SIZE = 4096;
	final static int LOCAL_RECEIVE_BUFFER_SIZE = 4096;

	private static Log log = LogFactory.getLog(UdpEchoClientImpl.class);

	private Connection connection;

	public UdpEchoClientImpl(int serverPort, String remoteServerAddress,
			int numberOfClient, int messageLength, int numberOfMessages,
			int clientThinkTime, int numberOfMaxRetries, int responseTimeout,
			SharedClientStatistics sharedData,
			ConnectionFactory connectionFactory) {
		super(serverPort, remoteServerAddress, numberOfClient, messageLength,
				numberOfMessages, clientThinkTime, numberOfMaxRetries,
				responseTimeout, sharedData, connectionFactory);
	}

	/**
	 * Client-Thread sendet hier alle Requests und wartet auf Antworten
	 */
	@Override
	public void run() {
		// TODO:Studienarbeit - Methode ab hier vervollstaendigen
		Thread.currentThread().setName("Client-Thread" + clientNumber);
		try {
			waitForOtherClients();
			localPort = 0;
			UdpClientConnectionFactory fac = new UdpClientConnectionFactory();
			connection = fac.connectToServer(remoteServerAddress, serverPort,
					localPort, LOCAL_SEND_BUFFER_SIZE, LOCAL_RECEIVE_BUFFER_SIZE);
			for (int i = 0; i < numberOfMessagesToSend; i++) {
				try {
					doEcho(i);
				} catch (SocketTimeoutException e) {
					log.debug(e.getMessage());
				}
			}
		} catch (Exception e) {
			ExceptionHandler.logExceptionAndTerminate(e);
		} finally {
            try {
                connection.close();
            } catch (Exception e) {
                ExceptionHandler.logException(e);
            }
        }
		// Testausgabe
		System.out.println("Anzahl aller Mehrfachsendungen von Thread: "
				+ Thread.currentThread().getName() + ": "
				+ sharedData.getNumberOfRetries(clientNumber));
	}

	/**
	 * Sendet einen EchoRequest und wartet, bis eine Antwort vom Server
	 * empfangen wird. Wird die Antwort nicht in einer vorgegebenen Zeit
	 * empfangen, so wird erneut eine EchoRequest gesendet. Erst nach mehreren
	 * erfolglosen Versuchen wird der Vorgang abgebrochen.
	 *
	 * In dieser Methode sind einfache Massnahmen zur Erhoehung der
	 * Uebertragungssicherheit als Erweiterung zum klassischen UDP
	 * implementiert:
	 *
	 * - Timerueberwachuung zur Begrenzung der Wartezeit auf einen EchoResponse.
	 * - Uebertragungswiederholung nach Ablauf der Wartezeit (Anzahl
	 * Wiederholungen ist konfigurierbar). - Erkennung von Duplikaten bei
	 * EchoResponse-PDUs durch eine einfache Folgenummer (Sequence Number ohne
	 * Ueberlaufcheck) und Verwerfen der Duplikate.
	 *
	 */
	private void doEcho(int i) throws Exception {
		// RTT-Startzeit ermitteln
		long rttStartTime = System.nanoTime();
		sharedData.incrSentMsgCounter(clientNumber);
		// Send and try to receive PDU
		EchoPDU receivedPdu = attemptSendReceivePDU(i, 0);
		// Calculate Roundtrip Time 
		long rtt = System.nanoTime() - rttStartTime;
		// Log result
		postReceive(i, receivedPdu, rtt);
		// Denkzeit
		Thread.sleep(clientThinkTime);
	}

	private EchoPDU attemptSendReceivePDU(int i, int retries) throws Exception{
		// Send PDU
		connection.send(constructEchoPDU(i));
		// Capture response
		Object rsp = connection.receive(responseTimeout);
		
		if (rsp != null) //Successful, return response
		{
			return (EchoPDU) rsp;
		}
		else if (rsp == null && retries < numberOfRetries) // Unsuccessful, try again
		{
			return attemptSendReceivePDU(i, retries++);
		}
		else // Number of attempts up, throw exception
		{
			// TODO: Check German!"
			throw new Exception("Die Maximale Anzahl von Nachrichten wurde erreicht");
		}

	}
}

