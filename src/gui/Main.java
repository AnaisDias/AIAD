package gui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.*;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

public class Main extends Application{
	
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
