package org.hisrc.distant.onebusaway.gtfs.impl;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.LocalDate;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

public class FilterSingleDayGtfsEntityHandler extends
		DelegatingGtfsEntityHandler {

	protected final ServiceDate serviceDate;
	protected final int dayOfWeek;
	protected final Set<AgencyAndId> availableServiceIds = new HashSet<AgencyAndId>();

	public FilterSingleDayGtfsEntityHandler(GtfsEntityHandler handler,
			int year, int month, int day) {
		super(handler);
		this.serviceDate = new ServiceDate(year, month, day);
		final LocalDate localDate = new LocalDate(year, month, day);
		this.dayOfWeek = localDate.getDayOfWeek();
	}

	@Override
	public void handleServiceCalendar(ServiceCalendar object) {
		addServiceCalendar(object);
		super.handleServiceCalendar(object);
	}

	@Override
	public void handleServiceCalendarDate(ServiceCalendarDate object) {
		addServiceCalendarDate(object);
		super.handleServiceCalendarDate(object);
	}

	@Override
	public void handleStopTime(StopTime object) {
		if (isRelevantTrip(object.getTrip())) {
			super.handleStopTime(object);
		}
	}

	private boolean isRelevantTrip(final Trip trip) {
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
