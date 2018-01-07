/*******************************************************************************
 * Copyright (c) 2018 TadpoleHub.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.sql.util.export;

import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine.EXPORT_METHOD;

/**
 * Export result dto
 * 
 * @author hangum
 *
 */
public class ExportResultDTO {
	/** start current time */
	long startCurrentTime = System.currentTimeMillis();
	
	/** end current time */
	long endCurrentTime = startCurrentTime; 
	
	/** 파일 익스포트 후에 파일결로 + 파일 이름 */
	String fileName = "";
	
	/** export row count */
	int rowCount = 0;
	
	/** export type */
	EXPORT_METHOD exportMethod = EXPORT_METHOD.TEXT;
	
	/** description */
	String description = "";
	
	/** exception */
	Exception exception;

	/**
	 * @return the startCurrentTime
	 */
	public long getStartCurrentTime() {
		return startCurrentTime;
	}

	/**
	 * @param startCurrentTime the startCurrentTime to set
	 */
	public void setStartCurrentTime(long startCurrentTime) {
		this.startCurrentTime = startCurrentTime;
	}

	/**
	 * @return the endCurrentTime
	 */
	public long getEndCurrentTime() {
		return endCurrentTime;
	}

	/**
	 * @param endCurrentTime the endCurrentTime to set
	 */
	public void setEndCurrentTime(long endCurrentTime) {
		this.endCurrentTime = endCurrentTime;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	/**
	 * @return the exportMethod
	 */
	public EXPORT_METHOD getExportMethod() {
		return exportMethod;
	}

	/**
	 * @param exportMethod the exportMethod to set
	 */
	public void setExportMethod(EXPORT_METHOD exportMethod) {
		this.exportMethod = exportMethod;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

}
