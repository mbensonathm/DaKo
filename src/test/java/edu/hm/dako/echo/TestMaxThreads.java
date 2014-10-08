/**
 *  Test, wie viele Threads ein Prozess zu einer Zeit nutzen kann.
 *
 *  Hinweis: 
 *  Dies kann von der maximalen Heap-Groesse abhaengen und auch von den Einstellungen im Kernel.  
 *  MAC OS X hat z.B. andere Einstellungen als Windows 7.
 *
 *  @author Mandl
 *  @version 1.0
 */

package edu.hm.dako.echo;

/**
 * Testprogramm zum Test, wie viele Threads in einem System moeglich sind
 * @author mandl
 */
public class TestMaxThreads {

    // Anzahl an anzulegenden Threads
    static final int nrThreads = 50000;

    // Einfacher Thread
    private class MyThread extends Thread {

        public MyThread() {
        }

        public void run() {

            // Warten, damit Thread nicht gleich wieder beendet wird
            try {
                sleep(10000);
            } catch (Exception e) {
                // Ausnahme nicht behandelt
            }
        }
    }

    public static void main(String args[]) {

        new TestMaxThreads().doTest();
    }

    public void doTest() {

        MyThread myThreadArray[] = new MyThread[nrThreads];

        // Threads instanziieren und starten

        for (int i = 0; i < nrThreads; i++) {
            myThreadArray[i] = new MyThread();
            myThreadArray[i].setName("Thread-" + (i + 1));

            try {
                myThreadArray[i].start();
            } catch (Exception e) {
                System.out.println("Exception beim Thread-Start:" + e);
                System.exit(9);
            }

            System.out.println(myThreadArray[i].getName() + " gestartet");
        }

        // Auf das Ende der Threads warten
        try {
            for (int i = 0; i < nrThreads; i++) {
                myThreadArray[i].join();
            }
        } catch (InterruptedException e) {
            // Ausnahme nicht behandelt
        }

        System.out.println("Test erfolgreich, Anzahl Threads erzeugt: " + nrThreads);
    }
}