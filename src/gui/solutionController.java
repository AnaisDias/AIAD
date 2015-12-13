package gui;

import java.lang.reflect.Array;

import Scheduler.MyEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class solutionController {
	@FXML
	ListView<String> sol;
	@FXML
	Label name;
	//adicioanr aprametros qe or preciso pelo s
	public solutionController(ObservableList<MyEvent> events, String string){
		
		name.setText(string);
		ObservableList<String> temp = FXCollections.observableArrayList();
		for (MyEvent event : events) {
			
			String temp2 = "Event - " + event.getName() + "  Solution : " + event.getAgreedTimePeriod().toString() + " Cost: " + event.getAgreedCost();
			temp.add(temp2);
			
		}
		sol.setItems(temp);
	}
	
	@FXML
	protected void initialize() {
		
		
	}
	
	
}
