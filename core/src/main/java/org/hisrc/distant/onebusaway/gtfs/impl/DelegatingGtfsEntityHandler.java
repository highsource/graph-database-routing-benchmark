package org.hisrc.distant.onebusaway.gtfs.impl;

import org.apache.commons.lang3.Validate;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.FareRule;
import org.onebusaway.gtfs.model.FeedInfo;
import org.onebusaway.gtfs.model.Frequency;
import org.onebusaway.gtfs.model.Pathway;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;

public class DelegatingGtfsEntityHandler implements GtfsEntityHandler {

	private final GtfsEntityHandler handler;

	public DelegatingGtfsEntityHandler() {
		this(new DefaultGtfsEntityHandler());
	}

	public DelegatingGtfsEntityHandler(GtfsEntityHandler handler) {
		this.handler = Validate.notNull(handler);
	}

	@Override
	public void handleAgency(Agency object) {
		handler.handleAgency(object);
	}

	@Override
	public void handleShapePoint(ShapePoint object) {
		handler.handleShapePoint(object);
	}

	@Override
	public void handleRoute(Route object) {
		handler.handleRoute(object);
	}

	@Override
	public void handleStop(Stop object) {
		handler.handleStop(object);
	}

	@Override
	public void handleTrip(Trip object) {
		handler.handleTrip(object);
	}

	@Override
	public void handleStopTime(StopTime object) {
		handler.handleStopTime(object);
	}

	@Override
	public void handleServiceCalendar(ServiceCalendar object) {
		handler.handleServiceCalendar(object);
	}

	@Override
	public void handleServiceCalendarDate(ServiceCalendarDate object) {
		handler.handleServiceCalendarDate(object);
	}

	@Override
	public void handleFareAttribute(FareAttribute object) {
		handler.handleFareAttribute(object);
	}

	@Override
	public void handleFareRule(FareRule object) {
		handler.handleFareRule(object);
	}

	@Override
	public void handleFrequency(Frequency object) {
		handler.handleFrequency(object);
	}

	@Override
	public void handlePathway(Pathway object) {
		handler.handlePathway(object);
	}

	@Override
	public void handleTransfer(Transfer object) {
		handler.handleTransfer(object);
	}

	@Override
	public void handleFeedInfo(FeedInfo object) {
		handler.handleFeedInfo(object);
	}
}
