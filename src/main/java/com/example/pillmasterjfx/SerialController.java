package com.example.pillmasterjfx;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialController {
    SerialPort port;
    String serialPortName;

    public enum CONTROL_FLAG {
        A(0x5),
        B(0x6),
        C(0x7),
        D(0x8),
        E(0x9),
        MAIN(0x1),
        REV(0x4),
        ADJ(0x3),
        BOTH(0x2),
        OPERATE(0x5);

        private final byte value;
        public byte getValue() {
            return value;
        }

        public static byte makeSignal(CONTROL_FLAG command,
                                   CONTROL_FLAG canister) {
            return (byte) ((command.getValue() << 1) + canister.getValue());
        }

        CONTROL_FLAG(int value) {
            this.value = (byte)value;
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
            serialPortName = "/dev/ttyACM1";
        }
        port = new SerialPort(serialPortName);
        port.openPort();
        port.setParams(BAUDRATE, DATA_BITS, STOP_BITS, PARITY);
        port.setDTR(false);
        port.setRTS(false);
    }

    public boolean writeToPort(char message) {
        try {
            port.writeByte((byte) message);
            return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closePort() throws SerialPortException {
        if(port.isOpened()) {
            port.closePort();
        }
    }
}
