package com.example.pillmasterjfx;

import javafx.application.Platform;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

interface SerialStringReader {
    public void process(String s);
}
public class ArduinoController {
    private final SerialController serialController;

    public ArduinoController() throws SerialPortException {
        serialController = new SerialController();
    }

    public void write(int i) {
        serialController.writeToPort(i);
    }

    public void clearListener() throws SerialPortException {
        serialController.getPort().removeEventListener();
    }

    public void listen(SerialStringReader reader) throws SerialPortException {
        serialController.getPort().addEventListener(new SerialPortEventListener() {
            StringBuilder message = new StringBuilder();
            @Override
            public void serialEvent(SerialPortEvent event) {
                if(event.isRXCHAR() && event.getEventValue() > 0){
                    try {
                        byte[] buffer = event.getPort().readBytes();
                        for (byte b: buffer) {
                            if ( (b == '\r' || b == '\n') && message.length() > 0) {
                                String toProcess = message.toString();
                                Platform.runLater(
                                        () -> reader.process(toProcess)
                                );
                                message.setLength(0);
                            }
                            else {
                                message.append((char)b);
                            }
                        }
                    }
                    catch (SerialPortException ex) {
                        System.out.println(ex);
                        System.out.println("serialEvent");
                    }
                }
            }
        });
    }
}
