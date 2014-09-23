package com.namics.lab.comuty.bs.services;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.namics.lab.comuty.bs.services.data.Route;

public class GoogleDirectionsReader {

	private final static String baseURL = "http://maps.googleapis.com/maps/api/directions/json?";
	public final static String DRIVING_MODE = "driving";
	public final static String WALKING_MODE = "walking";
	public final static String TRANSIT_MODE = "transit";

	private String origin = "";
	private String destination = "";
	private String mode = "";

	public GoogleDirectionsReader(String origin, String destination, String mode) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.mode = mode;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void getDirectionsDepartureTime(long departure_time) {
		String request = buildRequestURL("departure_time", departure_time);

		this.executeRequest(request);

	}

	public void getDirectionsArrivalTime(long arrival_time) {
		String request = buildRequestURL("arrival_time", arrival_time);

		this.executeRequest(request);
	}

	private String buildRequestURL(String time_key, long time) {
		String request = "";
		try {
			request += baseURL + "&" + "origin="
					+ URLEncoder.encode(origin, "UTF-8") + "&" + "destination="
					+ URLEncoder.encode(destination, "UTF-8") + "&" + "mode="
					+ mode + "&" + time_key + "=" + time;

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return request;
	}

	private void executeRequest(String request) {
		HttpClient httpClient = HttpClients.createDefault();

		try {

			HttpGet httpGetRequest = new HttpGet(request);

			System.out.println("Executing Request: " + request);

			// Execute HTTP request
			HttpResponse httpResponse = httpClient.execute(httpGetRequest);

			System.out.println("----------------------------------------");
			System.out.println(httpResponse.getStatusLine());
			System.out.println("----------------------------------------");

			// Get hold of the response entity
			HttpEntity entity = httpResponse.getEntity();

			// If the response does not enclose an entity, there is no need
			// to bother about connection release
			byte[] buffer = new byte[1024];
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				try {
					// int bytesRead = 0;
					// BufferedInputStream bis = new BufferedInputStream(
					// inputStream);
					// while ((bytesRead = bis.read(buffer)) != -1) {
					// String chunk = new String(buffer, 0, bytesRead);
					// System.out.println(chunk);
					// }

					this.parseResponse(inputStream);

				} catch (IOException ioException) {
					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					ioException.printStackTrace();
				} catch (RuntimeException runtimeException) {
					// In case of an unexpected exception you may want to abort
					// the HTTP request in order to shut down the underlying
					// connection immediately.
					httpGetRequest.abort();
					runtimeException.printStackTrace();
				} finally {
					// Closing the input stream will trigger connection release
					try {
						inputStream.close();
					} catch (Exception ignore) {
					}
				}
			}
		} catch (ClientProtocolException e) {
			// thrown by httpClient.execute(httpGetRequest)
			e.printStackTrace();
		} catch (IOException e) {
			// thrown by entity.getContent();
			e.printStackTrace();
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpClient.getConnectionManager().shutdown();
		}
	}

	private void parseResponse(InputStream response) throws IOException {

		JsonReader reader = new JsonReader(new InputStreamReader(response));

		List<Route> routes = null;
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("routes")) {
				routes = readRoutesArray(reader);
			} else {
				reader.skipValue();
			}
		}
	}

	private List<Route> readRoutesArray(JsonReader reader) throws IOException {
		List<Route> routes = new ArrayList<Route>();

		reader.beginArray();
		while (reader.hasNext()) {
			routes.add(readRoute(reader));
		}
		reader.endArray();
		return routes;
	}

	private Route readRoute(JsonReader reader) throws IOException {
		Route route = null;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("bounds")) {
				reader.skipValue();
			} else if (name.equals("copyrights")) {
				reader.skipValue();
			} else if (name.equals("legs") && reader.peek() != JsonToken.NULL) {
				route = readRouteInformation(reader);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return route;
	}

//	legs is array but only first element is parsed as legs are supposed to be 1 in prototype
	private Route readRouteInformation(JsonReader reader) throws IOException {
		Route route = new Route();

		reader.beginArray();
		reader.hasNext();
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("distance")) {
				reader.beginObject();
				reader.nextName();
				reader.skipValue();
				reader.nextName();
				route.setDistance(reader.nextDouble());
				reader.endObject();
			} else if (name.equals("duration")) {
				reader.beginObject();
				reader.nextName();
				reader.skipValue();
				reader.nextName();
				route.setDuration(reader.nextInt());
				reader.endObject();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		reader.endArray();

		return route;
	}
}
