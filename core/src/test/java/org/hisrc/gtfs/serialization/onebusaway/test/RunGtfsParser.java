package org.hisrc.gtfs.serialization.onebusaway.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipFile;

import org.hisrc.distant.graph.model.edge.TransitEdge;
import org.hisrc.distant.graph.model.vertex.StopVertex;
import org.hisrc.distant.jgrapht.FailingEdgeFactory;
import org.hisrc.distant.onebusaway.gtfs.impl.FilterSingleDayGtfsEntityHandler;
import org.hisrc.distant.onebusaway.gtfs.impl.GraphBuildingGtfsEntityHandler;
import org.hisrc.distant.onebusaway.gtfs.impl.GtfsEntityHandlingDaoImpl;
import org.hisrc.distant.onebusaway.gtfs.serialization.GtfsReader;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedPseudograph;
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

		final DirectedGraph<StopVertex, TransitEdge> graph = new DirectedPseudograph<StopVertex, TransitEdge>(
				FailingEdgeFactory.<StopVertex, TransitEdge> create());

		final GtfsMutableDao dao = new GtfsEntityHandlingDaoImpl(
				new FilterSingleDayGtfsEntityHandler(
						new GraphBuildingGtfsEntityHandler(graph), 2015, 07, 10));
		gtfsReader.setEntityStore(dao);
		gtfsReader.run();

		Assert.assertEquals(773, graph.vertexSet().size());
		Assert.assertEquals(36743, graph.edgeSet().size());
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
