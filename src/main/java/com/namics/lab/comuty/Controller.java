package com.namics.lab.comuty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.namics.lab.comuty.bs.services.GoogleDirectionsReader;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class Controller {
	
	private GpioController gpio;
	
	private ScheduledExecutorService executor;
	
	public static final String origin = "Teufenerstrasse 19, St. Gallen";
	public static final String destination = "Enge, Zürich";
	public static final int offset_hours = 3;
	public static final String travel_mode = GoogleDirectionsReader.TRANSIT_MODE;
	
	public void startComuty() {
		System.out.println("starting Comuty");
		gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
		pin.low();
		
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new CheckLightstTask(pin, this), 0, 30, TimeUnit.SECONDS);
	}
	
	public void stopComuty() {
		System.out.println("Comuty was stopped");
		executor.shutdown();
	}

}
