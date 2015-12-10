package gui;

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
import javafx.fxml.FXMLLoader;



import Scheduler.*;
import Utilities.*;

import java.util.Random;

public class MainController {

	@FXML
    void handleButtonCreateAction(ActionEvent event) {
		System.out.println("You clicked me!");
		try {
			  	FXMLLoader loader = new FXMLLoader(getClass().getResource("template/createEvent.fxml"));
	            Stage stage = new Stage();
	            stage.setTitle("Schedule an even with uSchedule");
	            Scene scene = new Scene(loader.load());
	            stage.setScene(scene);
	            stage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
		/*tomorrow morning ill complete it  */
    }
	
}
