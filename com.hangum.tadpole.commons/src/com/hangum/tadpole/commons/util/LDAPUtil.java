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
package com.hangum.tadpole.commons.util;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.hangum.tadpole.commons.Messages;
import com.hangum.tadpole.commons.exception.TadpoleAuthorityException;

/**
 * LDAP utils
 * 
 * @author hangum
 *
 */
public class LDAPUtil {
	private static final Logger logger = Logger.getLogger(LDAPUtil.class);
	
	public static LDAPUtil instance = null;
	private static String _ldapHost;		 // LDAP 호스트
	private static String _principal;	 // LDAP 쿼리
	
	private LDAPUtil() {}
	
	public static LDAPUtil getInstance() {
		if(instance == null) {
			Properties prop = LoadConfigFile.getConfigFile();
			
			_ldapHost	= StringUtils.trim(prop.getProperty("LDAP.HOST.URL"));
			_principal	= StringUtils.trim(prop.getProperty("LDAP.SEARCHBASE"));
			
			instance = new LDAPUtil();
		}
		
		return instance;
	}
	
	/**
	 * LDAP Login
	 * 
	 * @param strEmail
	 * @param strPass
	 */
	public static void ldapLogin(String strEmail, String strPass) throws TadpoleAuthorityException {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, _ldapHost);
		properties.put(Context.SECURITY_AUTHENTICATION, "simple");
		properties.put(Context.SECURITY_PRINCIPAL, String.format(_principal, strEmail));
		properties.put(Context.SECURITY_CREDENTIALS, strPass);
		
		DirContext con = null;
		try {
			con = new InitialDirContext(properties);
		} catch (Exception e) {
			logger.error("LDAP Login fail" + e.getMessage());
			throw new TadpoleAuthorityException(Messages.get().PleaseCheckIDpassword);
		} finally {
			 if(con != null) try { con.close(); } catch(Exception e) {}
		}
	}
}
