package com.example.pillmasterjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
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
    public ToggleButton aAdj;
    public ToggleButton bAdj;
    public ToggleButton cAdj;
    public ToggleButton dAdj;
    public ToggleButton eAdj;

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
    public void onButtonToggle(ActionEvent e) {
        if(((ToggleButton)e.getSource()).isSelected()) {
            ((ToggleButton)e.getSource()).setStyle("-fx-background-color:red");
        } else {
            ((ToggleButton)e.getSource()).setStyle("");
        }
    }

    @FXML
    public void onOperateButtonClick(ActionEvent e) {
        SerialController.CONTROL_FLAG canister = null;
        SerialController.CONTROL_FLAG command = null;
        switch (((Button) e.getSource()).getId()) {
            case ("aLeft") -> {
                canister = SerialController.CONTROL_FLAG.A;
                if (aAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.BOTH;
                } else {
                    command = SerialController.CONTROL_FLAG.REV;
                }
            }
            case ("aRight") -> {
                canister = SerialController.CONTROL_FLAG.A;
                if (aAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.ADJ;
                } else {
                    command = SerialController.CONTROL_FLAG.MAIN;
                }
            }
            case ("bLeft") -> {
                canister = SerialController.CONTROL_FLAG.B;
                if (bAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.BOTH;
                } else {
                    command = SerialController.CONTROL_FLAG.REV;
                }
            }
            case ("bRight") -> {
                canister = SerialController.CONTROL_FLAG.B;
                if (bAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.ADJ;
                } else {
                    command = SerialController.CONTROL_FLAG.MAIN;
                }
            }
            case ("cLeft") -> {
                canister = SerialController.CONTROL_FLAG.C;
                if (cAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.BOTH;
                } else {
                    command = SerialController.CONTROL_FLAG.REV;
                }
            }
            case ("cRight") -> {
                canister = SerialController.CONTROL_FLAG.C;
                if (cAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.ADJ;
                } else {
                    command = SerialController.CONTROL_FLAG.MAIN;
                }
            }
            case ("dLeft") -> {
                canister = SerialController.CONTROL_FLAG.D;
                if (dAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.BOTH;
                } else {
                    command = SerialController.CONTROL_FLAG.REV;
                }
            }
            case ("dRight") -> {
                canister = SerialController.CONTROL_FLAG.D;
                if (dAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.ADJ;
                } else {
                    command = SerialController.CONTROL_FLAG.MAIN;
                }
            }
            case ("eLeft") -> {
                canister = SerialController.CONTROL_FLAG.E;
                if (eAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.BOTH;
                } else {
                    command = SerialController.CONTROL_FLAG.REV;
                }
            }
            case ("eRight") -> {
                canister = SerialController.CONTROL_FLAG.E;
                if (eAdj.isSelected()) {
                    command = SerialController.CONTROL_FLAG.ADJ;
                } else {
                    command = SerialController.CONTROL_FLAG.MAIN;
                }
            }
            case ("aOperate") -> {
                canister = SerialController.CONTROL_FLAG.A;
                command = SerialController.CONTROL_FLAG.OPERATE;
            }
            case ("bOperate") -> {
                command = SerialController.CONTROL_FLAG.OPERATE;
                canister = SerialController.CONTROL_FLAG.B;
            }
            case ("cOperate") -> {
                command = SerialController.CONTROL_FLAG.OPERATE;
                canister = SerialController.CONTROL_FLAG.C;
            }
            case ("dOperate") -> {
                command = SerialController.CONTROL_FLAG.OPERATE;
                canister = SerialController.CONTROL_FLAG.D;
            }
            case ("eOperate") -> {
                command = SerialController.CONTROL_FLAG.OPERATE;
                canister = SerialController.CONTROL_FLAG.E;
            }
        }

        if(canister != null && command != null) {
            serialController.writeToPort(
                    SerialController.CONTROL_FLAG.makeSignal(
                            canister,
                            command
                    )
            );
        }
    }
}
