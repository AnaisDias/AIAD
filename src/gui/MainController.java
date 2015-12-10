package gui;

import jade.core.AID;
import jade.wrapper.AgentContainer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import Scheduler.MyAgent;
import Utilities.*;

import java.util.Random;

public class MainController {

	@FXML
    void handleStartAgentAction(ActionEvent event) {
		try {
			  	FXMLLoader loader = new FXMLLoader(getClass().getResource("template/agentView.fxml"));
	            Stage stage = new Stage();
	            stage.setTitle("Agent view");
	            Scene scene = new Scene(loader.load());
	            stage.setScene(scene);
	            stage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
		
    }
	
}
