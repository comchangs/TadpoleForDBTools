/*******************************************************************************
 * Copyright (c) 2018 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.engine.utils;

import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.commons.util.Utils;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;

/**
 * {@code RequestQuery} utils
 * 
 * @author hangum
 *
 */
public class RequestQueryUtil {

	/**
	 * simple request query
	 * 
	 * @param userDB
	 * @param strQuery
	 * @return
	 */
	public static RequestQuery simpleRequestQuery(UserDBDAO userDB, String strSQL) {
		return new RequestQuery(Utils.getUniqueID(), userDB, strSQL, PublicTadpoleDefine.OBJECT_TYPE.TABLES, 
				EditorDefine.QUERY_MODE.QUERY, EditorDefine.EXECUTE_TYPE.BLOCK, true);
	}

}
