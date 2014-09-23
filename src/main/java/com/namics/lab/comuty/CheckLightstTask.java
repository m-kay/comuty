package com.namics.lab.comuty;

import java.util.Date;

import com.namics.lab.comuty.bs.services.GoogleDirectionsReader;
import com.namics.lab.comuty.bs.services.data.Route;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class CheckLightstTask implements Runnable{
	
	private final GpioPinDigitalOutput pin;
	private Date arrivalDate = null;
	private static final int TRESHOLD_IN_MS = 300000;
	private Controller controller = null;
	
	public CheckLightstTask(GpioPinDigitalOutput pin, Controller controller){
		this.pin = pin;
		this.controller = controller;
	}

	@Override
	public void run() {
		System.out.println("get data task");
		
		int durationInSeconds = getDurationOfTravel();
		if (durationInSeconds == -1)
			return;
		
		Date startTravelTime = new Date(arrivalDate.getTime()-durationInSeconds*1000);
		Date now = new Date();
		long timeToGo = startTravelTime.getTime()-now.getTime();
		if (timeToGo < TRESHOLD_IN_MS && timeToGo >= 0) {
			pin.high();
			System.out.println("you should go now!");
		}
		else {
			if (timeToGo < 0) {
				pin.low();
				controller.stopComuty();
				System.out.println("If you are still here, you missed your connection!");
			}
		}

	}
	
	private int getDurationOfTravel() {
		GoogleDirectionsReader reader = new GoogleDirectionsReader(Controller.origin, Controller.destination, Controller.travel_mode);
		if (arrivalDate == null) {
			arrivalDate = new Date();
			arrivalDate.setHours(arrivalDate.getHours() + Controller.offset_hours);
		}
		
		
		Route route = reader.getDirectionsArrivalTime(arrivalDate.getTime()/1000);
		
		if (route != null) {
			System.out.println("data collection successful");
			return route.getDuration();
		}
		else {
			System.out.println("There was an error");
			return -1;
		}
	}

}
