package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {

	private Airline myAirline;
	private List<Airport> reachedAirports;

	private List<Airport> allAirports;
	private List<Airline> allAirlines;

	private Map<Integer, Airport> airportMap;
	private Map<Integer, Airline> airlineMap;

	private SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> graph;

	public Model() {
		FlightDAO dao = new FlightDAO();

		this.allAirlines = dao.getAllAirlines();
		this.allAirports = dao.getAllAirports();

		// populate a map AirportId->Airport
		this.airportMap = new HashMap<>();
		for (Airport a : allAirports)
			airportMap.put(a.getAirportId(), a);

		// populate a map AirlineId->Airline
		this.airlineMap = new HashMap<>();
		for (Airline a : allAirlines)
			airlineMap.put(a.getAirlineId(), a);

	}

	public List<Airport> getReachedAirports(Airline airline) {
		if (this.myAirline == null || !this.myAirline.equals(airline)) {

			this.myAirline = airline;
			System.out.println("Searching " + airline.toString() + "\n");

			FlightDAO dao = new FlightDAO();

			List<Integer> airportIds = dao.getReachedAirportsID(this.myAirline);
			System.out.println("Found " + airportIds.size() + " airports\n");

			this.reachedAirports = new ArrayList<Airport>();
			for (Integer id : airportIds)
				this.reachedAirports.add(airportMap.get(id));

			this.reachedAirports.sort(new Comparator<Airport>() {
				@Override
				public int compare(Airport o1, Airport o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

		}

		return this.reachedAirports;
	}

	public List<Airport> getReachedAirports() {
		return reachedAirports;
	}

	public List<Airport> getAllAirports() {
		return allAirports;
	}

	public List<Airline> getAllAirlines() {
		return allAirlines;
	}

	public void buildGraph(Airline airline) {
		this.graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);

		Graphs.addAllVertices(graph, this.allAirports);

		FlightDAO dao = new FlightDAO();
		List<Route> routes = dao.getRoutesByAirline(airline);

		for (Route r : routes) {
			if (r.getSourceAirportId() != 0 && r.getDestinationAirportId() != 0) {
				Airport a1 = airportMap.get(r.getSourceAirportId());
				Airport a2 = airportMap.get(r.getDestinationAirportId());

				if (a1 != null && a2 != null) {
					
					LatLng c1 = new LatLng(a1.getLatitude(), a1.getLongitude()) ;
					LatLng c2 = new LatLng(a2.getLatitude(), a2.getLongitude()) ;
					double distance = LatLngTool.distance(c1, c2, LengthUnit.KILOMETER) ;

					Graphs.addEdge(graph, a1, a2, distance);
					System.out.format("%s->%s %.0fkm\n",a1,a2,distance);

				}
			}
		}

	}

}
