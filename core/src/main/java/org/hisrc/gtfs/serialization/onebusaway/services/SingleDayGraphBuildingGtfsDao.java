package org.hisrc.gtfs.serialization.onebusaway.services;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.hisrc.gtfs.graph.builder.GraphBuilder;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.joda.time.LocalDate;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleDayGraphBuildingGtfsDao extends GtfsDaoImpl {

	private Logger logger = LoggerFactory
			.getLogger(SingleDayGraphBuildingGtfsDao.class);

	private final GraphBuilder graphBuilder;
	private final ServiceDate serviceDate;
	private final int dayOfWeek;
	private final Set<AgencyAndId> availableServiceIds = new HashSet<AgencyAndId>();

	public SingleDayGraphBuildingGtfsDao(GraphBuilder graphBuilder, int year,
			int month, int day) {
		super();
		this.graphBuilder = Validate.notNull(graphBuilder);
		this.serviceDate = new ServiceDate(year, month, day);
		final LocalDate localDate = new LocalDate(year, month, day);
		this.dayOfWeek = localDate.getDayOfWeek();
	}

	public void saveEntity(Object entity) {
		super.saveEntity(entity);
		if (entity instanceof Stop) {
			addStop((Stop) entity);
		}
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

	int count = 0;

	private Map<AgencyAndId, Stop> stopMap = new HashMap<AgencyAndId, Stop>();

	private void addStop(Stop stop) {
		this.stopMap.put(stop.getId(), stop);
	}

	private Stop findRequiredStop(AgencyAndId id) {
		final Stop stop = this.stopMap.get(id);
		if (stop == null) {
			throw new IllegalStateException(MessageFormat.format(
					"Stop for id [{0}] could not be found.", id));
		} else {
			return stop;
		}
	}

	private Stop findParentStationStop(Stop stop) {
		if (stop.getParentStation().isEmpty()) {
			return null;
		} else {
			final AgencyAndId parentStopId = new AgencyAndId(stop.getId()
					.getAgencyId(), stop.getParentStation());
			return findRequiredStop(parentStopId);
		}
	}

	public void addStopTime(StopTime stopTime) {
		if (count % 10000 == 0) {
			logger.info("" + count);
		}
		count++;

		final Trip trip = stopTime.getTrip();
		// Filter out the trip if it is not relevant
		if (!isRelevantTrip(trip)) {
			return;
		}

		// Find the previous departure vertex
		final TemporalVertex previousDepartureVertex = graphBuilder.findPreviousTripStopDepartureVertex(stopTime);

		final Stop stop = stopTime.getStop();
		final int arrivalTime = stopTime.getArrivalTime();
		final int departureTime = stopTime.getDepartureTime();

		// Add arrival and departure vertices
		final TemporalVertex arrivalVertex = graphBuilder
				.addTripStopArrivalVertex(stopTime);
		final TemporalVertex departureVertex = graphBuilder
				.addTripStopDepartureVertex(stopTime);

		// If there was a previous departure vertex (that is, this stop time is
		// not the first in the sequence, add a ride edge from last departure to
		// current arrival
		if (previousDepartureVertex != null) {
			graphBuilder.addRideEdge(previousDepartureVertex, arrivalVertex,
					arrivalTime - previousDepartureVertex.getTime());
		}
		
		// Add a "stay in vehicle" edge in between the arrival and the next departure
		graphBuilder.addStayEdge(arrivalVertex, departureVertex, departureTime
				- arrivalTime);

		// Add arrival and departure stop time vertices and unboarding and boarding edges
		final TemporalVertex arrivalStopTimeVertex = graphBuilder.addStopTimeVertex(stop,
				arrivalTime);
		graphBuilder.addUnboardEdge(arrivalVertex, arrivalStopTimeVertex);
		final TemporalVertex departureStopTimeVertex = graphBuilder.addStopTimeVertex(stop,
				departureTime);
		graphBuilder.addBoardEdge(departureStopTimeVertex, departureVertex);

		Stop parentStationStop = findParentStationStop(stop);

		if (parentStationStop != null) {
			final TemporalVertex parentStationArrivalVertex = graphBuilder
					.addParentStationVertex(parentStationStop,
							arrivalVertex.getTime());
			final TemporalVertex parentStationDepartureVertex = graphBuilder
					.addParentStationVertex(parentStationStop,
							departureVertex.getTime());
			graphBuilder.addChildParentEdge(arrivalVertex,
					parentStationArrivalVertex);
			graphBuilder.addParentChildEdge(departureVertex,
					parentStationDepartureVertex);
		}
	}

	public boolean isRelevantTrip(final Trip trip) {
		return availableServiceIds.contains(trip.getServiceId());
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
}
