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
package com.nxp.timedoctor.ui.trace.canvases;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.TraceModel;
import com.nxp.timedoctor.core.model.ZoomModel;
import com.nxp.timedoctor.core.model.SampleLine.LineType;
import com.nxp.timedoctor.ui.trace.descriptions.SampleInfo;

/**
 * Abstract parent class for trace canvases. Contains a factory method to return
 * the appropriate subclass, and handles calling redraws when the
 * <code>ZoomModel</code> observable changes. Trace canvases automatically use
 * style <code>SWT.DOUBLE_BUFFERED</code>
 */
public abstract class TraceCanvas extends Canvas implements Observer {
	
	private SampleInfo sampleInfo;
	
	/**
	 * Constructs a new double-buffered canvas, and adds this
	 * <code>TraceCanvas</code> to <code>data</code>.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param data
	 *            <code>Observable</code> containing all zoom and scroll data
	 */
	protected TraceCanvas(final Composite parent, 
			final ZoomModel zoom,
			final SampleInfo sampleInfo) {
		super(parent, SWT.DOUBLE_BUFFERED);
		
		this.sampleInfo = sampleInfo;
		zoom.addObserver(this);		
	}

	/**
	 * Factory method to return TraceCanvases of the appropriate subtype to draw
	 * the line.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param line
	 *            the line to be displayed
	 * @param data
	 *            contains all zoom and scroll data
	 * @param model
	 *            contains all trace data
	 * @return an instance of the subclass of TraceCanvas corresponding to the
	 *         line type to be drawn
	 */
	public static TraceCanvas createCanvas(final Composite parent,
			final SampleLine line, final ZoomModel data, final TraceModel model) {
		LineType type = line.getType();
		switch (type) {
		case TASKS:
			return new TaskCanvas(parent, line, data, model);
		case ISRS:
			return new IsrCanvas(parent, line, data, model);
		case SEMAPHORES:
			return new SemaphoreCanvas(parent, line, data);
		case QUEUES:
			return new QueueCanvas(parent, line, data, model);
		case EVENTS:
			return new EventCanvas(parent, line, data);
		case VALUES:
			return new ValueCanvas(parent, line, data);
		case CYCLES:
			return new CyclesCanvas(parent, line, data);
		case NOTES:
			return new NoteCanvas(parent, line, data);
		case AGENTS:
			return new AgentCanvas(parent, line, data, model);
		case MEM_CYCLES:
			return new MemCyclesCanvas(parent, line, data);
		case CHANNEL:
			// Should never be called--channels are deprecated. Use QUEUE
			// instead.
			return new QueueCanvas(parent, line, data, model);
		default:
			return null;
		}
	}

	/**
	 * Redraws the line when scroll or zoom state changes.
	 * 
	 * @param o
	 *            the <code>Observable</code> triggering the update
	 * @param data
	 *            has no effect on this update
	 */
	public final void update(final Observable o, final Object data) {
		redraw();
		update();
	}

	public void showSampleInfo(int sampleIndex) {
		if (sampleIndex >= 0) {
			setToolTipText(sampleInfo.getInfoStr(sampleIndex));
		} else {
			setToolTipText(null);
		}
	}
}
