package edu.hm.dako.echo.client;

import edu.hm.dako.echo.benchmarking.UserInterfaceInputParameters;
import edu.hm.dako.echo.common.SharedClientStatistics;
import edu.hm.dako.echo.connection.ConnectionFactory;
import edu.hm.dako.echo.connection.DecoratingConnectionFactory;
import edu.hm.dako.echo.tcp.TcpConnectionFactory;
import edu.hm.dako.echo.tcp.TcpEchoClientImpl;
import edu.hm.dako.echo.udp.UdpClientConnectionFactory;
import edu.hm.dako.echo.udp.UdpEchoClientImpl;

/**
 * Uebernimmt die Konfiguration und die Erzeugung bestimmter Client-Typen.
 * Siehe {@link edu.hm.dako.echo.benchmarking.UserInterfaceInputParameters.ImplementationType}
 * Dies beinhaltet die {@link ConnectionFactory}, die Adressen, Ports, Denkzeit etc.
 */
public final class ClientFactory {

    private ClientFactory() {
    }

    public static Runnable getClient(UserInterfaceInputParameters param, int numberOfClient, SharedClientStatistics sharedData) {
        try {
            switch (param.getImplementationType()) {
                case TCPMultiThreaded:
                    return new TcpEchoClientImpl(param.getRemoteServerPort(),
                            param.getRemoteServerAddress(), numberOfClient, param.getMessageLength(),
                            param.getNumberOfMessages(), param.getClientThinkTime(),
                            param.getNumberOfRetries(), param.getResponseTimeout(),
                            sharedData, getDecoratedFactory(new TcpConnectionFactory()));
                case UDPMultiThreaded:
                    return new UdpEchoClientImpl(param.getRemoteServerPort(),
                            param.getRemoteServerAddress(), numberOfClient, param.getMessageLength(),
                            param.getNumberOfMessages(), param.getClientThinkTime(),
                            param.getNumberOfRetries(), param.getResponseTimeout(),
                            sharedData, getDecoratedFactory(new UdpClientConnectionFactory()));
                default:
                    throw new RuntimeException("Unbekannter Implementierungstyp: " + param.getImplementationType());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ConnectionFactory getDecoratedFactory(ConnectionFactory connectionFactory) {
        return new DecoratingConnectionFactory(connectionFactory);
    }
}