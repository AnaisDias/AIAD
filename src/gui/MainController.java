package gui;

import jade.wrapper.AgentContainer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {

	public TextField agentName;
	AgentContainer agentsContainer;

	public MainController(AgentContainer agentsContainer) {
		this.agentsContainer = agentsContainer;
	}

	@FXML
	void handleStartAgentAction(ActionEvent event) {
		try {
			if(agentName.getText().isEmpty()){
				return;
			}
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("template/agentView.fxml"));
			loader.setController(new AgentFxController(agentName.getText(), agentsContainer));

			Stage stage = new Stage();
			stage.setTitle("Agent " + agentName.getText() +" view");
			Scene scene = new Scene(loader.load());
			stage.setScene(scene);
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					loader.<AgentFxController>getController().stop();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
