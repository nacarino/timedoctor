package net.timedoctor.core.tests.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.timedoctor.core.model.Sample;
import net.timedoctor.core.model.SampleCPU;
import net.timedoctor.core.model.SampleLine;
import net.timedoctor.core.model.TraceModel;
import net.timedoctor.core.model.lines.AgentSampleLine;

public class AgentSampleLineTest extends TestCase {
	
	private TraceModel model = new TraceModel();
	private SampleCPU cpu = new SampleCPU(model, 0, "testCPU", 1.0);

	public void testAgentSampleLine() {
		AgentSampleLine line = new AgentSampleLine(cpu, 0);
		assertEquals(SampleLine.LineType.AGENTS, line.getType());
	}
	
	public void testHasSamples() {
		AgentSampleLine line = new AgentSampleLine(cpu, 0);
		assertFalse(line.hasSamples(0, 100));
		line.addSample(Sample.SampleType.START, 2);
		assertTrue(line.getCount() == 1);
		assertTrue(line.hasSamples(0, 4));
		line.addSample(Sample.SampleType.SUSPEND, 3);
		assertTrue(line.hasSamples(0, 4));
		assertTrue(line.hasSamples(0, 2));
		assertFalse(line.hasSamples(0, 1));
	}

	public void testCalculate() {
		fail("Not yet implemented");
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(AgentSampleLineTest.class);
		return suite;
	}

}
