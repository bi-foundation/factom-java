package org.blockchain_innovation.factom.iot_sas;

import com.fazecast.jSerialComm.SerialPort;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;

public class IoTSASPort {
    private SerialPort port;

    public SerialPort setup() {
        return setup(null, null);
    }

    public SerialPort setup(String portName, Integer baudRate) {
        if (port != null && port.isOpen()) {
            port.closePort();
        }

        this.port = SerialPort.getCommPort(portName == null ? "/dev/serial0" : portName);
        port.setComPortParameters(baudRate == null ? 57600 : baudRate, 8, 1, 0);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        return port;
    }


    public SerialPort get() {
        return open();
    }

    public SerialPort open() {
        assertSetup();
        if (!port.isOpen()) {
            port.openPort();
        }
        return port;
    }

    public SerialPort close() {
        if (port != null && port.isOpen()) {
            port.closePort();
        }
        return port;
    }

    private void assertSetup() {
        if (port == null) {
            throw new FactomRuntimeException.AssertionException("For the SAS client please setup serial communication first using the setup method");
        }
    }


    public static class IoTSASPortException extends FactomRuntimeException {

        public IoTSASPortException(Throwable cause) {
            super(cause);
        }
    }
}
