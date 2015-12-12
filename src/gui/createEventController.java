package gui;

import javafx.application.Application;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import Scheduler.MyAgent;
import jade.core.AID;

public class createEventController {

	@FXML
    public ListView<AID> agentsToInviteList;
	
	@FXML
    private TextField name;
	
	MyAgent agent;
	public createEventController(MyAgent agent) {
		this.agent= agent;
	}

	@FXML
    void inviteAgents(ActionEvent event) {
		
		
	}
	
	@FXML
	protected void initialize() {
	
		agentsToInviteList.setItems(agent.allAgents);
	}
}