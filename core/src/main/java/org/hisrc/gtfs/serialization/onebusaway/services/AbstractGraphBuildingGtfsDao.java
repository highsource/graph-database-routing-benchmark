package org.hisrc.gtfs.serialization.onebusaway.services;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.hisrc.gtfs.graph.servicebuilder.GraphServiceBuilder;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractGraphBuildingGtfsDao extends GtfsDaoImpl {

	private Logger logger = LoggerFactory
			.getLogger(SingleDayGraphBuildingGtfsDao.class);

	protected abstract boolean isRelevantTrip(final Trip trip);

	protected final GraphServiceBuilder graphBuilder;
	private Multimap<Stop, Transfer> incomingTransfersByStop = HashMultimap
			.create();

	public AbstractGraphBuildingGtfsDao(GraphServiceBuilder graphBuilder) {
		this.graphBuilder = Validate.notNull(graphBuilder);
	}

	public void saveEntity(Object entity) {
		if (entity instanceof Stop) {
			super.saveEntity(entity);
			addStop((Stop) entity);
		} else if (entity instanceof Transfer) {
			super.saveEntity(entity);
			addTransfer((Transfer) entity);
		} else if (entity instanceof StopTime) {
			// super.saveEntity(entity);
			addStopTime((StopTime) entity);
		} else {
			super.saveEntity(entity);
		}
	}

	private Multimap<Stop, Transfer> outgoingTransfersByStop = HashMultimap
			.create();
	int count = 0;

	private void addTransfer(Transfer transfer) {
		incomingTransfersByStop.put(transfer.getToStop(), transfer);
		outgoingTransfersByStop.put(transfer.getFromStop(), transfer);
	}

	private Collection<Transfer> findIncomingTransfersByStop(Stop stop) {
		return incomingTransfersByStop.get(stop);
	}

	private Collection<Transfer> findOutgoingTransfersByStop(Stop stop) {
		return outgoingTransfersByStop.get(stop);
	}

	private Map<AgencyAndId, Stop> stopsById = new HashMap<AgencyAndId, Stop>();

	private void addStop(Stop stop) {
		this.stopsById.put(stop.getId(), stop);
	}

	private Stop findRequiredStop(AgencyAndId id) {
		final Stop stop = this.stopsById.get(id);
		if (stop == null) {
			throw new IllegalStateException(MessageFormat.format(
					"Stop for id [{0}] could not be found.", id));
		} else {
			return stop;
		}
	}

	private Stop findParentStationStop(Stop stop) {
		String parentStation = stop.getParentStation();
		if (parentStation == null || parentStation.isEmpty()) {
			return null;
		} else {
			final AgencyAndId parentStopId = new AgencyAndId(stop.getId()
					.getAgencyId(), parentStation);
			return findRequiredStop(parentStopId);
		}
	}

	private Trip lastTrip = null;
	private TemporalVertex lastTripStopDepartureVertex = null;
	private int lastStopSequence = -1;

	private Set<Trip> processedTrips = new HashSet<Trip>();

	private TemporalVertex findPreviousTripStopDepartureVertex(StopTime stopTime) {
		final Trip trip = stopTime.getTrip();
		final int stopSequence = stopTime.getStopSequence();
		final TemporalVertex previousDepartureNode;
		if (trip == lastTrip) {
			if (stopSequence <= lastStopSequence) {
				throw new IllegalStateException(
						"Stop sequence must be greater than the last stop sequence.");
			} else {
				previousDepartureNode = lastTripStopDepartureVertex;
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
		return previousDepartureNode;
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
		final TemporalVertex previousDepartureVertex = findPreviousTripStopDepartureVertex(stopTime);

		final Stop stop = stopTime.getStop();
		final int arrivalTime = stopTime.getArrivalTime();
		final int departureTime = stopTime.getDepartureTime();

		// Add arrival and departure vertices
		final TemporalVertex arrivalVertex = graphBuilder
				.addTripStopTimeVertex(trip, stop, arrivalTime);
		final TemporalVertex departureVertex = graphBuilder
				.addTripStopTimeVertex(trip, stop, departureTime);

		// If there was a previous departure vertex (that is, this stop time is
		// not the first in the sequence, add a ride edge from last departure to
		// current arrival
		if (previousDepartureVertex != null) {
			graphBuilder.addRideEdge(previousDepartureVertex, arrivalVertex,
					arrivalTime - previousDepartureVertex.getTime());
		}

		// Add a "stay in vehicle" edge in between the arrival and the next
		// departure
		if (departureTime > arrivalTime) {
			graphBuilder.addStayEdge(arrivalVertex, departureVertex,
					departureTime - arrivalTime);
		}

		// Add arrival and departure stop time vertices and unboarding and
		// boarding edges
		final TemporalVertex arrivalStopTimeVertex = graphBuilder
				.addStopTimeVertex(stop, arrivalTime);
		graphBuilder.addUnboardEdge(arrivalVertex, arrivalStopTimeVertex);
		addChildParentEdge(stop, arrivalStopTimeVertex, arrivalTime);

		final TemporalVertex departureStopTimeVertex = graphBuilder
				.addStopTimeVertex(stop, departureTime);
		graphBuilder.addBoardEdge(departureStopTimeVertex, departureVertex);
		addParentChildEdge(stop, departureStopTimeVertex, departureTime);

		for (Transfer incomingTransfer : findIncomingTransfersByStop(stop)) {
			if (incomingTransfer.getTransferType() == 2) {
				final int transferTime = incomingTransfer.getMinTransferTime();
				final Stop fromStop = incomingTransfer.getFromStop();
				final int transferStartTime = departureTime - transferTime;
				final TemporalVertex transferToStartVertex = graphBuilder
						.addStopTimeVertex(fromStop, transferStartTime);
				graphBuilder.addTransferEdge(transferToStartVertex,
						departureStopTimeVertex, transferTime);
				addParentChildEdge(fromStop, transferToStartVertex,
						transferStartTime);
			}
		}

		for (Transfer outgoingTransfer : findOutgoingTransfersByStop(stop)) {
			if (outgoingTransfer.getTransferType() == 2) {
				final int transferTime = outgoingTransfer.getMinTransferTime();
				final Stop toStop = outgoingTransfer.getToStop();
				final int transferEndTime = arrivalTime + transferTime;
				final TemporalVertex transferEndVertex = graphBuilder
						.addStopTimeVertex(toStop, transferEndTime);
				graphBuilder.addTransferEdge(arrivalStopTimeVertex,
						transferEndVertex, transferTime);
				addChildParentEdge(toStop, transferEndVertex, transferEndTime);
			}
		}

		lastTrip = stopTime.getTrip();
		lastTripStopDepartureVertex = departureVertex;
		lastStopSequence = stopTime.getStopSequence();
	}

	private void addChildParentEdge(final Stop stop,
			final TemporalVertex stopTimeVertex, final int time) {
		final Stop parentStationStop = findParentStationStop(stop);
		if (parentStationStop != null) {
			TemporalVertex parentStationStopVertex = graphBuilder
					.addStopTimeVertex(parentStationStop, time);
			graphBuilder.addChildParentEdge(stopTimeVertex,
					parentStationStopVertex);
			addChildParentEdge(parentStationStop, parentStationStopVertex, time);
		}
	}

	private void addParentChildEdge(final Stop stop,
			final TemporalVertex stopTimeVertex, final int time) {
		final Stop parentStationStop = findParentStationStop(stop);
		if (parentStationStop != null) {
			TemporalVertex parentStationStopVertex = graphBuilder
					.addStopTimeVertex(parentStationStop, time);
			graphBuilder.addParentChildEdge(parentStationStopVertex,
					stopTimeVertex);
			addParentChildEdge(parentStationStop, parentStationStopVertex, time);
		}
	}

}