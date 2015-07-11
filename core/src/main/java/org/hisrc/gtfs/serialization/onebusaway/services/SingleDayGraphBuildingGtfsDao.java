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

	private Set<Trip> processedTrips = new HashSet<Trip>();
	private Trip lastTrip = null;
	private TemporalVertex lastNode = null;
	private int lastStopSequence = -1;

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

		final Trip trip = stopTime.getTrip();
		if (!availableServiceIds.contains(trip.getServiceId())) {
			return;
		}

		final int stopSequence = stopTime.getStopSequence();
		// logger.info("Stop sequence [" + stopSequence + "].");
		final TemporalVertex previousDepartureNode;
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

		final TemporalVertex arrivalNode = graphBuilder.addArrivalVertex(stop,
				arrivalTime);
		final TemporalVertex departureNode = graphBuilder.addDepartureVertex(
				stop, departureTime);

		Stop parentStationStop = findParentStationStop(stop);

		if (parentStationStop != null) {
			final TemporalVertex parentStationArrivalVertex = graphBuilder
					.addParentStationVertex(parentStationStop,
							arrivalNode.getTime());
			final TemporalVertex parentStationDepartureVertex = graphBuilder
					.addParentStationVertex(parentStationStop,
							departureNode.getTime());
			graphBuilder.addChildParentEdge(arrivalNode,
					parentStationArrivalVertex);
			graphBuilder.addParentChildEdge(departureNode,
					parentStationDepartureVertex);
		}

		if (previousDepartureNode != null) {
			final int previousDepartureTime = previousDepartureNode.getTime();
			graphBuilder.addDepartureArrivalEdge(previousDepartureNode,
					arrivalNode, arrivalTime - previousDepartureTime);
		}
		graphBuilder.addArrivalDepartureEdge(arrivalNode, departureNode,
				departureTime - arrivalTime);

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
}
