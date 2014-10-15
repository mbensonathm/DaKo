package edu.hm.dako.echo.common;

import java.io.Serializable;

/**
 * Klasse EchoPDU
 * <p/>
 * Dient der Uebertragung einer Echo-Nachricht (Request und Response)
 *
 * @author Mandl
 */
public class EchoPDU implements Serializable {
    private static final long serialVersionUID = -6172619032079227584L;

    private String clientName;       // Name des Client-Threads, der den Request absendet
    private String serverThreadName; // Name des Threads, der den Request im Server bearbeitet
    private long sequenceNumber;	 // Zaehlt die uebertragenen Nachrichten eines Clients
    private String message;          // Echo-Nachricht (eigentliche Nachricht in Textform)
    boolean lastRequest;             // Kennzeichen, ob Client letzte Nachricht sendet. 
    								 // Dieses Kennzeichen dient dem Server dazu, um festzustellen, 
     								 // ob sich der Client nach der Nachricht beendet
    private long serverTime; 		 // Zeit in Nanosekunden, die der Server benoetigt. Diese
    								 // Zeit wird vom Server vor dem Absenden der Response eingetragen.

    public EchoPDU() {
        clientName = null;
        serverThreadName = null;
        sequenceNumber = 0;
        message = null;
        serverTime = 0;
        lastRequest = false;
    }

    public static EchoPDU createServerEchoPDU(EchoPDU receivedPdu, long startTime) {
        EchoPDU echoPDU = new EchoPDU();
        echoPDU.setServerThreadName(Thread.currentThread().getName());
        echoPDU.setClientName(receivedPdu.getClientName());
        
        // Nachrichtenzaehler des Clients wird als Sequenznummer benutzt und in die Response-PDU uebernommen
        echoPDU.setSequenceNumber(receivedPdu.getSequenceNumber());
        
        echoPDU.setMessage(receivedPdu.getMessage());
        echoPDU.setServerTime(System.nanoTime() - startTime);
        return echoPDU;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }

    public void setServerThreadName(String name) {
        this.serverThreadName = name;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public void setServerTime(long time) {
        this.serverTime = time;
    }

    public void setLastRequest(boolean last) {
        this.lastRequest = last;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    public String getClientName() {
        return (clientName);
    }

    public String getServerThreadName() {
        return (serverThreadName);
    }

    public String getMessage() {
        return (message);
    }

    public long getServerTime() {
        return (serverTime);
    }

    public boolean getLastRequest() {
        return (lastRequest);
    }
    
    public long getSequenceNumber() {
        return (sequenceNumber);
    }
} 