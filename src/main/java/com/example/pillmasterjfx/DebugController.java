package com.example.pillmasterjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import jssc.SerialPortException;

public class DebugController {

    public Button eRight;
    public Button aLeft;
    public Button aOperate;
    public Button aRight;
    public Button bLeft;
    public Button bOperate;
    public Button bRight;
    public Button cLeft;
    public Button cOperate;
    public Button cRight;
    public Button dLeft;
    public Button dOperate;
    public Button dRight;
    public Button eLeft;
    public Button eOperate;

    SerialController serialController;

    public void initialize() {
        try {
            serialController = new SerialController();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void closeWindow(ActionEvent e) {
        try {
            if(serialController != null) {
                serialController.closePort();
            }
        } catch (SerialPortException s) {
            s.printStackTrace();
        }
        ((Stage) ((Node) e.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void onOperateButtonClick(ActionEvent e) {
        switch (((Button)e.getSource()).getId()) {
            case("aLeft"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.A,
                                SerialController.CONTROL_FLAG.REV
                                )
                );
                break;
            case("aRight"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.A,
                                SerialController.CONTROL_FLAG.MAIN
                        )
                );
                break;
            case("bLeft"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.B,
                                SerialController.CONTROL_FLAG.REV
                        )
                );
                break;
            case("bRight"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.B,
                                SerialController.CONTROL_FLAG.MAIN
                        )
                );
                break;
            case("cLeft"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.C,
                                SerialController.CONTROL_FLAG.REV
                        )
                );
                break;
            case("cRight"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.C,
                                SerialController.CONTROL_FLAG.MAIN
                        )
                );
                break;
            case("dLeft"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.D,
                                SerialController.CONTROL_FLAG.REV
                        )
                );
                break;
            case("dRight"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.D,
                                SerialController.CONTROL_FLAG.MAIN
                        )
                );
                break;
            case("eLeft"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.E,
                                SerialController.CONTROL_FLAG.REV
                        )
                );
                break;
            case("eRight"):
                serialController.writeToPort(
                        SerialController.CONTROL_FLAG.makeSignal(
                                SerialController.CONTROL_FLAG.E,
                                SerialController.CONTROL_FLAG.MAIN
                        )
                );
                break;
            case("aOperate"):
                break;
            case("bOperate"):
                break;
            case("cOperate"):
                break;
            case("dOperate"):
                break;
            case("eOperate"):
                break;
        }
    }
}
