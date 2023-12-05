package com.example.pillmasterjfx;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialController {
    SerialPort port;
    String serialPortName;

    boolean isRunning = false;

    public enum CONTROL_FLAG {
        A(5), B(6), C(7), D(8), E(9), ALT(4),
        MAIN(16), REV(64), ADJ(48), BOTH(32), OPERATE(80),
        WEIGHT(64), READY(48), TRIP(80);

        private final int value;
        public int getValue() {
            return value;
        }

        public static int makeSignal(CONTROL_FLAG command,
                                   CONTROL_FLAG canister) {
            return command.getValue() + canister.getValue();
        }

        CONTROL_FLAG(int value) {
            this.value = value;
        }
    }

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
            serialPortName = "/dev/ttyACM0";
        }
        port = new SerialPort(serialPortName);
        port.openPort();
        port.setParams(BAUDRATE, DATA_BITS, STOP_BITS, PARITY);
        port.setDTR(false);
        port.setRTS(false);
    }

    public boolean writeToPort(int message) {
        try {
            port.writeByte((byte)message);
            return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public SerialPort getPort() {
        return port;
    }

    public boolean operate(CONTROL_FLAG canister) {
        if(isRunning) {
            return false;
        } else {
            writeToPort(CONTROL_FLAG.makeSignal(CONTROL_FLAG.OPERATE, canister));
            isRunning = true;
            return true;
        }
    }

    public void closePort() throws SerialPortException {
        if(port.isOpened()) {
            port.closePort();
        }
    }
}
