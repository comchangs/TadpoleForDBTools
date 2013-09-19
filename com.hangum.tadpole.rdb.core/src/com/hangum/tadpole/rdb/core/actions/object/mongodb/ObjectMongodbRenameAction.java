/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.rdb.core.actions.object.mongodb;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.hangum.tadpold.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.dao.mysql.TableDAO;
import com.hangum.tadpole.exception.dialog.ExceptionDetailsErrorDialog;
import com.hangum.tadpole.mongodb.core.query.MongoDBQuery;
import com.hangum.tadpole.rdb.core.Activator;
import com.hangum.tadpole.rdb.core.actions.object.AbstractObjectAction;

/**
 * Object Explorer에서 사용하는 Mongodb rename action
 * 
 * @author hangum
 *
 */
public class ObjectMongodbRenameAction extends AbstractObjectAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ObjectMongodbRenameAction.class);

	public final static String ID = "com.hangum.db.browser.rap.core.actions.object.mongo.rename";
	
	public ObjectMongodbRenameAction(IWorkbenchWindow window, PublicTadpoleDefine.DB_ACTION actionType, String title) {
		super(window, actionType);
		setId(ID + actionType.toString());
		setText(title);
	}

	@Override
	public void run() {
		if(null != this.sel) {
//			String originalName = this.sel.getFirstElement().toString();
			TableDAO table = (TableDAO) this.sel.getFirstElement();
			String originalName = table.getName();
			String newName = "";
			
			InputDialog inputDialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
						"Rename Collection", 
						"Enter new collection name", 
						originalName, 
						new LengthValidator(originalName)
					);
			if(inputDialog.open() == Window.OK) {				
				try {
					MongoDBQuery.renameCollection(userDB, originalName, inputDialog.getValue());
					
					// object explorer를 refresh 
					refreshTable();
				} catch (Exception e) {
					logger.error("mongodb rename", e);
					
					Status errStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e); //$NON-NLS-1$
					ExceptionDetailsErrorDialog.openError(null, "Error","Rename Collection", errStatus); //$NON-NLS-1$
				}
				
			}
		}		
	}
	
}
/**
 * rename validattor
 */
class LengthValidator implements IInputValidator {
	String oldName = "";
	
	public LengthValidator(String oldName) {
		this.oldName = oldName;
	}

	public String isValid(String newText) {
		if(oldName.equals(newText)) {
			return "It is the same the name of the previous.";
		}
	    int len = newText.length();
	
	    // Determine if input is too short or too long
	    if (len < 2) return "Too short";
	
	    return null;
	}
}
