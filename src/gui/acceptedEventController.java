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
	Label timespan;
	
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
	DatePicker date;

	@FXML
	ChoiceBox<Integer> hour;

	@FXML
	ChoiceBox<Integer> minute;
	

	MyAgent agent;
	MyEvent ev;

	public acceptedEventController(MyAgent agent, MyEvent ev) {

		this.ev = ev;
		this.agent = agent;

	}

	@FXML
	protected void initialize() {

		for(int i=0;i<61;i++){
			from_minutes.getItems().add(i);
			to_minutes.getItems().add(i);
			minute.getItems().add(i);
		}
		for(int i=0;i<24;i++){
			from_hours.getItems().add(i);
			to_hours.getItems().add(i);
			hour.getItems().add(i);
		}
		
		String[] split=(""+ev.getDateProposal()).split(",");
		String sp="From "+split[0] +" to "+split[1];
		timespan.setText(sp);
		
	}
	
	@FXML
	public void intervalButton(){
		LocalDate end_LocDate = from_date.getValue();
		Instant end_instant = Instant.from(end_LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date end_date = Date.from(end_instant);
		Calendar cal_end = Calendar.getInstance();
		cal_end.setTime(end_date);
		
		
		ev.addConstraint(new AfterDateConstraint(cal_end));
		
		LocalDate start_LocDate = to_date.getValue();
		Instant start_instant = Instant.from(start_LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date start_date = Date.from(start_instant);
		Calendar cal_start = Calendar.getInstance();
		cal_start.setTime(start_date);
		
		
		ev.addConstraint(new BeforeDateConstraint(cal_start));
		
		
		ev.addConstraint(new AfterHourConstraint(from_hours.getValue(), from_minutes.getValue()));

		ev.addConstraint(new BeforeHourConstraint(to_hours.getValue(), to_minutes.getValue()));
		System.out.println("interval button constraint created");
		agent.acceptInvitation(ev);
		 ((Stage) from_hours.getScene().getWindow()).close();
		
	}
	@FXML
	public void specificButton(){
		LocalDate LocDate = date.getValue();
		Instant instant = Instant.from(LocDate.atStartOfDay(ZoneId.systemDefault()));
		Date date = Date.from(instant);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR,hour.getValue());
		cal.set(Calendar.MINUTE,minute.getValue());
		
		ev.addConstraint(new SpecificDateConstraint(cal));
		
		System.out.println("specific button constraint created");
		agent.acceptInvitation(ev);
		
		((Stage) from_hours.getScene().getWindow()).close();
	}
	

}
