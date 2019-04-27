package org.blockchain_innovation.factom.iot_sas;

import com.fazecast.jSerialComm.SerialPort;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.log.LogFactory;
import org.blockchain_innovation.factom.client.api.log.Logger;

import java.io.Closeable;
import java.io.IOException;

/**
 * Wrapper class around a serial port for the SAS communication.
 */
public class IoTSASPort implements Closeable {
    private static final Logger logger = LogFactory.getLogger(IoTSASPort.class);
    private SerialPort port;

    /**
     * Setup the serial port using defaults (/dev/serial0, 57600 baudRate).
     *
     * @return The serial port.
     */
    public SerialPort setup() {
        return setup(null, null);
    }

    /**
     * Setup the serial port.
     *
     * @param portName The system portname. By default /dev/serial0.
     * @param baudRate The baud rate.
     * @return The serial port.
     */
    public SerialPort setup(String portName, Integer baudRate) {
        if (port != null && port.isOpen()) {
            port.closePort();
        }

        this.port = SerialPort.getCommPort(portName == null ? "/dev/serial0" : portName);
        port.setComPortParameters(baudRate == null ? 57600 : baudRate, 8, 1, 0);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 200, 0);
        logger.info("IoT-SAS serial port is setup: %s", this);
        return port;
    }


    /**
     * Get the serial port.
     *
     * @return The serial port.
     */
    public SerialPort get() {
        return open();
    }

    /**
     * Open the serial port if it is not open already.
     *
     * @return The serial port.
     */
    public SerialPort open() {
        assertSetup();
        if (!port.isOpen()) {
            port.openPort();
            logger.info("IoT-SAS serial port is opened: %s", this);
            if (!port.isOpen()) {
                throw new IoTSASPortException(String.format("Could not open IoT-SAS serial port %s", this));
            }
        }
        return port;
    }

    /**
     * Close the serial port.
     */
    public void close() {
        if (port != null && port.isOpen()) {
            clearInBuffer();
            port.closePort();
            logger.info("IoT-SAS serial port is closed: %s", this);
        }
    }

    /**
     * Clears the in buffer.
     * @return the serial port
     */
    public SerialPort clearInBuffer() {
        if (port == null) {
            return null;
        }

        open();
        StringBuilder buffer = new StringBuilder();

        try {
            int in = 0;
            while (in >= 0) {
                in = port.getInputStream().read();
                if (in >= 0) {
                    buffer.append(Byte.toString((byte) in));
                }
            }
        } catch (IOException e) {
          //  logger.warn(e.getMessage());
        }

        if (buffer.length() > 0 && !buffer.toString().equals("-1120")) {
            logger.warn("IoT-SAS port had %d bytes in it's input buffer that are discarded. Enable debug logging to shows buffer", buffer.length());
            logger.info("Buffer: %s", buffer.toString());
        }
        return port;
    }

    /**
     * Assert the port has been setup.
     */
    private void assertSetup() {
        if (port == null) {
            throw new FactomRuntimeException.AssertionException("For the SAS client please setup serial communication first using the setup method");
        }
    }

    @Override
    public String toString() {
        return "IoTSASPort{" +
                port.getSystemPortName() +
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
