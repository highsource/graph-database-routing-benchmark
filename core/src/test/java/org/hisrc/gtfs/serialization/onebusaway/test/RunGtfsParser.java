package org.hisrc.gtfs.serialization.onebusaway.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipFile;

import org.hisrc.gtfs.graph.builder.GraphBuilder;
import org.hisrc.gtfs.graph.builder.jgrapht.JGraphTGraphBuilder;
import org.hisrc.gtfs.serialization.onebusaway.GtfsReader;
import org.hisrc.gtfs.serialization.onebusaway.services.SingleDayGraphBuildingGtfsDao;
import org.junit.Test;
import org.onebusaway.csv_entities.ZipFileCsvInputSource;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.services.GtfsMutableDao;

public class RunGtfsParser {

	// @Test
	// public void parsesSWU() throws IOException, URISyntaxException {
	// final GtfsReader gtfsReader = new GtfsReader();
	// gtfsReader.getEntityClasses().remove(StopTime.class);
	// gtfsReader.getEntityClasses().add(StopTime.class);
	// final ZipFile zipFile = new ZipFile(new File("src/main/etc/swu.zip"));
	// final ZipFileCsvInputSource csvInputSource = new ZipFileCsvInputSource(
	// zipFile);
	// gtfsReader.setInputSource(csvInputSource);
	//
	// final GraphBuilder graphBuilder = new JGraphTGraphBuilder();
	// final GtfsMutableDao dao = new
	// SingleDayGraphBuildingGtfsDao(graphBuilder, 2015,
	// 07, 10);
	// gtfsReader.setEntityStore(dao);
	// gtfsReader.run();
	// }

	@Test
	public void parsesVBB() throws IOException, URISyntaxException {
		final GtfsReader gtfsReader = new GtfsReader();
		gtfsReader.getEntityClasses().remove(StopTime.class);
		gtfsReader.getEntityClasses().add(StopTime.class);
		final ZipFile zipFile = new ZipFile(new File("src/main/etc/vbb.zip"));
		final ZipFileCsvInputSource csvInputSource = new ZipFileCsvInputSource(
				zipFile);
		gtfsReader.setInputSource(csvInputSource);

		final GraphBuilder graphBuilder = new JGraphTGraphBuilder();
		final GtfsMutableDao dao = new SingleDayGraphBuildingGtfsDao(
				graphBuilder, 2015, 07, 10);
		gtfsReader.setEntityStore(dao);
		gtfsReader.run();
	}

}
