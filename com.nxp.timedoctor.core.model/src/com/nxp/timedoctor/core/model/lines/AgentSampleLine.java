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
package com.nxp.timedoctor.core.model.lines;

import com.nxp.timedoctor.core.model.SampleCPU;
import com.nxp.timedoctor.core.model.SampleLine;
import com.nxp.timedoctor.core.model.Description.DescrType;
import com.nxp.timedoctor.core.model.Sample.SampleType;

/**
 * SampleLine type to represent agents. Implements the <code>calculate</code>
 * method and overrides the <code>addSamples</code> method from its
 * superclass.
 */
public class AgentSampleLine extends SampleLine {

    /**
     * Number of times the line has been suspended since the last start/resume.
     */
    private int suspendCount = 0;

    /**
     * Constructor takes a cpu and integer id and constructs and AgentSampleLine
     * using them. Sets its own type and section.
     * 
     * @param cpu
     *            the cpu associated with the line
     * @param id
     *            the integer id of the line
     */
    public AgentSampleLine(final SampleCPU cpu, final int id) {
        super(cpu, id);
        setType(LineType.AGENTS);
    }

    /**
     * Overrides abstract method <code>calculate</code> in superclass.
     * 
     * @param endTime
     *            the time to which to calculate samples
     */
    @Override
    public final void calculate(final double endTime) {
        int[] clr = new int[getCount() + 1];
        int[] st = new int[getCount() + 1];
        if (getName() == null) {
            setName(String.format("Agent 0x%x", getID()));
        }
        int n = 0;
        setMaxSampleDuration(0);
        for (int i = 0, ii = 0; i < getCount(); i++) {
            if (getSample(i).type == SampleType.START) {
                clr[n] = -1;
                for (; ii < getDescCount(); ii++) {
                    if (getDescription(ii).time >= getSample(i).time) {
                        break;
                    }
                }
                for (; ii < getDescCount(); ii++) {
                    if (getDescription(ii).time == getSample(i).time) {
                        if (getDescription(ii).type == DescrType.COLOR) {
                            clr[n] = (int) getDescription(ii).value;
                        }
                    } else {
                        break;
                    }
                }
                st[n++] = i;
            } else if (getSample(i).type == SampleType.SUSPEND) {
                if (n > 0) {
                    clr[n] = clr[n - 1];
                } else {
                    clr[n] = -1;
                }
                st[n++] = i;
            } else if (getSample(i).type == SampleType.STOP
                    || getSample(i).type == SampleType.RESUME) {
                if (n > 0) {
                    n--;
                    int j = st[n];
                    getSample(j).val = i;
                    getSample(i).val = (double) clr[n];
                    if (getSample(i).type == SampleType.STOP) {
                        setMaxSampleDuration(Math.max(getMaxSampleDuration(),
                                getSample(i).time - getSample(j).time));
                    }
                }
            }
        }
        /*
         * Add samples at end time to conclude still open task
         */
        for (n--; n >= 0; n--) {
            if (getSample(st[n]).type == SampleType.START) {
                addSample(SampleType.STOP, endTime);
            } else {
                while (suspendCount > 0) {
                    addSample(SampleType.RESUME, endTime);
                }
            }
            getSample(st[n]).val = getCount() - 1;
        }
        if (getCount() > 0) {
            addSample(SampleType.END, endTime, getSample(getCount() - 1).val);
        } else {
            addSample(SampleType.END, endTime);
        }
    }

    /**
     * Overrides superclass method for type-specific behavior.
     * 
     * @param from
     *            the start time for the search
     * @param to
     *            the end time for the search
     * @return boolean indicating whether samples are present in the specified
     *         window
     */
    @Override
    public final boolean hasSamples(final double from, final double to) {
        int i = binarySearch(from);
        int j = binarySearch(to);
        if (i != j) {
            return true;
        } else if (getCount() == 0) {
            return false;
        } else if (getSample(i).time > to || getSample(i).time < from) {
            return false;
        } else if (getSample(i).type == SampleType.STOP) {
            // what does Checkstyle mean here?
            return false;
        } else {
            return true;
        }
    }

}
