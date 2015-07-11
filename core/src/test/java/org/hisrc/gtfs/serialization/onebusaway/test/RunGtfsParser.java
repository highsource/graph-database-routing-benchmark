package org.hisrc.gtfs.serialization.onebusaway.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.zip.ZipFile;

import org.hisrc.gtfs.graph.builder.jgrapht.GtfsDirectedGraphBuilder;
import org.hisrc.gtfs.graph.model.TemporalStopNode;
import org.hisrc.gtfs.graph.model.TransitionEdge;
import org.hisrc.gtfs.serialization.onebusaway.GtfsReader;
import org.jgrapht.DirectedGraph;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.csv_entities.ZipFileCsvInputSource;
import org.onebusaway.gtfs.model.StopTime;

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

		final GtfsDirectedGraphBuilder graphBuilder = new GtfsDirectedGraphBuilder(
				2015, 07, 10);
		gtfsReader.setEntityStore(graphBuilder.getGtfsMutableDao());
		gtfsReader.run();

		final DirectedGraph<TemporalStopNode, TransitionEdge> graph = graphBuilder
				.build();

		Assert.assertFalse(graph.edgeSet().isEmpty());
	}

}
