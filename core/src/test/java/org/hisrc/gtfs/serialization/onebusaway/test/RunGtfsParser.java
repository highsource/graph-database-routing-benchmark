package org.hisrc.gtfs.serialization.onebusaway.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipFile;

import org.hisrc.distant.graph.model.edge.Transition;
import org.hisrc.distant.graph.model.edge.TransitionEdge;
import org.hisrc.distant.graph.model.vertex.StopVertex;
import org.hisrc.distant.jgrapht.TimeDependentGraphPath;
import org.hisrc.distant.onebusaway.gtfs.impl.FilterSingleDayGtfsEntityHandler;
import org.hisrc.distant.onebusaway.gtfs.impl.GraphBuildingGtfsEntityHandler;
import org.hisrc.distant.onebusaway.gtfs.impl.GtfsEntityHandlingDaoImpl;
import org.hisrc.distant.onebusaway.gtfs.serialization.GtfsReader;
import org.hisrc.distant.service.GraphService;
import org.hisrc.distant.service.impl.GraphServiceImpl;
import org.jgrapht.DirectedGraph;
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

		final GraphService service = new GraphServiceImpl();

		final DirectedGraph<StopVertex, TransitionEdge> graph = service
				.getGraph();
		final GtfsMutableDao dao = new GtfsEntityHandlingDaoImpl(
				new FilterSingleDayGtfsEntityHandler(
						new GraphBuildingGtfsEntityHandler(graph), 2015, 07, 10));
		gtfsReader.setEntityStore(dao);
		gtfsReader.run();

		Assert.assertEquals(773, graph.vertexSet().size());
		Assert.assertEquals(1650, graph.edgeSet().size());

		final String ulmOstpreussenwegStopId = "SWU_9001070";
		Assert.assertNotNull(service.findVertexById(ulmOstpreussenwegStopId));
		String wiblingenReutlingerStrasseStopId = "SWU_9001635";
		Assert.assertNotNull(service
				.findVertexById(wiblingenReutlingerStrasseStopId));

		String ulmHauptbahnhofStopId = "SWU_9001008";
		Assert.assertNotNull(service.findVertexById(ulmHauptbahnhofStopId));

		final long start = System.currentTimeMillis();
		final TimeDependentGraphPath<StopVertex, TransitionEdge, Transition> findEarliestArrivalPath = service
				.findEarliestArrivalPath(ulmOstpreussenwegStopId,
						wiblingenReutlingerStrasseStopId, 12 * 60 * 60);
		final long end = System.currentTimeMillis();
		System.out.println(end - start);
		Assert.assertNotNull(findEarliestArrivalPath);

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
