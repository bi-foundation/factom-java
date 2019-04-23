package org.blockchain_innovation.factom.iot_sas;

import com.fazecast.jSerialComm.SerialPort;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;

import java.io.Closeable;

/**
 * Wrapper class around a serial port for the SAS communication
 */
public class IoTSASPort implements Closeable {
    private SerialPort port;

    /**
     * Setup the serial port using defaults (/dev/serial0, 57600 baudRate)
     * @return The serial port
     */
    public SerialPort setup() {
        return setup(null, null);
    }

    /**
     * Setup the serial port
     *
     * @param portName The system portname. By default /dev/serial0
     * @param baudRate The baud rate
     * @return The serial port
     */
    public SerialPort setup(String portName, Integer baudRate) {
        if (port != null && port.isOpen()) {
            port.closePort();
        }

        this.port = SerialPort.getCommPort(portName == null ? "/dev/serial0" : portName);
        port.setComPortParameters(baudRate == null ? 57600 : baudRate, 8, 1, 0);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        return port;
    }


    /**
     * Get the serial port
     *
     * @return The serial port
     */
    public SerialPort get() {
        return open();
    }

    /**
     * Open the serial port if it is not open already
     *
     * @return The serial port
     */
    public SerialPort open() {
        assertSetup();
        if (!port.isOpen()) {
            port.openPort();
        }
        return port;
    }

    /**
     * Close the serial port
     */
    public void close() {
        if (port != null && port.isOpen()) {
            port.closePort();
        }
    }

    /**
     * Assert the port has been setup
     */
    private void assertSetup() {
        if (port == null) {
            throw new FactomRuntimeException.AssertionException("For the SAS client please setup serial communication first using the setup method");
        }
    }

    @Override
    public String toString() {
        return "IoTSASPort{" +
                "port=" + port.getSystemPortName() +
                '}';
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public static class IoTSASPortException extends FactomRuntimeException {

        public IoTSASPortException(Throwable cause) {
            super(cause);
        }

        public IoTSASPortException(String message) {
            super(message);
        }
    }
}
