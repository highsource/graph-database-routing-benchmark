package org.hisrc.gtfs.serialization.onebusaway.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipFile;

import org.hisrc.gtfs.graph.model.vertex.TemporalVertex;
import org.hisrc.gtfs.graph.service.GraphService;
import org.hisrc.gtfs.graph.servicebuilder.GraphServiceBuilder;
import org.hisrc.gtfs.graph.servicebuilder.jgrapht.JGraphTGraphServiceBuilder;
import org.hisrc.gtfs.serialization.onebusaway.GtfsReader;
import org.hisrc.gtfs.serialization.onebusaway.services.SingleDayGraphBuildingGtfsDao;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.csv_entities.ZipFileCsvInputSource;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.services.GtfsMutableDao;

public class RunGtfsParser {

	@Test
	public void parsesSWU() throws IOException, URISyntaxException {
		final GtfsReader gtfsReader = new GtfsReader();
		gtfsReader.getEntityClasses().remove(StopTime.class);
		gtfsReader.getEntityClasses().add(StopTime.class);
		final ZipFile zipFile = new ZipFile(new File("src/main/etc/swu.zip"));
		final ZipFileCsvInputSource csvInputSource = new ZipFileCsvInputSource(
				zipFile);
		gtfsReader.setInputSource(csvInputSource);

		final GraphServiceBuilder graphBuilder = new JGraphTGraphServiceBuilder();
		final GtfsMutableDao dao = new SingleDayGraphBuildingGtfsDao(
				graphBuilder, 2015, 07, 10);
		gtfsReader.setEntityStore(dao);
		gtfsReader.run();
		final GraphService graphService = graphBuilder.build();

//		final int startTime = 12 * 60 * 60;
//		final TemporalVertex start = graphService
//				.findLatestTemporalVertexByStopIdBefore("SWU_900107011",
//						startTime);
//		Assert.assertNotNull(start);
//		Assert.assertTrue(start.getTime() <= startTime);
//
//		final int endTime = 14 * 60 * 60;
//		final TemporalVertex end = graphService
//				.findLatestTemporalVertexByStopIdBefore("SWU_9001635",
//						startTime);

		graphService.findShortestPathStartingAfter("SWU_9001070",
				"SWU_9001635", 12 * 60 * 60);

	}

	// @Test
	// public void parsesVBB() throws IOException, URISyntaxException {
	// final GtfsReader gtfsReader = new GtfsReader();
	// gtfsReader.getEntityClasses().remove(StopTime.class);
	// gtfsReader.getEntityClasses().add(StopTime.class);
	// final ZipFile zipFile = new ZipFile(new File("src/main/etc/vbb.zip"));
	// final ZipFileCsvInputSource csvInputSource = new ZipFileCsvInputSource(
	// zipFile);
	// gtfsReader.setInputSource(csvInputSource);
	//
	// final GraphBuilder graphBuilder = new JGraphTGraphBuilder();
	// final GtfsMutableDao dao = new SingleDayGraphBuildingGtfsDao(
	// graphBuilder, 2015, 07, 10);
	// gtfsReader.setEntityStore(dao);
	// gtfsReader.run();
	//
	// graphBuilder.build();
	// }

}
