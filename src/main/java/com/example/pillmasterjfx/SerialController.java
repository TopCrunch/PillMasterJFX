package com.example.pillmasterjfx;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialController {
    SerialPort port;
    String serialPortName;

    private static final int BAUDRATE = 9600;
    private static final int DATA_BITS = 8;
    private static final int STOP_BITS = 1;
    private static final int PARITY = 0;

    public SerialController() throws SerialPortException {
        String osName = System.getProperty("os.name").toLowerCase();
        System.out.println("Operating System: " + osName);
        if(osName.contains("windows")) {
            serialPortName = "COM4";
        } else if(osName.contains("linux")) {
            serialPortName = "/dev/ttyACM1";
        }
        port = new SerialPort(serialPortName);
        port.openPort();
        port.setParams(BAUDRATE, DATA_BITS, STOP_BITS, PARITY);
        port.setDTR(false);
        port.setRTS(false);
    }

    public void writeToPort(char message) throws SerialPortException {
        port.writeByte((byte)message);
    }

    public void closePort() throws SerialPortException {
        if(port.isOpened()) {
            port.closePort();
        }
    }
}
