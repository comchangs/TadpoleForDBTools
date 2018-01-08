/*******************************************************************************
 * Copyright (c) 2016 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.rdb.core.dialog.export.sqlresult.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine.EXPORT_METHOD;
import com.hangum.tadpole.commons.libs.core.message.CommonMessages;
import com.hangum.tadpole.engine.sql.util.SQLUtil;
import com.hangum.tadpole.rdb.core.Messages;
import com.hangum.tadpole.rdb.core.dialog.export.sqlresult.dao.ExportSqlDAO;

/**
 * sql result to text export
 * 
 * @author hangum
 *
 */
public class ExportSQLComposite extends AbstractExportComposite {
	private static final Logger logger = Logger.getLogger(ExportSQLComposite.class);
	
	private Button btnBatchInsert;
	private Button btnInsert;
	private Button btnUpdate;
	private Button[] btnWhereColumn;
	private Button btnMerge;
	private Composite compositeTargetTable;
	private Label lblExSchematable;
	private Composite compositeCommit;
	private Text textCommit;
	private Label label;
	private Group grpWhere;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ExportSQLComposite(Composite tabFolderObject, int style, String defaultTargetName, Map<Integer, String> mapColumnName) {
		super(tabFolderObject, style);

		CTabItem tbtmTable = new CTabItem((CTabFolder)tabFolderObject, SWT.NONE);
		tbtmTable.setText("SQL");
		tbtmTable.setData(EXPORT_METHOD.SQL);//$NON-NLS-1$

		Composite compositeText = new Composite(tabFolderObject, SWT.NONE);
		tbtmTable.setControl(compositeText);
		GridLayout gl_compositeTables = new GridLayout(2, false);
		compositeText.setLayout(gl_compositeTables);
		compositeText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label lblIncludeHead = new Label(compositeText, SWT.NONE);
		lblIncludeHead.setText(Messages.get().ExportSQLComposite_TargetTable);
		
		compositeTargetTable = new Composite(compositeText, SWT.NONE);
		compositeTargetTable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		compositeTargetTable.setLayout(new GridLayout(2, false));
		
		textTargetName = new Text(compositeTargetTable, SWT.BORDER);
		textTargetName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textTargetName.setText(defaultTargetName);
		
		lblExSchematable = new Label(compositeTargetTable, SWT.NONE);
		lblExSchematable.setText("ex) scheme.table ");
		
		Label lblCommit = new Label(compositeText, SWT.NONE);
		lblCommit.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCommit.setText(Messages.get().Commit);
		
		compositeCommit = new Composite(compositeText, SWT.NONE);
		compositeCommit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeCommit.setLayout(new GridLayout(2, false));
		
		textCommit = new Text(compositeCommit, SWT.BORDER | SWT.RIGHT);
		textCommit.setText("0");
		textCommit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label = new Label(compositeCommit, SWT.NONE);
		label.setText(Messages.get().ForEachMatter);
		
		Label lblSeparator = new Label(compositeText, SWT.NONE);
		lblSeparator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeparator.setText(Messages.get().DMLType);
		
		Composite compositeSeparator = new Composite(compositeText, SWT.NONE);
		compositeSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeSeparator.setLayout(new GridLayout(4, false));
		
		btnBatchInsert = new Button(compositeSeparator, SWT.RADIO);
		btnBatchInsert.setText("Batch Insert");
		
		btnInsert = new Button(compositeSeparator, SWT.RADIO);
		btnInsert.setText("INSERT");
		btnInsert.setSelection(true);
		
		btnUpdate = new Button(compositeSeparator, SWT.RADIO);
		btnUpdate.setText("Update");
		
		btnMerge = new Button(compositeSeparator, SWT.RADIO);
		btnMerge.setText("Merge");
		btnMerge.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				grpWhere.setText(Messages.get().ExportSQLComposite_MergeMatchColumn);
			}
		});
		
		Label lblEncoding = new Label(compositeText, SWT.NONE);
		lblEncoding.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEncoding.setText(Messages.get().encoding);
		
		comboEncoding = new Combo(compositeText, SWT.NONE);
		comboEncoding.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboEncoding.add("UTF-8");
		comboEncoding.add("euc-kr");
		comboEncoding.select(0);
		
		new Label(compositeText, SWT.NONE);
		
		grpWhere = new Group(compositeText, SWT.NONE);
		grpWhere.setLayout(new GridLayout(3, false));
		grpWhere.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpWhere.setText(Messages.get().SelectWhereColumn);
		
		int intWhereColumn = 0;
		for(int i=1; i<mapColumnName.size(); i++) {
			// tdb 내부적으로 사용하는 컬럼을 보이지 않도록 합니다.
			if(!SQLUtil.isTDBSpecialColumn(mapColumnName.get(i))) intWhereColumn++;
		}
		
		btnWhereColumn = new Button[intWhereColumn];
		int intWidgetCnt = 0;
		for(int i=0; i<mapColumnName.size(); i++) {
			// tdb 내부적으로 사용하는 컬럼을 보이지 않도록 합니다.
			if(!SQLUtil.isTDBSpecialColumn(mapColumnName.get(i))) {
				btnWhereColumn[intWidgetCnt] = new Button(grpWhere, SWT.CHECK);
				btnWhereColumn[intWidgetCnt].setText(mapColumnName.get(i));
				intWidgetCnt++;
			}
		}
		
	}

	@Override
	public ExportSqlDAO getLastData() {
		ExportSqlDAO dao = new ExportSqlDAO();
		
		dao.setComboEncoding(this.comboEncoding.getText());
		dao.setTargetName(this.textTargetName.getText());

		List<String> listWhereColumnName = new ArrayList<>();
		for (int i=0; i<btnWhereColumn.length; i++) {
			Button button = btnWhereColumn[i];
			if(button.getSelection()) listWhereColumnName.add(button.getText());
		}
		dao.setListWhere(listWhereColumnName);
		
		if (StringUtils.isEmpty(this.textCommit.getText())) {
			dao.setCommit(0);
		}else if (!StringUtils.isNumeric(this.textCommit.getText())){
			dao.setCommit(0);
		}else{
			dao.setCommit(Integer.valueOf(this.textCommit.getText().trim()));
		}
		
		if (this.btnBatchInsert.getSelection()){
			dao.setStatementType("batch");
		}else if (this.btnInsert.getSelection()){
			dao.setStatementType("insert");
		}else if (this.btnUpdate.getSelection()){
			dao.setStatementType("update");
		}else if (this.btnMerge.getSelection()){
			dao.setStatementType("merge");
		}
		
		return dao;
	}

	@Override
	public boolean isValidate() {
		if(super.isValidate()) {
			if (StringUtils.isEmpty(this.textTargetName.getText()) ){
				MessageDialog.openWarning(getShell(), CommonMessages.get().Warning, Messages.get().ExportSQLComposite_PleaseTargetInput);
				this.textTargetName.setFocus();
				return false;
			}
			
			List<String> listWhereColumnName = new ArrayList<>();
			for (int i=0; i<btnWhereColumn.length; i++) {
				Button button = btnWhereColumn[i];
				if(button.getSelection()) listWhereColumnName.add(button.getText());
			}

			// fix https://github.com/hangum/TadpoleForDBTools/issues/807
			if (this.btnUpdate.getSelection() && listWhereColumnName.size() <= 0){
				MessageDialog.openWarning(getShell(), CommonMessages.get().Warning, Messages.get().ExportSQLComposite_UpdateMsg);
				return false;
			}else if (this.btnMerge.getSelection() && listWhereColumnName.size() <= 0){
				MessageDialog.openWarning(getShell(), CommonMessages.get().Warning, Messages.get().ExportSQLComposite_PleaseMergeMath);
				return false;
			}
			
			if (!StringUtils.isNumeric(this.textCommit.getText())){
				MessageDialog.openWarning(getShell(), CommonMessages.get().Warning, Messages.get().ExportSQLComposite_PleaseCommitCount);
				textCommit.setText("0");
				this.textCommit.setFocus();
				return false;
			}
		}
		return true;
	}
	
}
