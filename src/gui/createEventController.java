package gui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import Scheduler.MyAgent;
import Scheduler.MyEvent;
import Utilities.TimePeriod;
import jade.core.AID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class createEventController {

	@FXML
    public ListView<String> agentsToInviteList;

	@FXML
    private Label error;
	
	@FXML
    private TextField name;
	
	@FXML
    private DatePicker date_from;
	
	@FXML
    private DatePicker date_to;
	
	@FXML
    private ChoiceBox<Integer> time_from_hours;
	
	@FXML
    private ChoiceBox<Integer> time_from_minutes;
	
	@FXML
    private ChoiceBox<Integer> time_to_hours;
	
	@FXML
    private ChoiceBox<Integer> time_to_minutes;
	
	@FXML
    private TextField span_hours;
	
	@FXML
    private ChoiceBox<Integer> span_minutes;
	
	private TreeSet<AID> invitedAgents;
	private  ObservableList<AID> allAgentsShow;
	
	MyAgent agent;
	public createEventController(MyAgent agent) {
		this.agent= agent;

	}

	@FXML
    void invitedAgents(ActionEvent event) {
		
		
	}
	
	@FXML
	protected void initialize() {

		allAgentsShow= FXCollections.observableArrayList();
		agentsToInviteList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		
		/*for (AID agent_forlist : agent.allAgents) {
			if(!agent_forlist.equals(agent.getAID())){
		        allAgentsShow.add(agent_forlist);
			}
		}
		agentsToInviteList.setItems(allAgentsShow);*/
		agentsToInviteList.setItems(agent.neighborsShow);

		for(int i=0;i<61;i++){
			span_minutes.getItems().add(i);
			time_from_minutes.getItems().add(i);
			time_to_minutes.getItems().add(i);
		}
		for(int i=0;i<24;i++){
			time_from_hours.getItems().add(i);
			time_to_hours.getItems().add(i);
		}
		
		
	}
	
	@FXML
	void createEvent(){
		System.out.println("create event button clicked");
		
		if(name.getText()==null 
				|| date_from.getValue() == null 
				|| date_to.getValue() == null 
				|| time_from_hours.getValue() == null 
				|| time_from_minutes.getValue() == null 
				|| time_to_hours.getValue() == null 
				|| time_to_minutes.getValue() == null  ){
			error.setText("Please fill all the requested inputs");
			System.out.println("soumerda");
			return ;
		}
		
		invitedAgents= new TreeSet<AID>();
			
		for (String aid : agentsToInviteList.getSelectionModel().getSelectedItems()) {
			invitedAgents.add(agent.agentsMap.get(aid));
			
		}
		
		for (AID aid : invitedAgents) {
			System.out.println("Invited : " +aid.getName());
		}
		
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		
		LocalDate init_LocDate = date_from.getValue();
		Instant ini_instant = Instant.from(init_LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date ini_date = Date.from(ini_instant);
		
		LocalDate end_LocDate = date_to.getValue();
		Instant end_instant = Instant.from(end_LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date end_date = Date.from(end_instant);
		
		
		cal_start.setTime(ini_date);
		cal_end.setTime(end_date);
		
		cal_start.set(Calendar.HOUR,time_from_hours.getValue());
		cal_start.set(Calendar.MINUTE,time_from_minutes.getValue());
		cal_end.set(Calendar.HOUR,time_to_hours.getValue());
		cal_end.set(Calendar.MINUTE,time_to_minutes.getValue());
		
		cal_end.set(Calendar.MINUTE,time_to_minutes.getValue());
		if(cal_start.after(cal_end)){
			error.setText("Error: Invalid time interval.");
			return ;
		}
		
		TimePeriod period=new TimePeriod(cal_start,cal_end);
		
		long span=Integer.parseInt(span_hours.getText())*60 +span_minutes.getValue();
		
		MyEvent event = new MyEvent(name.getText(), span, invitedAgents, period);
		System.out.println(event.getName());
		System.out.println(event.getSpan());
		agent.sendInvitations(event);
	    ((Stage) name.getScene().getWindow()).close();

	}
}
