package org.hisrc.distant.graph.model.edge.test;

import org.hisrc.distant.graph.model.edge.RideEdge;
import org.hisrc.distant.graph.model.edge.Transit;
import org.junit.Assert;
import org.junit.Test;

public class RideEdgeTest {

	@Test
	public void returnsCorrectTransits() {
		final RideEdge edge = new RideEdge();
		edge.addTransit(new Transit(100, 200));
		edge.addTransit(new Transit(200, 300));
		edge.addTransit(new Transit(90, 210));
		edge.addTransit(new Transit(110, 190));

		Assert.assertEquals(190, edge.after(0).getArrivalTime());
		Assert.assertEquals(190, edge.after(80).getArrivalTime());
		Assert.assertEquals(190, edge.after(90).getArrivalTime());
		Assert.assertEquals(190, edge.after(100).getArrivalTime());
		Assert.assertEquals(190, edge.after(110).getArrivalTime());
		Assert.assertEquals(300, edge.after(120).getArrivalTime());
		Assert.assertEquals(300, edge.after(190).getArrivalTime());
		Assert.assertEquals(300, edge.after(200).getArrivalTime());
		Assert.assertNull(edge.after(210));
	}

}
