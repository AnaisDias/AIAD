package gui;

import Scheduler.MyAgent;
import Scheduler.MyEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.Stage;

public class AgentFxController{

	@FXML
    public ListView<MyEvent> eventsAccepted;
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
		}
	}


	@FXML
    void handleButtonCreateAction(ActionEvent event) {
		try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("template/createEvent.fxml"));
				loader.setController(new createEventController(this.agent));
	            Stage stage = new Stage();
	            stage.setTitle("Schedule an even with uSchedule");
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
		
		eventsAccepted.setItems(agent.events);
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




	/*
	Stage thestage;
	Button btnCreateEvent;
	Scene mainScene;
	Scene createEventScene;
	
	
	@Override
    public void start(Stage primaryStage) {
		thestage = primaryStage;
		VBox vbox = new VBox(20);
		VBox vbox2 = new VBox(20);
        try {
        	
            vbox.setStyle("-fx-padding: 10;");
        	BorderPane root = new BorderPane();
			mainScene = new Scene(vbox, 400, 400);
			createEventScene = new Scene(vbox2,400,400);

            primaryStage.setScene(mainScene);
            primaryStage.show();

        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        ObservableList<String> items = FXCollections.observableArrayList("test1", "test2");
        ListView<String> list = new ListView<>(items);

        list.setEditable(true);
        list.setMaxWidth(100);
        
        
        primaryStage.setTitle("Schedule Aligner");

		GridPane grid = new GridPane();
		GridPane grid2 = new GridPane();
		btnCreateEvent=new Button("Create Event");
		btnCreateEvent.setOnAction(e-> ButtonClicked(e));
		DatePicker startDatePicker = new DatePicker();
		DatePicker startDatePicker2 = new DatePicker();
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		grid2.setAlignment(Pos.TOP_CENTER);
		grid2.setHgap(10);
		grid2.setVgap(10);
		grid2.setPadding(new Insets(25, 25, 25, 25));
		
		grid.add(startDatePicker, 0, 1);
		grid.add(startDatePicker2, 0, 1);
		
		
		vbox.getChildren().addAll(list, grid, btnCreateEvent);
		vbox2.getChildren().add(grid2);

		Text scenetitle = new Text("Welcome");
    }
	
	public static void main(String[] args) {
		  try {
	            launch(args);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	public void ButtonClicked(ActionEvent e)
    {
        if (e.getSource()==btnCreateEvent)
            thestage.setScene(createEventScene);
        else
            thestage.setScene(mainScene);
    }
	
}

*/
