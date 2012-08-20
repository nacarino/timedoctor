/*******************************************************************************
 * Copyright (c) 2006-2012 TimeDoctor contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License version 1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Royal Philips Electronics NV. - initial API and implementation
 *******************************************************************************/
package net.timedoctor.core.model;

import java.util.Observable;

/**
 * Holds data on the current zoom and offset of trace lines in the view. Extends
 * the observable class in order to be able to automatically update all
 * interested observers when either field changes. Updated by listeners in the
 * view to reflect actions requested by the user.
 *
 * @see Observable
 */
// MR change name, should reflect that it holds more than just zoom data
public class ZoomModel extends Observable {

    /**
     * The initial size of the stacks for storing zoom data to allow for the
     * zoom back feature.
     */
    // MR improve name, relate to a stack of zoom items
    private static final int INIT_STACK_SIZE = 50;
    
    private static final int INITIAL_INTERVAL = 1000;
    
    private static final int DEFAULT_TICKS_PER_UNIT = 10;
    
    /**
     * The start time of the data currently displayed onscreen.
     */
    private double startTime;

    /**
     * The end time of the data curently displayed onscreen.
     */
    private double endTime;

    /**
     * Array of start times for zoom back feature.
     */
    // MR create a separate class (record) with zoom data (start and end)
    // MR improve name to reflect zooming
    private double[] startStack = new double[INIT_STACK_SIZE];

    /**
     * Array of end times for zoom back feature.
     */
    private double[] endStack = new double[INIT_STACK_SIZE];

    /**
     * Counter to track the height of the zoom stack.
     */
    private int stackCount = 0;

    /**
     * Time set by the baseline cursor
     */
    private double selectTime = -1;

    /**
	 * Accuracy of the time to be displayed.
	 */
	private double timeDisplayAccuracy = 0.0;
	
	private int intervalCount = -1;

    private SampleLine selectedLine =  null;

	private int pixeslsWidth = -1;
    
    /**
     * Sets the zoom and updates all observers with the new value.
     *
     * @param time
     *            the new start time
     */
    protected final void setStartTime(final double time) {
    	if (time != startTime) {
    		this.startTime = time;
    		updateTimeDisplayAccuracy();
    		setChanged();
    		notifyObservers();
    	}
    }

    /**
     * Sets the end time and updates all observers.
     *
     * @param time
     *            the new offset value
     */
    protected final void setEndTime(final double time) {
        if (time != endTime) {
        	this.endTime = time;
        	updateTimeDisplayAccuracy();
        	setChanged();
        	notifyObservers();
        }
    }

    public final void setSelectTime(final double time) {
    	if (time != selectTime) {
    		this.selectTime = time;
    		setChanged();
    		notifyObservers();
    	}
    }

    public final double getSelectTime() {
    	return selectTime;
    }
    
    /**
     * Sets the start and end times of the visible portion of the trace, and
     * updates all observers.
     * 
     * @param start
     *            the start time of the visible portion
     * @param end
     *            the end time of the visible portion
     */
    // MR improve name (what times?)
    public final void setTimes(final double start, final double end) {
    	if ((start != end) && ((start != startTime) || (end != endTime))) {
			this.startTime = start;
			this.endTime = end;
			updateTimeDisplayAccuracy();
			setChanged();
			notifyObservers();
		}
    }

    /**
	 * Returns the current zoom factor.
	 * 
	 * @return the current zoom factor
	 */
    public final double getStartTime() {
        return startTime;
    }

    /**
     * Returns the current x-offset.
     * 
     * @return current x-offset
     */
    public final double getEndTime() {
        return endTime;
    }

    /**
     * Pushes a zoom onto the top of the stack, defined by start and end times.
     * 
     * @param start
     *            the start time of this zoom
     * @param end
     *            the end time of this zoom
     */
    public final void pushZoom(final double start, final double end) {
        if (stackCount == INIT_STACK_SIZE) {
            for (int i = 0; i < INIT_STACK_SIZE - 1; i++) {
                startStack[i] = startStack[i + 1];
                endStack[i] = endStack[i + 1];
            }
            stackCount--;
        }

        startStack[stackCount] = startTime;
        endStack[stackCount] = endTime;
        stackCount++;
    }

    /**
     * Pops the top zoom off the stack and returns the start and end times in
     * the 0 and 1 positions of an array.
     * 
     * @return an array representing the top zoom
     */
    public final double[] popZoom() {
    	if ( stackCount == 0 ) {
    		return null;
    	}
        
        stackCount--;

        double[] zoom = new double[2];
        zoom[0] = startStack[stackCount];
        zoom[1] = endStack[stackCount];
        return zoom;
    }
    
    /**
     * Returns whether the zoom stack is empty
     * 
     * @return
     * 		true, if empty; false otherwise
     */
    public final boolean isZoomStackEmpty() {
    	return (stackCount == 0);
    }
	
	/**
	 * Returns the current accuracy value
	 * 
	 * @return the accuracy value
	 */
	public final double getTimeDisplayAccuracy() {
		return timeDisplayAccuracy;
	}
	
	/**
	 * The number of intervals, best suited for the current time display accuracy
	 * as returned by {@link #getTimeDisplayAccuracy()}
	 * 
	 * @return
	 * 		The number of intervals
	 */
	public final int getIntevalCount() {
		return intervalCount;
	}
	
	/**
	 * Returns the TimeDisplay interval
	 * 
	 * @return
	 * 		The time display interval
	 */
	public final double getTimeDisplayInterval() {
		return timeDisplayAccuracy / intervalCount;
	}

	/**
	 * Returns the factor of pixels per time unit
	 * 
	 * @param width
	 *            The width of the widget
	 * @return The pixels per time unit factor
	 */
	public double getPixelsPerTime(final int width) {
		double timeRange = endTime - startTime;
		return width / timeRange;
	}

	/**
	 * Returns the time for the given width and position x
	 * 
	 * @param x
	 *            The x-position of the cursor
	 * @param width
	 *            The width of the widget
	 * @return The time at position x
	 */
	public double getTimeAtPosition(final int x, final int width) {
		return startTime + (x / getPixelsPerTime(width));
	}
	
	public void setSelectedLine(SampleLine line) {
		this.selectedLine = line;
		setChanged();
		notifyObservers();
	}
	
	public SampleLine getSelectedLine() {
		return selectedLine;
	}
	
	/**
	 * The width of the display
	 * 
	 * @param width
	 * 			The width in integer
	 */
	public void setWidth(final int width) {
		this.pixeslsWidth = width;
		updateTimeDisplayAccuracy();
	}

	private void updateTimeDisplayAccuracy() {
		if (pixeslsWidth != -1) {
			int count = DEFAULT_TICKS_PER_UNIT;
			
			double pixelsPerTime = this.pixeslsWidth / (endTime - startTime);
			double interval = INITIAL_INTERVAL;
			double width = INITIAL_INTERVAL * pixelsPerTime;
			
			// Find 10-power that ensures 75 pixel spacing (max number is xxxx.xxus)
			int cursorWidth = 75;
			while (width > 10 * cursorWidth) {
				interval /= 10;
				width /= 10;
			}
			if (width > 5 * cursorWidth) {
				interval /= 5;
				width /= 5;
				count = 2;
			} else if (width > 2 * cursorWidth) {
				interval /= 2;
				width /= 2;
				count = 5;
			}
			
			this.timeDisplayAccuracy = interval;
			this.intervalCount = count;
		}
	}
}
