package org.hisrc.distant.onebusaway.gtfs.impl;

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

public interface GtfsEntityHandler {

	public void handleAgency(Agency object);

	public void handleShapePoint(ShapePoint object);

	public void handleRoute(Route object);

	public void handleStop(Stop object);

	public void handleTrip(Trip object);

	public void handleStopTime(StopTime object);

	public void handleServiceCalendar(ServiceCalendar object);

	public void handleServiceCalendarDate(ServiceCalendarDate object);

	public void handleFareAttribute(FareAttribute object);

	public void handleFareRule(FareRule object);

	public void handleFrequency(Frequency object);

	public void handlePathway(Pathway object);

	public void handleTransfer(Transfer object);

	public void handleFeedInfo(FeedInfo object);

}
