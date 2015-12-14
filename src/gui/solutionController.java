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
	String st;
	ObservableList<MyEvent> ev;
	
	public solutionController(ObservableList<MyEvent> events, String string){
		st=string;
		ev=events;
	}
	
	@FXML
	protected void initialize() {
		System.out.println(st);
		name.setText(st);
		ObservableList<String> temp = FXCollections.observableArrayList();
		for (MyEvent event : ev) {
			
			String temp2 = "Event - " + event.getName() + "  Solution : " + event.getAgreedTimePeriod().toString() + " Cost: " + event.getAgreedCost();
			temp.add(temp2);
			
		}
		sol.setItems(temp);
		
	}
	
	
}
