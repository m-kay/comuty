package com.namics.lab.comuty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

@WebListener
public class Initializer implements ServletContextListener {

	private GpioController gpio;
	
	private ScheduledExecutorService executor;
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if(executor != null){
			executor.shutdownNow();
		}
		
		if(gpio != null){
			gpio.shutdown();
		}

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("starting lights");
		gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);
		pin.low();
		
		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new CheckLightstTask(pin), 0, 2, TimeUnit.SECONDS);
	}

}
