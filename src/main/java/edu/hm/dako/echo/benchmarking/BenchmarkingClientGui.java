/**
 * 
 * Sehr einfaches und noch nicht ausgereiftes Swing-basiertes GUI 
 * fuer den Anstoss von Testlaeufen im Benchmarking-Client
 *
 *  Ein Refactoring und eine Fehlerbereinigung (nicht Bestandteil der Studienarbeit) ist 
 *  noch durchzufuehren:
 *  - Buttons an der Oberflaeche kleiner machen
 *  - Pruefen, ob alle Eingabeparameter ordnungsgemaess erfasst wurden (Plausibilitaetspruefung)
 * 	- Namensgebung fuer Variablen verbessern
 * 	- Compiler-Warnungs entfernen
 * 	- ImplementationType und MeasurementType besser erfassen (redundanten Code vermeiden)
 * 	- Problem bei langen Tests - GUI hat nach Fensterwechsel nicht mehr den Fokus - beseitigen
 * 	- Fehlerbereinigung insgesamt
 */
package edu.hm.dako.echo.benchmarking;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import edu.hm.dako.echo.benchmarking.UserInterfaceInputParameters.ImplementationType;
import edu.hm.dako.echo.benchmarking.UserInterfaceInputParameters.MeasurementType;

import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.Formatter;

public class BenchmarkingClientGui extends JPanel
        implements BenchmarkingClientUserInterface, ActionListener {

    public static final String MULTI_THREADED_TCP = "MultiThreaded-TCP";
    public static final String MULTI_THREADED_UDP = "MultiThreaded-UDP";
    private long timeCounter = 0; // Zeitzaehler fuer Testlaufzeit

    private static JFrame frameEchoBenchmarkingGui; // Frame fuer Echo-Anwendungs-GUI
    private static JPanel panelBenchmarkingClientGui;

    /**
     * GUI-Komponenten
     */

    private JComboBox optionListImplType;
    private JComboBox optionListMeasureType;
    private JTextField textFieldNumberOfClientThreads;
    private JFormattedTextField textFieldAvgCpuUsage;
    private JFormattedTextField textFieldNumberOfMessagesPerClients;
    private JTextField textFieldServerport;
    private JTextField textFieldThinkTime;
    private JFormattedTextField textFieldServerIpAddress;
    private JFormattedTextField textFieldMessageLength; 
    private JFormattedTextField textFieldNumberOfMaxRetries;
    private JFormattedTextField textFieldResponseTimeout;
    private JFormattedTextField textFieldSeperator;
    private JFormattedTextField textFiledPlannedRequests;
    private JFormattedTextField textFieldTestBegin;
    private JFormattedTextField textFieldSentRequests;
    private JFormattedTextField textFieldTestEnd;
    private JFormattedTextField textFieldReceivedResponses;
    private JFormattedTextField textFieldTestDuration;
    private JFormattedTextField textFieldAvgRTT;
    private JFormattedTextField textFieldAvgServerTime;
    private JFormattedTextField textFieldMaxRTT;
    private JFormattedTextField textFieldMaxHeapUsage;
    private JFormattedTextField textFieldMinRTT;
    private JFormattedTextField textFieldNumberOfRetries; // Anzahl der Uebertragungswiederholungen

    private JTextArea messageArea;
    private JScrollPane scrollPane;

    private Button startButton;
    private Button newButton;
    private Button finishButton;

    private static final long serialVersionUID = 100001000L;
	private Formatter formatter;

    public BenchmarkingClientGui() {
        super(new BorderLayout());
    }

    private void initComponents() {

        /**
         * Erzeugen der GUI-Komponenten
         */
        String[] optionStrings = {
                MULTI_THREADED_TCP,
                MULTI_THREADED_UDP};
        optionListImplType = new JComboBox(optionStrings);

        String[] optionStrings1 = {
                "Variable Threads",
                "Variable Length"};
        optionListMeasureType = new JComboBox(optionStrings1);

        textFieldNumberOfClientThreads = new JTextField();
        //text = new JFormattedTextField();
        textFieldAvgCpuUsage = new JFormattedTextField();
        //textFieldNumberOfMessages = new JFormattedTextField();
        textFieldNumberOfMessagesPerClients = new JFormattedTextField();
        textFieldServerport = new JTextField();
        textFieldThinkTime = new JTextField();
        textFieldServerIpAddress = new JFormattedTextField();
        textFieldMessageLength = new JFormattedTextField();
        
        textFieldNumberOfMaxRetries = new JFormattedTextField();
        textFieldResponseTimeout = new JFormattedTextField();
        
        textFieldSeperator = new JFormattedTextField();
        textFiledPlannedRequests = new JFormattedTextField();
        textFieldTestBegin = new JFormattedTextField();
        textFieldSentRequests = new JFormattedTextField();
        textFieldTestEnd = new JFormattedTextField();
        textFieldReceivedResponses = new JFormattedTextField();
        textFieldTestDuration = new JFormattedTextField();
        textFieldAvgRTT = new JFormattedTextField();
        textFieldAvgServerTime = new JFormattedTextField();
        textFieldMaxRTT = new JFormattedTextField();
        textFieldMaxHeapUsage = new JFormattedTextField();
        textFieldMinRTT = new JFormattedTextField();   
        
        textFieldNumberOfRetries = new JFormattedTextField(); // Anzahl der Uebertragungswiederholungen

        // Nachrichtenbereich mit Scrollbar
        messageArea = new JTextArea("", 5, 100);

        //messageArea.setLineWrap(true);
        scrollPane = new JScrollPane(messageArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Buttons
        startButton = new Button("Starten");
        newButton = new Button("Neu");
        finishButton = new Button("Beenden");
    }

    public static void main(String[] args) {

        PropertyConfigurator.configureAndWatch("log4j.client.properties", 60 * 1000);

        try {
            //UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }

        frameEchoBenchmarkingGui = new JFrame("Benchmarking Client GUI");
        frameEchoBenchmarkingGui.setTitle("Benchmark");
        frameEchoBenchmarkingGui.add(new BenchmarkingClientGui());
        frameEchoBenchmarkingGui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JComponent panel = new BenchmarkingClientGui().buildPanel();
        frameEchoBenchmarkingGui.getContentPane().add(panel);
        frameEchoBenchmarkingGui.pack();
        frameEchoBenchmarkingGui.setVisible(true);
    }

    /**
     * buildPanel
     * Panel anlegen
     *
     * @return
     */
    public JComponent buildPanel() {

        initComponents();

        // Layout definieren
        FormLayout layout = new FormLayout(
                "right:max(40dlu;pref), 3dlu, 70dlu, 7dlu, " +
                        "right:max(40dlu;pref), 3dlu, 70dlu",
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
                        "p, 3dlu, p, 3dlu, p, 3dlu, p, 9dlu, " +
                        "p, 3dlu, p, 3dlu, " +
                        "p, 3dlu, p, 3dlu, p");

        panelBenchmarkingClientGui = new JPanel(layout);
        panelBenchmarkingClientGui.setBorder(Borders.DIALOG_BORDER);

        /*
         *  Panel mit Labels und Komponenten fuellen
         */

        CellConstraints cc = new CellConstraints();
        panelBenchmarkingClientGui.add(createSeparator("Eingabeparameter"), cc.xyw(1, 1, 7));
        panelBenchmarkingClientGui.add(new JLabel("Implementierungsstyp"), cc.xy(1, 3));
        panelBenchmarkingClientGui.add(optionListImplType, cc.xyw(3, 3, 1));
        
        panelBenchmarkingClientGui.add(new JLabel("Anzahl Client-Threads"), cc.xy(5, 3));
        panelBenchmarkingClientGui.add(textFieldNumberOfClientThreads, cc.xy(7, 3));
        textFieldNumberOfClientThreads.setText("10");
        
        panelBenchmarkingClientGui.add(new JLabel("Art der Messung"), cc.xy(1, 5));
        panelBenchmarkingClientGui.add(optionListMeasureType, cc.xyw(3, 5, 1));
        
        panelBenchmarkingClientGui.add(new JLabel("Anzahl Nachrichten je Client"), cc.xy(5, 5));
        panelBenchmarkingClientGui.add(textFieldNumberOfMessagesPerClients, cc.xy(7, 5));
        textFieldNumberOfMessagesPerClients.setText("100");
        
        panelBenchmarkingClientGui.add(new JLabel("Serverport"), cc.xy(1, 7));
        panelBenchmarkingClientGui.add(textFieldServerport, cc.xy(3, 7));
        textFieldServerport.setText("50000");
        
        panelBenchmarkingClientGui.add(new JLabel("Denkzeit [ms]"), cc.xy(5, 7));
        panelBenchmarkingClientGui.add(textFieldThinkTime, cc.xy(7, 7));
        textFieldThinkTime.setText("100");
        
        panelBenchmarkingClientGui.add(new JLabel("Server-IP-Adresse"), cc.xy(1, 9));
        panelBenchmarkingClientGui.add(textFieldServerIpAddress, cc.xy(3, 9));
        textFieldServerIpAddress.setText("localhost");
        
        panelBenchmarkingClientGui.add(new JLabel("Nachrichtenlaenge [Byte]"), cc.xy(5, 9));
        panelBenchmarkingClientGui.add(textFieldMessageLength, cc.xy(7, 9));
        textFieldMessageLength.setText("100");
  
        panelBenchmarkingClientGui.add(new JLabel("Maximale Anzahl Wiederholungen"), cc.xy(1, 11));
        panelBenchmarkingClientGui.add(textFieldNumberOfMaxRetries, cc.xy(3, 11));
        textFieldNumberOfMaxRetries.setText("2");
        
        panelBenchmarkingClientGui.add(new JLabel("Response-Timeout [ms]"), cc.xy(5, 11));
        panelBenchmarkingClientGui.add(textFieldResponseTimeout, cc.xy(7, 11));
        textFieldResponseTimeout.setText("2000");

        panelBenchmarkingClientGui.add(createSeparator("Laufzeiteinstellungen"), cc.xyw(1, 15, 7));
        panelBenchmarkingClientGui.add(new JLabel("Geplante Requests"), cc.xy(1, 17));
        panelBenchmarkingClientGui.add(textFiledPlannedRequests, cc.xy(3, 17));
        textFiledPlannedRequests.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Testbeginn"), cc.xy(5, 17));
        panelBenchmarkingClientGui.add(textFieldTestBegin, cc.xy(7, 17));
        textFieldTestBegin.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Gesendete Requests"), cc.xy(1, 19));
        panelBenchmarkingClientGui.add(textFieldSentRequests, cc.xy(3, 19));
        textFieldSentRequests.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Testende"), cc.xy(5, 19));
        panelBenchmarkingClientGui.add(textFieldTestEnd, cc.xy(7, 19));
        textFieldTestEnd.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Empfangene Responses"), cc.xy(1, 21));
        panelBenchmarkingClientGui.add(textFieldReceivedResponses, cc.xy(3, 21));
        textFieldReceivedResponses.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Testdauer [s]"), cc.xy(5, 21));
        panelBenchmarkingClientGui.add(textFieldTestDuration, cc.xy(7, 21));
        textFieldTestDuration.setEditable(false);
        
        panelBenchmarkingClientGui.add(createSeparator("Messergebnisse"), cc.xyw(1, 23, 7));
        panelBenchmarkingClientGui.add(new JLabel("Mittlere RTT [ms]"), cc.xy(1, 25));
        panelBenchmarkingClientGui.add(textFieldAvgRTT, cc.xy(3, 25));
        textFieldAvgRTT.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Mittlere Serverzeit [ms]"), cc.xy(5, 25));
        panelBenchmarkingClientGui.add(textFieldAvgServerTime, cc.xy(7, 25));
        textFieldAvgServerTime.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Maximale RTT [ms]"), cc.xy(1, 27));
        panelBenchmarkingClientGui.add(textFieldMaxRTT, cc.xy(3, 27));
        textFieldMaxRTT.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Maximale Heap-Belegung [MiB]"), cc.xy(5, 27));
        panelBenchmarkingClientGui.add(textFieldMaxHeapUsage, cc.xy(7, 27));
        textFieldMaxHeapUsage.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Minimale RTT [ms]"), cc.xy(1, 29));
        panelBenchmarkingClientGui.add(textFieldMinRTT, cc.xy(3, 29));
        textFieldMinRTT.setEditable(false);
        
        panelBenchmarkingClientGui.add(new JLabel("Durchschnittliche CPU-Auslastung [%]"), cc.xy(5, 29));
        panelBenchmarkingClientGui.add(textFieldAvgCpuUsage, cc.xy(7, 29));
        textFieldAvgCpuUsage.setEditable(false);
      
        panelBenchmarkingClientGui.add(textFieldNumberOfRetries, cc.xy(3, 31)); 
        textFieldNumberOfRetries.setEditable(false);
        panelBenchmarkingClientGui.add(new JLabel("Anzahl aller Uebertragungswiederholungen"), cc.xy(5, 31));
        panelBenchmarkingClientGui.add(textFieldNumberOfRetries, cc.xy(7, 31));
        
        
        panelBenchmarkingClientGui.add(createSeparator(""), cc.xyw(1, 33, 7));

        // Meldungsbereich erzeugen

        panelBenchmarkingClientGui.add(scrollPane, cc.xyw(1, 35, 7));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setCaretPosition(0);

        panelBenchmarkingClientGui.add(createSeparator(""), cc.xyw(1, 37, 7));

        // Buttons erzeugen
        panelBenchmarkingClientGui.add(startButton, cc.xyw(2, 39, 2));   //Starten
        panelBenchmarkingClientGui.add(newButton, cc.xyw(4, 39, 2));     //Loeschen
        panelBenchmarkingClientGui.add(finishButton, cc.xyw(6, 39, 2)); //Abbrechen
        
        // Listener fuer Buttons registrieren
        startButton.addActionListener(this);
        newButton.addActionListener(this);
        finishButton.addActionListener(this);
        return panelBenchmarkingClientGui;
    }

    /**
     * actionPerformed
     * Listener-Methode zur Bearbeitung der Button-Aktionen
     * Reagiert auf das Betaetigen eines Buttons
     *
     * @param e Ereignis
     */
    //@SuppressWarnings("deprecation")
    public void actionPerformed(ActionEvent e) {

        // Analysiere Ereignis und fuehre entsprechende Aktion aus

        if (e.getActionCommand().equals("Starten")) {
            startAction(e);
            startButton.setEnabled(false);
        } else if (e.getActionCommand().equals("Neu")) {
            newAction(e);
            startButton.setEnabled(true);
        } else if (e.getActionCommand().equals("Beenden"))
            finishAction(e);
    }

    /**
     * startAction
     * Aktion bei Betaetigung des "Start"-Buttons ausfuehren
     * Eingabefelder werden validiert.
     *
     * @param e Ereignis
     */
    private void startAction(ActionEvent e) {
        // Input-Parameter aus GUI lesen
        UserInterfaceInputParameters iParm = new UserInterfaceInputParameters();

        String testString;
        
        /*
         *  GUI sammmelt Eingabedaten und validieren
         */

        // Validierung fuer Denkzeit
        testString = textFieldThinkTime.getText();
        if (!testString.matches( "[0-9]+")) {
        	 // nicht numerisch 
        	 // Aktualisieren des Frames auf dem Bildschirm
        	 setMessageLine("Denkzeit bitte numerisch angeben");
            frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
            return;
         } else {
           	Integer iThinkTime = new Integer(textFieldThinkTime.getText());
        	System.out.println("Denkzeit: " + iThinkTime + " ms");
        	iParm.setClientThinkTime(iThinkTime.intValue());
    	}

        // Validierung fuer Serverport
        testString = textFieldServerport.getText();
        if (testString.matches( "[0-9]+")){
        	Integer iServerPort = new Integer(textFieldServerport.getText());
        	if ((iServerPort < 1) || (iServerPort > 65535 )){
        		// nicht im Wertebereich
        		// Aktualisieren des Frames auf dem Bildschirm
        		setMessageLine("Serverport im Bereich von 1 bis 65535 angeben");
        		frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
        		return;
        	} else {
        		System.out.println("Serverport: " + iServerPort);
        		iParm.setRemoteServerPort(iServerPort.intValue());
        	}
         } else {
        	 setMessageLine("Serverport nicht numerisch");
     		 frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
     		 return;
         }
        
        // Validierung fuer Anzahl Client-Threads
        testString = textFieldNumberOfClientThreads.getText();
        if (testString.matches( "[0-9]+")){
        	Integer iClientThreads = new Integer(textFieldNumberOfClientThreads.getText());
        	if (iClientThreads < 1) {
        		// nicht im Wertebereich
        		// Aktualisieren des Frames auf dem Bildschirm
        		setMessageLine("Anzahl Client-Threads bitte groesser als 0 angeben");
        		frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
        		return;
        	} else {         	
        	 System.out.println("Anzahl Client-Threads:" + iClientThreads);
        	 iParm.setNumberOfClients(iClientThreads.intValue());
        	}
        } else {
       	 setMessageLine("Anzahl Client-Threads nicht numerisch");
    		 frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
    		 return;
        }
        
        // Validierung fuer Anzahl Nachrichten
        testString = textFieldNumberOfMessagesPerClients.getText();
        if (testString.matches( "[0-9]+")){
            Integer iNumberOfMessages = new Integer(textFieldNumberOfMessagesPerClients.getText());
        	if (iNumberOfMessages < 1) {
        		// nicht numerisch
        		// Aktualisieren des Frames auf dem Bildschirm
        		setMessageLine("Anzahl Nachrichten groesser als 0 angeben");
        		frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
        		return;
        	} else {         	
                System.out.println("Anzahl Nachrichten:" + iNumberOfMessages);
                iParm.setNumberOfMessages(iNumberOfMessages.intValue());
        	}
        } else {
       	 setMessageLine("Anzahl Nachrichten nicht numerisch");
    		 frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
    		 return;
        }
        	
        // Validierung fuer Nachrichtenlaenge
        testString = textFieldMessageLength.getText();
        if (testString.matches( "[0-9]+")){
        	Integer iMessageLength = new Integer(textFieldMessageLength.getText());
        	if ((iMessageLength < 1) || (iMessageLength > 50000 )){
        		// nicht im Wertebereich
        		// Aktualisieren des Frames auf dem Bildschirm
        		setMessageLine("Nachrichtenlaenge bitte im Bereich von 1 bis 50000 angeben");
        		frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
        		return;
        	} else {
        		 System.out.println("Nachrichtenlaenge:" + iMessageLength + " Byte");
        	     iParm.setMessageLength(iMessageLength.intValue()); 
        	}
         } else {
        	 setMessageLine("Nachrichtenlaenge nicht numerisch");
     		 frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
     		 return;
         }
    
        System.out.println("RemoteServerAdress:" + textFieldServerIpAddress.getText());
        iParm.setRemoteServerAddress(textFieldServerIpAddress.getText());
        
             
        // Validierung fuer Response-Timeout
        testString = textFieldResponseTimeout.getText();
        if (!testString.matches( "[0-9]+")) {
        	 // nicht numerisch 
        	 // Aktualisieren des Frames auf dem Bildschirm
        	 setMessageLine("Response-Timeout nicht numerisch");
            frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
            return;
         } else {
        	 Integer iResponseTimeout = new Integer(textFieldResponseTimeout.getText());
             System.out.println("Response-Timeout:" + iResponseTimeout);
             iParm.setResponseTimeout( iResponseTimeout.intValue());
    	}
        
        // Validierung fuer maximalen Nachrichtenwiederholung
        testString = textFieldNumberOfMaxRetries.getText();
        if (!testString.matches( "[0-9]+")) {
        	 // nicht numerisch 
        	 // Aktualisieren des Frames auf dem Bildschirm
        	 setMessageLine("Maximale Anzahl Wiederholungen nicht numerisch");
            frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
            return;
         } else {
        	 Integer iNumberOfMaxRetries = new Integer(textFieldNumberOfMaxRetries.getText());
             System.out.println("Maximale Anzahl Wiederholungen:" + iNumberOfMaxRetries);
             iParm.setNumberOfRetries( iNumberOfMaxRetries.intValue());
    	}
        
       
        /*
         *  Benchmarking-Client instanziieren und Benchmark starten
         */

        // Eingegebenen Implementierungstyp auslesen
        String item1 = (String) optionListImplType.getSelectedItem();
        System.out.println("Implementierungstyp eingegeben: " + item1);
        if (item1.equals(MULTI_THREADED_TCP))
            iParm.setImplementationType(ImplementationType.TCPMultiThreaded);
        if (item1.equals(MULTI_THREADED_UDP))
            iParm.setImplementationType(ImplementationType.UDPMultiThreaded);

        // Eingegebenen Messungstyp auslesen
        String item2 = (String) optionListMeasureType.getSelectedItem();
        System.out.println("Messungstyp eingegeben: " + item2);

        if (item1.equals("Variable Threads"))
            iParm.setMeasurementType(MeasurementType.VarThreads);
        if (item1.equals("Variable Length"))
            iParm.setMeasurementType(MeasurementType.VarMsgLength);

        // Aufruf des Benchmarks
        BenchmarkingClient benchClient = new BenchmarkingClient();
        benchClient.executeTest(iParm, this);
    }

    /**
     * newAction
     * Aktion bei Betaetigung des "Neu"-Buttons ausfuehren
     *
     * @param e Ereignis
     */
    private void newAction(ActionEvent e) {
        /*
         * Loeschen bzw. initialisieren der Ausgabefelder 
         */
        textFieldSeperator.setText("");
        textFiledPlannedRequests.setText("");
        textFieldTestBegin.setText("");
        textFieldSentRequests.setText("");
        textFieldTestEnd.setText("");
        textFieldReceivedResponses.setText("");
        textFieldTestDuration.setText("");
        textFieldAvgRTT.setText("");
        textFieldAvgServerTime.setText("");
        textFieldMaxRTT.setText("");
        textFieldAvgCpuUsage.setText("");
        textFieldMaxHeapUsage.setText("");
        textFieldMinRTT.setText("");
        textFieldNumberOfRetries.setText(""); // Anzahl an Nachrichtenwiederholungen insgesamt
    }

    /**
     * Aktion bei Betaetigung des "Beenden"-Buttons ausfuehren
     *
     * @param e Ereignis
     */
    private void finishAction(ActionEvent e) {
        setMessageLine("Programm wird beendet...");

        // Programm beenden
        System.exit(0);
    }

    /**
     * Schliessen des Fensters und Beenden des Programms
     *
     * @param e
     */
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    @Override
    public synchronized void showStartData(UserInterfaceStartData data) {
        String strNumberOfRequests = (new Long(data.getNumberOfRequests())).toString();
        textFiledPlannedRequests.setText(strNumberOfRequests);
        textFieldTestBegin.setText(data.getStartTime());

        // Aktualisieren der Ausgabefelder auf dem Bildschirm
        textFiledPlannedRequests.update(textFiledPlannedRequests.getGraphics());
        textFieldTestBegin.update(textFieldTestBegin.getGraphics());
    }

    @Override
    public synchronized void showResultData(UserInterfaceResultData data) {

    	
    	textFieldSentRequests.setText((new Long(data.getNumberOfSentRequests())).toString());
    	
    	textFieldTestEnd.setText(data.getEndTime());
		
        textFieldReceivedResponses.setText((new Long(data.getNumberOfResponses())).toString());      
        textFieldMaxHeapUsage.setText((new Long(data.getMaxHeapSize())).toString());
        textFieldNumberOfRetries.setText((new Long(data.getNumberOfRetries())).toString()); 

        formatter = new Formatter();
		textFieldAvgRTT.setText(formatter.format( "%.2f", (new Double(data.getAvgRTT())) ).toString());          
		formatter = new Formatter();
		textFieldAvgServerTime.setText(formatter.format( "%.4f", (new Double(data.getAvgServerTime())) ).toString());       
		formatter = new Formatter();
		textFieldMaxRTT.setText(formatter.format( "%.2f", (new Double(data.getMaxRTT())) ).toString());
		formatter = new Formatter();
        textFieldMinRTT.setText(formatter.format( "%.2f", (new Double(data.getMinRTT())) ).toString());
        formatter = new Formatter();
        textFieldAvgCpuUsage.setText(formatter.format( "%.2f", (new Double(data.getMaxCpuUsage() * 100))).toString());
        
        // Aktualisieren des Frames auf dem Bildschirm
        frameEchoBenchmarkingGui.update(frameEchoBenchmarkingGui.getGraphics());
    }

    @Override
    public synchronized void setMessageLine(String message) {
        messageArea.append(message + "\n");
        messageArea.update(messageArea.getGraphics());
    }

    @Override
    public synchronized void resetCurrentRunTime() {
        timeCounter = 0;
        String strTimeCounter = (new Long(timeCounter)).toString();
        textFieldTestDuration.setText(strTimeCounter);

        // Aktualisieren des Ausgabefeldes auf dem Bildschirm
        textFieldTestDuration.update(textFieldTestDuration.getGraphics());
    }

    @Override
    public synchronized void addCurrentRunTime(long sec) {
        timeCounter += sec;
        String strTimeCounter = (new Long(timeCounter)).toString();
        textFieldTestDuration.setText(strTimeCounter);

        // Aktualisieren des Ausgabefeldes auf dem Bildschirm
        textFieldTestDuration.update(textFieldTestDuration.getGraphics());
    }

    private Component createSeparator(String text) {
        return DefaultComponentFactory.getInstance().createSeparator(text);
    }
}