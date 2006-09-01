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
package com.nxp.timedoctor.internal.ui;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TraceEditorInput implements IPathEditorInput {
	private String fileName;

	private String filePath;

	private Path   file;

	public TraceEditorInput(String name, String path) {
		this.file = new Path(path + File.separator + name);
		
		this.fileName = name;
		this.filePath = path;
	}

	public boolean exists() {
		return file.toFile().exists();
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}
	
	public String getName() {
		return fileName;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return filePath;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	/**
	 * Used for checking if an editor for this file is already open
	 */
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (!(obj instanceof TraceEditorInput)) {
			return false;
		}
		TraceEditorInput other = (TraceEditorInput) obj;
		return file.toString().equals(other.file.toString());
	}

	public IPath getPath() {
		return file;
	}
}
