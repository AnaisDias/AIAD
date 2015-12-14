package gui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import Constraints.AfterDateConstraint;
import Constraints.AfterHourConstraint;
import Constraints.BeforeDateConstraint;
import Constraints.BeforeHourConstraint;
import Constraints.SpecificDateConstraint;
import Scheduler.MyAgent;
import Scheduler.MyEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class acceptedEventController {

	@FXML
	DatePicker from_date;

	@FXML
	DatePicker to_date;

	@FXML
	ChoiceBox<Integer> from_hours;

	@FXML
	ChoiceBox<Integer> to_hours;
	
	@FXML
	ChoiceBox<Integer> from_minutes;

	@FXML
	ChoiceBox<Integer> to_minutes;
	
	
	@FXML
	Label timespan;
	
	@FXML
	Label span;

	@FXML
	Label error;
	

	MyAgent agent;
	MyEvent ev;

	public acceptedEventController(MyAgent agent, MyEvent ev) {

		this.ev = ev;
		this.agent = agent;

	}

	@FXML
	protected void initialize() {

		
		for(int i=0;i<60;i++){
			from_minutes.getItems().add(i);
			to_minutes.getItems().add(i);
			
		}
		for(int i=0;i<24;i++){
			from_hours.getItems().add(i);
			to_hours.getItems().add(i);
		}
		
		String[] split=(""+ev.getDateProposal()).split(",");
		String sp="From "+split[0] +" to "+split[1];
		timespan.setText(sp);
		span.setText("Duration: "+ev.getSpan()+" minutes.");
		
		
	}
	
	@FXML
	public void intervalButton(){
		
		if(from_date.getValue() == null 
				|| to_date.getValue() == null 
				|| from_hours.getValue() == null 
				|| from_minutes.getValue() == null 
				|| to_hours.getValue() == null 
				|| to_minutes.getValue() == null  ){
			error.setText("Please fill all the requested inputs");
			return ;
		}
		
		LocalDate end_LocDate = from_date.getValue();
		Instant end_instant = Instant.from(end_LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date end_date = Date.from(end_instant);
		Calendar cal_end = Calendar.getInstance();
		cal_end.setTime(end_date);
		
		
		
		
		LocalDate start_LocDate = to_date.getValue();
		Instant start_instant = Instant.from(start_LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date start_date = Date.from(start_instant);
		Calendar cal_start = Calendar.getInstance();
		cal_start.setTime(start_date);
		
		if(cal_end.after(cal_start)){
			error.setText("Please input a valid time period.");
			return ;
		}
		
		ev.addConstraint(new AfterDateConstraint(cal_end));
		System.out.println("After date constraint: "+ cal_end.getTime());
		
		
		ev.addConstraint(new BeforeDateConstraint(cal_start));
		System.out.println("Before date constraint: "+ cal_start.getTime());
		
		
		ev.addConstraint(new AfterHourConstraint(from_hours.getValue(), from_minutes.getValue()));
		System.out.println("After hour constraint: "+ from_hours.getValue() + " " +from_minutes.getValue());

		ev.addConstraint(new BeforeHourConstraint(to_hours.getValue(), to_minutes.getValue()));
		System.out.println("Before hour constraint: "+ to_hours.getValue() + " " +to_minutes.getValue());

		System.out.println("interval button constraint created");
		agent.acceptInvitation(ev);
		 ((Stage) from_hours.getScene().getWindow()).close();
		
	}
	
	

}
