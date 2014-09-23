package com.namics.lab.comuty;

import java.util.Date;

import com.namics.lab.comuty.bs.services.GoogleDirectionsReader;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		GoogleDirectionsReader gr = new GoogleDirectionsReader("Saint Gallen", "Zurich", GoogleDirectionsReader.DRIVING_MODE);
		Date now = new Date();
		now.setHours(now.getHours()+2);
		gr.getDirectionsArrivalTime(now.getTime());
	}

}
