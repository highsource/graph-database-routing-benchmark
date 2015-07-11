package org.hisrc.gtfs.graph.builder.jgrapht;

import java.util.HashSet;
import java.util.Set;

import org.hisrc.gtfs.graph.model.TemporalStopNode;
import org.hisrc.gtfs.graph.model.TransitionEdge;
import org.hisrc.gtfs.graph.model.TransitionType;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedMultigraph;
import org.joda.time.LocalDate;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsMutableDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GtfsDirectedGraphBuilder {

	private final ServiceDate serviceDate;
	private final int dayOfWeek;
	private final Set<AgencyAndId> availableServiceIds = new HashSet<AgencyAndId>();

	public GtfsDirectedGraphBuilder(int year, int month, int day) {
		this.serviceDate = new ServiceDate(year, month, day);
		final LocalDate localDate = new LocalDate(year, month, day);
		this.dayOfWeek = localDate.getDayOfWeek();
	}

	private DirectedGraph<TemporalStopNode, TransitionEdge> graph = new DirectedMultigraph<TemporalStopNode, TransitionEdge>(
			new EdgeFactory<TemporalStopNode, TransitionEdge>() {
				@Override
				public TransitionEdge createEdge(TemporalStopNode start,
						TemporalStopNode stop) {
					throw new UnsupportedOperationException();
				}
			});

	private Logger logger = LoggerFactory
			.getLogger(GtfsDirectedGraphBuilder.class);

	private final GtfsMutableDao gtfsDao = new GtfsDaoImpl() {
		public void saveEntity(Object entity) {
			super.saveEntity(entity);
			if (entity instanceof ServiceCalendarDate) {
				addServiceCalendarDate((ServiceCalendarDate) entity);
			}
			if (entity instanceof ServiceCalendar) {
				addServiceCalendar((ServiceCalendar) entity);
			}
			if (entity instanceof StopTime) {
				addStopTime((StopTime) entity);
			}
		}

	};

	private Set<Trip> processedTrips = new HashSet<Trip>();
	private Trip lastTrip = null;
	private TemporalStopNode lastNode = null;
	private int lastStopSequence = -1;

	int count = 0;

	public void addStopTime(StopTime stopTime) {

		final Trip trip = stopTime.getTrip();
		if (!availableServiceIds.contains(trip.getServiceId())) {
			return;
		}

		final int stopSequence = stopTime.getStopSequence();
		// logger.info("Stop sequence [" + stopSequence + "].");
		final TemporalStopNode previousDepartureNode;
		if (trip == lastTrip) {
			if (stopSequence <= lastStopSequence) {
				throw new IllegalStateException(
						"Stop sequence must be greater than the last stop sequence.");
			} else {
				previousDepartureNode = lastNode;
			}
		} else {
			if (processedTrips.contains(trip)) {
				throw new IllegalStateException(
						"Trip was already processed and now appears again.");
			} else {
				processedTrips.add(lastTrip);
				previousDepartureNode = null;
			}
		}

		final Stop stop = stopTime.getStop();
		final int arrivalTime = stopTime.getArrivalTime();
		final int departureTime = stopTime.getDepartureTime();

		final TemporalStopNode arrivalNode = new TemporalStopNode(stop,
				arrivalTime, false);
		final TemporalStopNode departureNode = new TemporalStopNode(stop,
				departureTime, true);
		graph.addVertex(arrivalNode);
		graph.addVertex(departureNode);

		if (previousDepartureNode != null) {
			final int previousDepartureTime = previousDepartureNode.getTime();
			final TransitionEdge departureArrivalTransitionEdge = new TransitionEdge(
					TransitionType.DEPARTURE_ARRIVAL, arrivalTime
							- previousDepartureTime);

			// logger.info("Adding [" + previousDepartureNode + "--->"
			// + arrivalNode + "]");
			graph.addEdge(previousDepartureNode, arrivalNode,
					departureArrivalTransitionEdge);
		}
		// logger.info("Adding [" + arrivalNode + "-" + departureNode + "]");
		final TransitionEdge arrivalDepartureTransitionEdge = new TransitionEdge(
				TransitionType.ARRIVAL_DEPARTURE, departureTime - arrivalTime);
		graph.addEdge(arrivalNode, departureNode,
				arrivalDepartureTransitionEdge);

		lastTrip = trip;
		lastNode = departureNode;
		lastStopSequence = stopTime.getStopSequence();
		if (count % 10000 == 0) {
			logger.info("" + count);
		}
		count++;
	}

	private void addServiceCalendar(ServiceCalendar serviceCalendar) {
		if (serviceCalendar.getStartDate().compareTo(this.serviceDate) <= 0
				&& serviceCalendar.getEndDate().compareTo(this.serviceDate) >= 0
				&& (this.dayOfWeek == 1 && serviceCalendar.getMonday() == 1)
				|| (this.dayOfWeek == 2 && serviceCalendar.getTuesday() == 1)
				|| (this.dayOfWeek == 3 && serviceCalendar.getWednesday() == 1)
				|| (this.dayOfWeek == 4 && serviceCalendar.getThursday() == 1)
				|| (this.dayOfWeek == 5 && serviceCalendar.getFriday() == 1)
				|| (this.dayOfWeek == 6 && serviceCalendar.getSaturday() == 1)
				|| (this.dayOfWeek == 7 && serviceCalendar.getSunday() == 1)) {
			availableServiceIds.add(serviceCalendar.getServiceId());
		}
	}

	private void addServiceCalendarDate(ServiceCalendarDate serviceCalendarDate) {
		if (serviceCalendarDate.getExceptionType() == 1
				&& serviceCalendarDate.getDate().equals(this.serviceDate)) {
			availableServiceIds.add(serviceCalendarDate.getServiceId());
		}
	}

	public GtfsMutableDao getGtfsMutableDao() {
		return gtfsDao;
	}

	public DirectedGraph<TemporalStopNode, TransitionEdge> build() {
		return graph;
	}
}
