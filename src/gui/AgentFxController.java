package gui;

import java.io.IOException;

import Scheduler.MyAgent;
import Scheduler.MyEvent;
import jade.core.AID;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.Stage;

public class AgentFxController{

	@FXML
    public ListView<MyEvent> eventsAccepted;
	
	@FXML
	public ListView<MyEvent> eventsInvited;
	
	AgentContainer agentsContainer;	
	AgentController agentController;

	MyAgent agent;
	public AgentFxController(String agentName, AgentContainer agentsContainer) {
		this.agentsContainer = agentsContainer;
		try {	
			agent = new MyAgent();
			agentController = agentsContainer.acceptNewAgent(agentName, agent);
			agentController.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			((Stage) eventsInvited.getScene().getWindow()).close();
		}
	}


	@FXML
    void handleButtonCreateAction(ActionEvent event) {
		try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("template/createEvent.fxml"));
				loader.setController(new createEventController(this.agent));
	            Stage stage = new Stage();
	            stage.setTitle(agent.getLocalName() + ": Schedule an even with uSchedule");
	            Scene scene = new Scene(loader.load());
	            stage.setScene(scene);
	            

	            
	            /*
	            ObservableList<String> other_agents = FXCollections.observableArrayList();
	            for (AID agent : this.agent.allAgents) {
	            	if(agent != this.agent.getAID())
	            	other_agents.add(agent.getName());
				}
	            createEventController controller = loader.<createEventController>getController();
	            controller.agentsToInviteList.setItems(FXCollections.observableList(other_agents));
	            
	           //ListView<String> list = new ListView<>(other_agents);
	           //list.setItems(FXCollections.observableList(other_agents));
	            */
	            
	           
	            
	            stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
    }
	
	
	@FXML
	protected void initialize() {
		
		eventsAccepted.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		eventsInvited.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		eventsAccepted.setItems(agent.events);
		eventsInvited.setItems(agent.invitations);
		
		agent.allReady.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                agent.startAlgorithm();
            }
        });
	}

	@FXML
	void acceptEvents() throws IOException{
		
		for (MyEvent ev : eventsInvited.getSelectionModel().getSelectedItems()) {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("template/acceptedEvent.fxml"));
			loader.setController(new acceptedEventController(this.agent,ev));
            Stage stage = new Stage();
            stage.setTitle(agent.getName() + " : " + ev.getName());
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            
            stage.show();
		}
		
	}
	
	@FXML
	void declineEvents() throws IOException{
		
		for (MyEvent ev : eventsInvited.getSelectionModel().getSelectedItems()) {
			agent.declineInvitation(ev);
			eventsInvited.getItems().remove(ev);
		}
		
	
		
	}
	
	
	public void stop() {
		try {
			agentController.kill();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}


	
