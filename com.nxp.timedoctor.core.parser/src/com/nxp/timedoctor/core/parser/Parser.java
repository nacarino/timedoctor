/*******************************************************************************
 * Copyright (c) 2006 Royal Philips Electronics NV.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package com.nxp.timedoctor.core.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.nxp.timedoctor.core.model.CheckedIllegalArgumentException;
import com.nxp.timedoctor.core.model.SampleCPU;
import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.Section;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.Description.DescrType;
import com.nxp.timedoctor.core.model.Sample.SampleType;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.core.model.lines.PortSampleLine;

/**
 * TimeDoctor parser. Designed to run in its own thread, and show a progress
 * monitor in the eclipse window. Parses the file and populates the model with
 * the data. 
 */
public class Parser extends Job {

	/**
	 * Max number of arguments (including the command itself)
	 * in a tag in the trace file.
	 */
	private static final int MAX_TAG_ARGS = 10;
	
	/**
	 * Constant for the command position in the tokens array.
	 */
	private static final int TAG_CMD_INDEX = 0;

	/**
	 * Constants for the indices of the arguments of the TimeDoctor commands
	 * in the trace file.
	 */
	private static final int TAG_ARG1_INDEX = 1;
	private static final int TAG_ARG2_INDEX = 2;
	private static final int TAG_ARG3_INDEX = 3;
	private static final int TAG_ARG4_INDEX = 4;
	private static final int TAG_ARG5_INDEX = 5;
	private static final int TAG_ARG6_INDEX = 6;
	private static final int TAG_ARG7_INDEX = 7;

	private TraceModel model;
	private File       ioFile;

	/**
	 * The sample CPU currently active. Null until run is called and parsing
	 * starts.
	 */
	private SampleCPU currentCPU = null;

	/**
	 * the last time at which something occurred. Equals
	 * <code>Double.MIN_VALUE</code> until run is called and parsing begins.
	 */
	private double lastTime = Double.MIN_VALUE;

	/**
	 * Last SampleLine modified. Used for adding descriptions, etc.
	 */
	private SampleLine lastLine = null;
	
	// MR move to argument of methods
	private int tokenLength;
	
	/**
	 * Initializes the model and input variables, and passes the name to the
	 * <code>Job</code> constructor.
	 * 
	 * @param name
	 *            the name of the job
	 * @param model
	 *            the model to populate with data
	 * @param ioFile
	 *            the input source for tokens to parse
	 */
	public Parser(final String name, final TraceModel model,
			final File ioFile) {
		super(name);

		this.model = model;
		this.ioFile = ioFile;
	}

	/**
	 * Run method for multithreading. Contains all parser functionality.
	 * 
	 * @param monitor
	 *            the progress monitor to update while running
	 * @return its status
	 */
	public final IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Parsing trace...", IProgressMonitor.UNKNOWN);

		parseFile();

		compensateStartTime(calculateStartTime());
		model.setEndTime();
		closeLines();
		model.computeMaxValues();

		monitor.done();
		return Status.OK_STATUS;
	}

	/**
	 * Parse the trace file and fill the model.
	 */
	private void parseFile() {
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(ioFile));

			String parseLine = file.readLine();
			String[] tokens = new String[MAX_TAG_ARGS];
			while (parseLine != null) {
				// Although it is deprecated, use a StringTokenizer
				// instead of String.split(), for increased performance
				// in simple splitting of strings separated by whitespaces
				tokenLength = 0;
				StringTokenizer tokenizer = new StringTokenizer(parseLine);
		        for (tokenLength = 0; tokenizer.hasMoreTokens(); tokenLength++) {
		            tokens[tokenLength] = tokenizer.nextToken();
		        }
				
				parseLine(tokens);
				parseLine = file.readLine();
			}
		} catch (FileNotFoundException e) {
			// how to pop up an error? no shell accessible, can't throw the
			// exception.
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseLine(String[] tokens) {
		String command = tokens[TAG_CMD_INDEX];

		if (command.compareTo("CPU") == 0) { // CPU <id> <name>
			parseCpuCommand(tokens);
		} else if (currentCPU == null && command.compareTo("TIME") != 0) {
			// Single-cpu file with no cpu tag
			currentCPU = new SampleCPU(model, 0, null, 1);
			model.addCPU(currentCPU);
		}
		
		// The following statements are ordered to the average occurence in
		// a trace file to optimize parsing performance.
		if (command.compareTo("STA") == 0) {
			parseStaCommand(tokens);
		} else if (command.compareTo("STO") == 0) {
			parseStoCommand(tokens);
		} else if (command.compareTo("OCC") == 0) {
			parseOccCommand(tokens);
		} else if (command.compareTo("TIM") == 0) {
			parseTimCommand(tokens);
		} else if (command.compareTo("VAL") == 0) {
			parseValCommand(tokens);
		} else if (command.compareTo("DSC") == 0) {
			parseDscCommand(tokens);
		} else if (command.compareTo("NAM") == 0) {
			parseNamCommand(tokens);
		} else if (command.compareTo("DNM") == 0) {
			parseDnmCommand(tokens);
		} else if (command.compareTo("CRE") == 0) {
			parseCreCommand(tokens);
		} else if (command.compareTo("DEL") == 0) {
			parseDelCommand(tokens);
		} else if (command.compareTo("TIME") == 0) {
			parseTimeCommand(tokens);
		} else if (command.compareTo("SPEED") == 0) {
			parseSpeedCommand(tokens);
		} else if (command.compareTo("MEMSPEED") == 0) {
			parseMemspeedCommand(tokens);
		} else if (command.compareTo("END") == 0) { // breaks the
			// read-eval loop if
			// the end-of-file
			// command is found
			return;
		}
	}

	/**
	 * Calculates the start time of the whole trace.
	 * 
	 * @return the time of the first sample in the trace
	 */
	private double calculateStartTime() {
		double startTime = Double.MAX_VALUE;
		for (Section section : model.getSections().values()) {
			if (section != null) {
				for (SampleLine line : section.getLines()) {
					startTime = Math.min(line.getStartTime(), startTime);
				}
			}
		}
		if (startTime == Double.MAX_VALUE) {
			startTime = 0.0;
		}
		return startTime;
	}

	// MR explain what "closing" means
	/**
	 * Consolidates and closes all sample lines.
	 * 
	 */
	private void closeLines() {
		for (Section section : model.getSections().values()) {
			for (SampleLine line : section.getLines()) {
				line.calculate(model.getEndTime());
			}
		}
	}

	/**
	 * Compensates for dead time at the beginning of the trace.
	 * 
	 * @param startTime
	 *            the time of the first sample
	 */
	private void compensateStartTime(final double startTime) {
		for (Section section : model.getSections().values()) {
			for (SampleLine line : section.getLines()) {
				line.compensateStartTime(startTime);
			}
		}
	}

	/**
	 * Parses a CPU command from the supplied token array and instantiates the
	 * CPU. TDIII commands are in the form
	 * <code>CPU &lt;id&gt; &lt;name&gt;</code>. TDII commands in the form
	 * <code>CPU &lt;name&gt;</code> are also supported.
	 * 
	 * @param tokens
	 *            the array of tokens containing the command
	 */
	private void parseCpuCommand(final String[] tokens) {
		int id;
		String name = null;
		if (tokenLength > 2) {
			id = Integer.parseInt(tokens[1]);
			name = tokens[2];
			SampleCPU cpu = model.getCPU(id);
			if (cpu != null) {
				currentCPU = cpu;
			} else {
				double mcps;
				if (currentCPU == null) {
					mcps = 1;
				} else {
					mcps = currentCPU.getMemClocksPerSec();
				}
				currentCPU = new SampleCPU(model, id, name, mcps);
				model.addCPU(currentCPU);
			}
		} else {
			// backwards compatibility
			name = tokens[1];
			double mcps;
			if (currentCPU == null) {
				mcps = 1;
			} else {
				mcps = currentCPU.getMemClocksPerSec();
			}
			currentCPU = new SampleCPU(model, 0, name, mcps);
			model.addCPU(currentCPU);
		}
	}

	/**
	 * Parses a create command from the tokens array, creating the trace line
	 * and setting the creation time. Commands are in the form
	 * <code>CRE &lt;type&gt; &lt;id&gt; &lt;time&gt; [&lt;prod_id&gt;
	 * [&lt;prod_cpu_id&gt;] &lt;cons_id&gt; [&lt;cons_cpu_id&gt;]]</code>.
	 * 
	 * @param tokens
	 *            the token array to parse from
	 */
	private void parseCreCommand(final String[] tokens) {
		// CRE <type> <id> <time> [<prod_id> [<prod_cpu_id>] <cons_id>
		// [<cons_cpu_id>]]
		double time = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
		lastTime = time / model.getTicksPerSec();
		int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
		/*
		 * Needs to deal with ports manually
		 */
		// MR extract method to handle ports specifically
		if (tokenLength > TAG_ARG4_INDEX) {
			int prodID = Integer.MIN_VALUE;
			int consID = Integer.MIN_VALUE;
			SampleCPU prodCpu = null;
			SampleCPU consCPU = null;
			prodID = Integer.parseInt(tokens[TAG_ARG4_INDEX]);
			consID = Integer.parseInt(tokens[TAG_ARG5_INDEX]);
			if (tokenLength == TAG_ARG6_INDEX) {
				prodCpu = currentCPU;
				consCPU = currentCPU;
			} else if (tokenLength > TAG_ARG6_INDEX) {
				int prodCpuId = Integer.parseInt(tokens[TAG_ARG6_INDEX]);
				int consCpuId = Integer.parseInt(tokens[TAG_ARG7_INDEX]);
				prodCpu = model.getCPU(prodCpuId);
				consCPU = model.getCPU(consCpuId);
			}
			SampleLine prod = null;
			SampleLine cons = null;
			Section tasks = model.getSections().getSection(LineType.TASK);
			Section queues = model.getSections().getSection(LineType.QUEUE);
			Section isrs = model.getSections().getSection(LineType.ISR);
			if (tasks != null) {
				prod = tasks.getLine(prodCpu, prodID, lastTime);
			}
			if (prod == null && queues != null) {
				prod = queues.getLine(prodCpu, prodID, lastTime);
			}
			if (prod == null && isrs != null) {
				prod = isrs.getLine(prodCpu, prodID, lastTime);
			}
			if (tasks != null) {
				cons = tasks.getLine(consCPU, consID, lastTime);
			}
			if (cons == null && queues != null) {
				cons = queues.getLine(consCPU, consID, lastTime);
			}
			if (cons == null && isrs != null) {
				cons = isrs.getLine(consCPU, consID, lastTime);
			}
			lastLine = new PortSampleLine(currentCPU, id, prod, cons);
			lastLine.addToSection(LineType.PORT);
			lastLine.setTimeCreate(lastTime);
		} else {
			LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
			lastLine = SampleLine.createLine(currentCPU, type, id, lastTime);
		}
	}

	/**
	 * Parses a delete command, finds the associated line, and sets that line's
	 * delete time. Command is in the form
	 * <code>DEL &lt;type&gt; &lt;id&gt; &lt;time&gt;</code>.
	 * 
	 * @param tokens
	 *            the tokens array to parse from
	 */
	private void parseDelCommand(final String[] tokens) {
		LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
		Section section = model.getSections().getSection(type);
		if (section != null) {
			int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
			double time = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
			lastTime = time / model.getTicksPerSec();
			lastLine = section.getLine(currentCPU, id, lastTime);
			try {
				lastLine.setTimeDelete(lastTime);
			} catch (CheckedIllegalArgumentException e) {
				return;
				// TODO how to properly handle this exception??
			}
		}
	}

	/**
	 * Parses a start command and handles adding the appropriate samples to the
	 * line, performing line interrupts if necessary, etc. Command is in the
	 * form <code>STA &lt;type&gt; &lt;id&gt; &lt;time&gt;
	 * [&lt;size&gt;]</code>.
	 * 
	 * @param tokens
	 *            the array of tokens to parse from
	 */
	private void parseStaCommand(final String[] tokens) {
		double time = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
		LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
		int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
		lastTime = time / model.getTicksPerSec();
		double val = 1;
		if (tokenLength > TAG_ARG4_INDEX) {
			val = Double.parseDouble(tokens[TAG_ARG4_INDEX]);
		}
		// MR extract method for getting/creating the line and section
		Section section = model.getSections().getSection(type);
		if (section == null) {
			lastLine = SampleLine.createLine(currentCPU, type, id, lastTime);
		} else {
			lastLine = section.getLine(currentCPU, id, lastTime);
		}

		if (lastLine == null) {
			lastLine = SampleLine.createLine(currentCPU, type, id, lastTime);
		}
		
		lastLine.addSample(SampleType.START, lastTime, val);
		if (type == LineType.TASK || type == LineType.ISR) {
			handlePreemption(SampleType.SUSPEND, lastTime, lastLine);
		}
	}

	/**
	 * Parses and handles a stop command from the tokens array. Command is in
	 * the form
	 * <code>STO &lt;type&gt; &lt;id&gt; &lt;time&gt; [&lt;size&gt;]</code>.
	 * 
	 * @param tokens
	 *            the tokens array to br parsed from
	 */
	private void parseStoCommand(final String[] tokens) {
		LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
		int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
		double size = 1;
		double time = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
		Section section = model.getSections().getSection(type);
		if (tokenLength > TAG_ARG4_INDEX) {
			size = Integer.parseInt(tokens[TAG_ARG4_INDEX]);
		}
		lastTime = time / model.getTicksPerSec();
		if (section != null) {
			lastLine = section.getLine(currentCPU, id, lastTime);
		}
		if (lastLine != null) {
			lastLine.addSample(SampleType.STOP, lastTime, size);
			if (type == LineType.TASK || type == LineType.ISR) {
				handlePreemption(SampleType.RESUME, lastTime, lastLine);
			}
		}
	}

	/**
	 * Parses and handles an OCC command.
	 * 
	 * @param tokens
	 *            the array of tokens to parse from
	 */
	private void parseOccCommand(final String[] tokens) {
		double time = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
		LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
		int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
		lastTime = time / model.getTicksPerSec();
		Section section = model.getSections().getSection(type);
		if (section == null) {
			lastLine = SampleLine.createLine(currentCPU, type, id, lastTime);
		} else {
			lastLine = section.getLine(currentCPU, id, lastTime);
			if (lastLine == null) {
				lastLine = SampleLine
						.createLine(currentCPU, type, id, lastTime);
			}
		}
		lastLine.addSample(SampleType.EVENT, lastTime);
	}

	/**
	 * Parses and handles a TIM command.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseTimCommand(final String[] tokens) {
		double time = Double.parseDouble(tokens[TAG_ARG1_INDEX]);
		lastTime = time / model.getTicksPerSec();
	}

	/**
	 * Parses and handles a VAL command.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseValCommand(final String[] tokens) {
		if (lastTime != Double.MIN_VALUE) {
			LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
			int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
			double val = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
			Section section = model.getSections().getSection(type);
			SampleLine sl = null;
			if (section == null) {
				sl = SampleLine.createLine(currentCPU, type, id, lastTime);
			} else {
				sl = section.getLine(currentCPU, id, lastTime);
				if (sl == null) {
					sl = SampleLine.createLine(currentCPU, type, id, lastTime);
				}
			}
			sl.addSample(SampleType.EVENT, lastTime, val);
		}
	}

	/**
	 * Parses and handles a DSC command, adding the description to the last line
	 * modified.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseDscCommand(final String[] tokens) {
		// DSC <type> <id> <name>
		if (lastLine != null) {
			DescrType type = DescrType.parseString(tokens[TAG_ARG1_INDEX]);
			int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
			if (type == DescrType.STRING) {
				String string = tokens[TAG_ARG3_INDEX];
				lastLine.addDescription(type, id, string);
			} else if (type == DescrType.CYCLES) {
				double cycles = Double.parseDouble(tokens[TAG_ARG3_INDEX])
						/ model.getTicksPerSec();
				lastLine.addDescription(type, id, cycles);
			} else {
				double val = Double.parseDouble(tokens[TAG_ARG3_INDEX]);
				lastLine.addDescription(type, id, val);
			}
		}
	}

	/**
	 * Parses and handles a name command, setting the name of the last line
	 * modified.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseNamCommand(final String[] tokens) {
		// NAM <type> <id> <name>
		LineType type = LineType.parseType(Integer.parseInt(tokens[TAG_ARG1_INDEX]));
		int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
		String name = tokens[TAG_ARG3_INDEX];
		Section section = model.getSections().getSection(type);
		if (section == null) {
			lastLine = SampleLine.createLine(currentCPU, type, id, 0);
		} else {
			lastLine = model.getSections().getSection(type).getLine(currentCPU,
					id, lastTime);
		}
		if (lastLine == null) {
			lastLine = SampleLine.createLine(currentCPU, type, id, 0);
		}
		lastLine.setName(name);
	}

	/**
	 * Parses and handles a description name command, registering the name and
	 * id in the model's description name hash table.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseDnmCommand(final String[] tokens) {
		// DNM <type> <id> <name>
		int id = Integer.parseInt(tokens[TAG_ARG2_INDEX]);
		String name = tokens[TAG_ARG3_INDEX];
		model.addDescrName(id, name);
	}

	/**
	 * Parses and handles a time command, setting the model's ticks per second
	 * field. If the new value is zero, restores to the default of 1.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseTimeCommand(final String[] tokens) {
		// TIME <ticks_per_sec>
		double ticksPerSec = Double.parseDouble(tokens[1]);
		model.setTicksPerSec(ticksPerSec);
		if (model.getTicksPerSec() == 0) {
			model.setTicksPerSec(1);
		}
	}

	/**
	 * Parses and handles a speed command, setting the speed of the current CPU
	 * in clocks per second. If the new speed is less than 1, restores to the
	 * default value of 1.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseSpeedCommand(final String[] tokens) {
		// SPEED clocks_per_second>
		double cps = Double.parseDouble(tokens[TAG_ARG1_INDEX]);
		currentCPU.setClocksPerSec(Math.max(1, cps));
	}

	/**
	 * Parses and handles a memspeed command, setting the memory speed of the
	 * current cpu in clocks per second. If the new speed is less than one,
	 * restores to the default value of 1.
	 * 
	 * @param tokens
	 *            the array of tokens to be parsed from
	 */
	private void parseMemspeedCommand(final String[] tokens) {
		// MEMSPEED <memclocks_per_sec>
		double mcps = Double.parseDouble(tokens[TAG_ARG1_INDEX]);
		currentCPU.setMemClocksPerSec(Math.max(1, mcps));
	}

	/**
	 * Add preemption (suspend and resume) samples to all other tasks
	 * and/or ISRs. Used by the parser in adding samples.
	 * 
	 * @param time
	 *            the time at which to interrupt tasks
	 * @param type
	 *            the type of task that triggered the interrupt
	 * @param line
	 *            the line calling the interrupt, which will not be interrupted
	 */
	private void handlePreemption(final SampleType type, final double time,
			final SampleLine line) {
		// Assert(lineType == LineType.TASK || lineType == LineType.ISR)
		addPreemptionSamples(line, type, time, LineType.TASK);

		// Tasks cannot interrupt ISRs
		if (line.getType() == LineType.ISR) {
			addPreemptionSamples(line, type, time, LineType.ISR);
		}
	}

	private void addPreemptionSamples(final SampleLine line, final SampleType type,
			final double time, final LineType lineType) {
		Section section = model.getSections().getSection(lineType);
		if (section != null) {
			for (SampleLine l : section.getLines()) {
				if (line != line) {
					l.addSample(type, time);
				}
			}
		}
	}
}