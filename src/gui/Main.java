package gui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

	@Override
    public void start(Stage primaryStage) {
        try {
        	BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        primaryStage.setTitle("Neural Networks");

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text scenetitle = new Text("Welcome");
    }
	
	public static void main(String[] args) {
		  try {
	            launch(args);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
}
