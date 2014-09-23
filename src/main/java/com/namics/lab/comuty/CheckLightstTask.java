package com.namics.lab.comuty;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class CheckLightstTask implements Runnable{
	
	private final GpioPinDigitalOutput pin;
	
	public CheckLightstTask(GpioPinDigitalOutput pin){
		this.pin = pin;
	}

	@Override
	public void run() {
		System.out.println("toggle led");
		pin.toggle();
	}

}
