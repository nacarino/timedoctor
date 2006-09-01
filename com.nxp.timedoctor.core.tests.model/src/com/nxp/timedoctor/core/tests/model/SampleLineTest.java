package com.nxp.timedoctor.core.tests.model;

import junit.framework.*;

import com.nxp.timedoctor.core.model.*;
import com.nxp.timedoctor.core.model.lines.*;
/**
 * Tests the generic SampleLine functionality, using various subclasses.
 * SampleLine is abstract and can therefore not be instantiated directly.
 * 
 * @author ischimandle
 * 
 */
public class SampleLineTest extends TestCase {

	/**
	 * Model for use in creation of test lines.
	 */
	private TraceModel model = new TraceModel();

	/**
	 * CPU for use in creation of test lines.
	 */
	private SampleCPU cpu = new SampleCPU(model, 0, "testCPU", 1);

	/**
	 * Testing using a TaskSampleLine because SampleLine itself is abstract,
	 * cannot be directly instantiated. In cases in which functionality is
	 * overridden, will use another concrete subclass.
	 */
	public final void testSampleLine() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		assertEquals(cpu, line.getCPU());
	}

	/**
	 * Tests the static factory method createLine, verifying that the lines it
	 * creates are of the proper type and are properly constructed, and checks
	 * cases of invalid inputs.
	 * 
	 */
	public final void testCreateLine() {
		SampleLine line = SampleLine.createLine(cpu, SampleLine.LineType.AGENT,
				0, 0.0);
		System.out.println(line.getClass());
		assertTrue(line instanceof AgentSampleLine);
		assertEquals(SampleLine.LineType.AGENT, line.getType());
		assertEquals(true, line.isValid(0.0));

		line = SampleLine.createLine(cpu, SampleLine.LineType.TASK, 1, 2.0);
		assertTrue(line instanceof TaskSampleLine);

		line = SampleLine.createLine(cpu, SampleLine.LineType.PORT, 2, 2.4);
		assertNull(line);
	}

	/**
	 * Test the hashCode method, verifying that the hashcode value doesn't
	 * change over successive calls.
	 * 
	 */
	public final void testHashCode() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		int code = (cpu.getID() << 28) | line.getID();
		for (int i = 0; i < 10; i++) {
			assertEquals(code, line.hashCode());
		}
	}

	/**
	 * Tests the getting of a create time set at instantiation.
	 * 
	 */
	public final void testGetTimeCreate() {
		SampleLine line = SampleLine.createLine(cpu, SampleLine.LineType.AGENT,
				0, 0.0);
		assertEquals(true, line.isValid(0.0));
		line = SampleLine.createLine(cpu, SampleLine.LineType.TASK, 1, 3.0);
		assertEquals(false, line.isValid(0.0));
		assertEquals(true, line.isValid(3.0));
	}

	/**
	 * Tests the direct setting of create times.
	 * 
	 */
	public final void testSetTimeCreate() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		line.setTimeCreate(5.323);
		assertEquals(false, line.isValid(5.0));
		assertEquals(true, line.isValid(5.323));
	}

	/**
	 * Tests the setting of a delete time, including in the case where the
	 * delete time to be set is before the create time.
	 * 
	 */
	public final void testSetTimeDelete() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		line.setTimeCreate(2.2);
		try {
			line.setTimeDelete(4.5);
		} catch (CheckedIllegalArgumentException e) {
			fail(e.getMessage());
		}
		assertEquals(false, line.isValid(4.5));
		try {
			line.setTimeDelete(1.0);
			fail("should have thrown exception");
		} catch (CheckedIllegalArgumentException e) {
		}
	}

	public final void testIsValid() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		line.setTimeCreate(2.2);
		try {
			line.setTimeDelete(4.5);
		} catch (CheckedIllegalArgumentException e) {
			fail(e.getMessage());
		}
		assertTrue(line.isValid(2.2));
		assertTrue(line.isValid(3.0));
		assertTrue(line.isValid(4.5));
		assertFalse(line.isValid(0));
		assertFalse(line.isValid(10));
	}

	public final void testGetType() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		assertEquals(SampleLine.LineType.TASK, line.getType());
		line = new QueueSampleLine(cpu, 1);
		assertEquals(SampleLine.LineType.QUEUE, line.getType());
	}

	public final void testGetID() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		assertEquals(0, line.getID());
		line = new AgentSampleLine(cpu, 1);
		assertEquals(1, line.getID());
	}

	public final void testToString() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		line.setName("TestLine");
		assertEquals("TestLine", line.getName());
		line.setName(null);
		line.calculate(0);
		System.out.println(line.getName());
		assertEquals("Task 0x0", line.getName());
	}

	public final void testGetCPU() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		assertEquals(cpu, line.getCPU());
	}

	public final void testGetStartCount() {
		fail("Not yet implemented");
	}

	public final void testGetVisible() {
		fail("Not yet implemented");
	}

	public final void testSelfVisible() {
		fail("Not yet implemented");
	}

	public final void testSetVisible() {
		fail("Not yet implemented");
	}

	/**
	 * Tests that the section returned by the getSection() method is the one in
	 * the model associated with the proper type, implicitly testing that the
	 * constructor correctly handled the section-adding process.
	 */
	public final void testGetSection() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		Section section = model.getSections().getSection(SampleLine.LineType.TASK);
		assertEquals(section.getLine(cpu, 0, 0.0),line);				
	}

	/**
	 * Tests the setting of the index value, for use in sections. May eventually
	 * be unnecessary.
	 * 
	 */
	public final void testSetIndex() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		assertEquals(0, line.getID());
		SampleLine line2 = new TaskSampleLine(cpu, 8);
		assertEquals(1, line2.getID());				
	}

	/**
	 * Tests the method to retrieve samples by index value from the line.
	 * 
	 */
	public final void testGetSample() {
		SampleLine line = new TaskSampleLine(cpu, 0);
		line.setTimeCreate(0.0);
		line.addSample(Sample.SampleType.START, 3.4);
		Sample sample = line.getSample(0);
		assertEquals(3.4, sample.time);
		assertEquals(Sample.SampleType.START, sample.type);
		assertEquals(-1d, sample.val);
	}

	public void testSampleValToIndex() {
		fail("Not yet implemented");
	}

	/**
	 * Make sure the count increments as samples are added.
	 *
	 */
	public void testGetCount() {
		SampleLine line = new TaskSampleLine(cpu, 1);
		line.setTimeCreate(0);
		line.addSample(Sample.SampleType.START, 100000);
		assertEquals(1, line.getCount());
		line.addSample(Sample.SampleType.STOP, 200000);
		assertEquals(2, line.getCount());
	}

	public void testGetMaxValue() {
		QueueSampleLine line = new QueueSampleLine(cpu, 0);
		line.addSample(Sample.SampleType.START, 100000, 8);
		line.addSample(Sample.SampleType.STOP, 200000, 10);
		line.addSample(Sample.SampleType.START, 300000, 6);
		line.calculate(400000);
		assertEquals(8.0, line.getMaxSampleValue());
	}

	public void testGetMaxDuration() {
		fail("Not yet implemented");
	}

	public void testStartTime() {
		fail("Not yet implemented");
	}

	public void testEndTime() {
		fail("Not yet implemented");
	}

	public void testCompensateStartTime() {
		fail("Not yet implemented");
	}

	public void testAddSampleSampleTypeDouble() {
		fail("Not yet implemented");
	}

	public void testAddSampleSampleTypeDoubleDouble() {
		fail("Not yet implemented");
	}

	public void testAddDescrDescrTypeIntString() {
		fail("Not yet implemented");
	}

	public void testAddDescrDescrTypeIntDouble() {
		fail("Not yet implemented");
	}

	public void testAddDescrDescrTypeIntStringDouble() {
		fail("Not yet implemented");
	}

	public void testDescrString() {
		fail("Not yet implemented");
	}

	public void testBinarySearch() {
		fail("Not yet implemented");
	}

	public void testGetCounterDifference() {
		fail("Not yet implemented");
	}

	public void testHasSamples() {
		fail("Not yet implemented");
	}

	public void testInterruptTasks() {
		fail("Not yet implemented");
	}

	public void testCalculate() {
		fail("Not yet implemented");
	}

}
