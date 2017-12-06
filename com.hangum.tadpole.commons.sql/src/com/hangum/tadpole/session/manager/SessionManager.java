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
package com.hangum.tadpole.session.manager;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.engine.TadpoleEngineActivator;
import com.hangum.tadpole.engine.manager.TadpoleSQLManager;
import com.hangum.tadpole.engine.manager.TadpoleSQLTransactionManager;
import com.hangum.tadpole.engine.query.dao.ManagerListDTO;
import com.hangum.tadpole.engine.query.dao.system.UserDAO;
import com.hangum.tadpole.engine.query.dao.system.UserDBDAO;
import com.hangum.tadpole.engine.query.dao.system.UserInfoDataDAO;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserInfoData;
import com.hangum.tadpole.engine.utils.HttpSessionCollectorUtil;

/**
 * tadpole의 session manager입니다
 * 
 * @author hangum
 *
 */
public class SessionManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SessionManager.class);
	
	/** login ip를 가져온 경로를 지정한다 */
	public static enum LOGIN_IP_TYPE {BROWSER_IP, SERVLET_REQUEST};

	/**
	 * <pre>
	 * 		MANAGER_SEQ는 그룹의 manager 권한 사용자의 seq 입니다.  seq로  그룹의 db list를 얻기위해 미리 가져옵니다.
	 * </pre>
	 * 
	 * @author hangum
	 */
	public static enum NAME {
														/** webrtc, request.getRemoteAddrress */
														LOGIN_IP_TYPE,
														LOGIN_IP,
														
								/* 자신의 유저 seq */		USER_SEQ, 
														LOGIN_EMAIL, 
//														LOGIN_PASSWORD, 
														LOGIN_NAME,
														IS_REGIST_DB,
														
														IS_SHARED_DB,
														LIMIT_ADD_DB_CNT,
														IS_MODIFY_PERFERENCE,
														SERVICE_END,
														LANGUAGE,
														TIMEZONE,
														
								/* 대표적인 권한 타입 */		REPRESENT_ROLE_TYPE, 
														USER_INFO_DATA,
														
														USE_OTP, OTP_SECRET_KEY,
														
														UNLOCK_DB_LIST,
														
														PERSPECTIVE,
														
														ALL_MANAGER_DB_LIST
														}
	/**
	 * UserManager Object list를 설정한다.
	 * 
	 * @param managerDTO
	 */
	public static void initManagerDBList() {
		HttpSession sStore = RWT.getRequest().getSession();
		sStore.setAttribute(NAME.ALL_MANAGER_DB_LIST.name(), new ArrayList<ManagerListDTO>());
	}
	
	/**
	 * UserManager Object list를 설정한다.
	 * 
	 * @param managerDTO
	 */
	public static void setManagerDBList(List<ManagerListDTO> listManagerDTO) {
		HttpSession sStore = RWT.getRequest().getSession();
		sStore.setAttribute(NAME.ALL_MANAGER_DB_LIST.name(), listManagerDTO);
	}

	/**
	 * UserManager object list를 가져온다.
	 * 
	 * @return
	 */
	public static List<ManagerListDTO> getManagerDBList() {
		HttpSession sStore = RWT.getRequest().getSession();
		Object listObj = sStore.getAttribute(NAME.ALL_MANAGER_DB_LIST.name());
		if(listObj == null) {
			return new ArrayList<ManagerListDTO>();
		} else {
			return (List<ManagerListDTO>)listObj;
		}
	}
	
	/**
	 * is login?
	 * 
	 * @return
	 */
	public static boolean isLogin() {
		if(getUserSeq() == 0) return false;
		else return true;
	}
	
	/**
	 * Update session information.<br>
	 * <br>
	 * Session uses the information in multiple places(preference, user info etc.). 
	 * So when updating the information stored in the Session, 
	 * you must update the information given session.
	 * 
	 * @param key Session Attribute name
	 * @param value Object
	 */
	public static void updateSessionAttribute(String key, Object value) {
		HttpSession sStore = RWT.getRequest().getSession();
		sStore.setAttribute(key, value);
	}
	
	/**
	 * 사용자를 session에 등록
	 * 
	 * @param userDao
	 * @param loginType
	 * @param ip
	 */
	public static void addSession(UserDAO userDao, String loginType, String ip) {
		HttpSession sStore = RWT.getRequest().getSession();
		
		sStore.setAttribute(NAME.LOGIN_IP_TYPE.name(), loginType);
		sStore.setAttribute(NAME.LOGIN_IP.name(), ip);
		sStore.setAttribute(NAME.REPRESENT_ROLE_TYPE.name(), userDao.getRole_type());
		sStore.setAttribute(NAME.USER_SEQ.name(), userDao.getSeq());
		sStore.setAttribute(NAME.LOGIN_EMAIL.name(), userDao.getEmail());
//		sStore.setAttribute(NAME.LOGIN_PASSWORD.name(), userDao.getPasswd());
		sStore.setAttribute(NAME.LOGIN_NAME.name(), userDao.getName());
		sStore.setAttribute(NAME.IS_REGIST_DB.name(), userDao.getIs_regist_db());
		sStore.setAttribute(NAME.LANGUAGE.name(), userDao.getLanguage());
		sStore.setAttribute(NAME.TIMEZONE.name(), userDao.getTimezone());
		
		sStore.setAttribute(NAME.IS_SHARED_DB.name(), userDao.getIs_shared_db());
		sStore.setAttribute(NAME.IS_MODIFY_PERFERENCE.name(), userDao.getIs_modify_perference());
		sStore.setAttribute(NAME.LIMIT_ADD_DB_CNT.name(), userDao.getLimit_add_db_cnt());
		sStore.setAttribute(NAME.SERVICE_END.name(), userDao.getService_end());
		
		sStore.setAttribute(NAME.PERSPECTIVE.name(), "default");
		
		sStore.setAttribute(NAME.USE_OTP.name(), userDao.getUse_otp());
		sStore.setAttribute(NAME.OTP_SECRET_KEY.name(), userDao.getOtp_secret());
		
		sStore.setAttribute(NAME.UNLOCK_DB_LIST.name(), new ArrayList<Integer>());
	}
	
	/**
	 * get login ip type
	 * 
	 * @param getLoginIpType
	 */
	public static String getLoginIpType() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.LOGIN_IP_TYPE.name());
	}
	
	/**
	 * get login ip
	 * @return
	 */
	public static String getLoginIp() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.LOGIN_IP.name());
	}
	
//	/**
//	 * set password
//	 * 
//	 * @param strPasswd
//	 */
//	public static void setPassword(String strPasswd) {
//		HttpSession sStore = RWT.getRequest().getSession();
//		sStore.setAttribute(NAME.LOGIN_PASSWORD.name(), strPasswd);
//	}
	
	public static void setUesrSeq(int seq) {
		HttpSession sStore = RWT.getRequest().getSession();
		sStore.setAttribute(NAME.USER_SEQ.name(), seq);
	}
	public static int getUserSeq() {
		HttpSession sStore = RWT.getRequest().getSession();
		Object obj = sStore.getAttribute(NAME.USER_SEQ.name());
		
		if(obj == null) return 0;
		else return (Integer)obj;
	}
	
	public static String getEMAIL() {
		HttpSession sStore = RWT.getRequest().getSession();
		
		return (String)sStore.getAttribute(NAME.LOGIN_EMAIL.name());
	}
	
//	public static String getPassword() {
//		HttpSession sStore = RWT.getRequest().getSession();
//		return (String)sStore.getAttribute(NAME.LOGIN_PASSWORD.name());
//	}
	
	public static String getName() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.LOGIN_NAME.name());
	}
	public static String getIsRegistDB() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.IS_REGIST_DB.name());
	}
	
	public static String getIsSharedDB() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.IS_SHARED_DB.name());
	}
	
	public static Integer getLimitAddDBCnt() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (Integer)sStore.getAttribute(NAME.LIMIT_ADD_DB_CNT.name());
	}
	
	public static String getIsModifyPerference() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.IS_MODIFY_PERFERENCE.name());
	}
	
	public static Timestamp getServiceEnd() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (Timestamp)sStore.getAttribute(NAME.SERVICE_END.name());
	}
	
	public static String getUseOTP() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.USE_OTP.name());
	}
	public static String getOTPSecretKey() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.OTP_SECRET_KEY.name());
	}
	public static String getLangeage() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.LANGUAGE.name());
	}
	public static String getTimezone() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.TIMEZONE.name());
	}
	
	/**
	 * 자신이 대표 권한을 리턴합니다.
	 * 
	 * <pre>
	 * 권한 중복일 경우
	 * admin이면서 manager일수는 없습니다. 
	 * 	1) admin
	 *  2) manager 
	 *  3) dba 
	 *  4) user
	 * 
	 * group당 manager권한은 반듯이 하나입니다.
	 * manager권한이 정지되면 그룹을 수정 못하는 것으로.
	 * </pre>
	 * 
	 * @return
	 */
	public static String getRepresentRole() {
		HttpSession sStore = RWT.getRequest().getSession();
		return (String)sStore.getAttribute(NAME.REPRESENT_ROLE_TYPE.name());
	}
	
	public static boolean isSystemAdmin() {
		return PublicTadpoleDefine.USER_ROLE_TYPE.SYSTEM_ADMIN.name().equals(getRepresentRole()) ? true : false;
	}

	/**
	 * 초기 접속시 사용자의 모든 프리퍼런스 데이터를 설정합니다. 
	 */
	public static void setUserAllPreferenceData(Map<String, Object> mapUserInfo) {
		HttpSession sStore = RWT.getRequest().getSession();
		sStore.setAttribute(NAME.USER_INFO_DATA.name(), mapUserInfo);		
	}
	
	/**
	 * 기존 세션 정보를 추가합니다. 
	 * @param key
	 * @param obj
	 */
	public static void setUserInfo(String key, String obj) {
		
		HttpSession sStore = RWT.getRequest().getSession();
		Map<String, Object> mapUserInfoData = (Map<String, Object>)sStore.getAttribute(NAME.USER_INFO_DATA.name());
		UserInfoDataDAO userInfoDataDAO = (UserInfoDataDAO)mapUserInfoData.get(key);
		if(userInfoDataDAO == null) {
			userInfoDataDAO = new UserInfoDataDAO(SessionManager.getUserSeq(), key, obj);
		
			try {
				TadpoleSystem_UserInfoData.insertUserInfoData(userInfoDataDAO);
			} catch(Exception e) {
				logger.error("User data save exception [key]" + key + "[value]" + obj, e);
			}
		} else {
			userInfoDataDAO.setValue0(obj);
		}
			
		mapUserInfoData.put(key, userInfoDataDAO);
		sStore.setAttribute(NAME.USER_INFO_DATA.name(), mapUserInfoData);
	}
	
	/**
	 * 사용자 User 정보 .
	 * 
	 * @param key
	 * @param value 
	 * @return
	 */
	public static UserInfoDataDAO getUserInfo(String key, String value) {
		HttpSession sStore = RWT.getRequest().getSession();
		Map<String, Object> mapUserInfoData = (Map<String, Object>)sStore.getAttribute(NAME.USER_INFO_DATA.name());
		
		UserInfoDataDAO userData = (UserInfoDataDAO)mapUserInfoData.get(key);
		if(userData == null) {
			userData = new UserInfoDataDAO(SessionManager.getUserSeq(), key, value);
			try {
				TadpoleSystem_UserInfoData.insertUserInfoData(userData);
			} catch(Exception e) {
				logger.error("User data save exception [key]" + key + "[value]" + value, e);
			}
			
			mapUserInfoData.put(key, userData);
		}
		
		return userData;
	}
	
	/**
	 * set unlock db list
	 * @param userDB
	 * @return
	 */
	public static boolean setUnlokDB(final UserDBDAO userDB) {
		HttpSession sStore = RWT.getRequest().getSession();
		List<UserDBDAO> listUnlockDB = (List)sStore.getAttribute(NAME.UNLOCK_DB_LIST.name());
		
		return listUnlockDB.add(userDB);
	}
	
	/**
	 * is unlock db 
	 * @param userDB
	 * @return
	 */
	public static boolean isUnlockDB(final UserDBDAO userDB) {
		HttpSession sStore = RWT.getRequest().getSession();
		List<UserDBDAO> listUnlockDB = (List)sStore.getAttribute(NAME.UNLOCK_DB_LIST.name());
		
		return listUnlockDB.contains(userDB);
	}
	
	/**
	 * unlock session
	 * 
	 * @param userDB
	 */
	public static void removeUnlockDB(final UserDBDAO userDB) {
		HttpSession sStore = RWT.getRequest().getSession();
		List<UserDBDAO> listUnlockDB = (List)sStore.getAttribute(NAME.UNLOCK_DB_LIST.name());
		
		listUnlockDB.remove(userDB);
	}
	
	/**
	 * logout 처리를 합니다.
	 */
	public static void logout(final String strID) {
		HttpSessionCollectorUtil.getInstance().sessionDestroyed(strID);
		
		HttpServletRequest request = RWT.getRequest();
		try {
			HttpSession sStore = request.getSession();			
//			sStore.setAttribute(NAME.USER_SEQ.toString(), 0);
			sStore.invalidate();
		} catch(Throwable e) {
			// ignore exception
		}
		
		try {
			// fixed https://github.com/hangum/TadpoleForDBTools/issues/708
			// ps - 사용자 session id를 보여주고 싶지 않아서 배열을 이용해서 뺐어요. - hangum
			String[] arryRequestURL = StringUtils.split(request.getRequestURL().toString(), ";");
	     	String browserText = MessageFormat.format("parent.window.location.href = \"{0}\";", arryRequestURL[0]);
	     	JavaScriptExecutor executor = RWT.getClient().getService( JavaScriptExecutor.class );
	     	executor.execute("setTimeout('"+browserText+"', 100)" );
		} catch(Exception e) {
			logger.error("loguout", e);
		} finally {
//			removeConnection(strID);
		}
	}
	
	/**
	 * 사용자 커넥션을 삭제한다.
	 * 
	 * @param strID
	 */
	public static void removeConnection(final String strID) {
		removeTransactionInstance(strID);
		removeNonTransactionInstance(strID);
	}
	
	/**
	 * remove instance
	 * 
	 * @param strID
	 */
	private static void removeTransactionInstance(final String strID) {
		Job job = new Job("Remove transaction instance") { //$NON-NLS-1$
			@Override
			public IStatus run(IProgressMonitor monitor) {
				
				try {
					TadpoleSQLTransactionManager.executeAllRollback(strID);
				} catch(Exception e) {
					logger.error("removeTransactionInstance connection instance", e);
					return new Status(Status.WARNING, TadpoleEngineActivator.PLUGIN_ID, e.getMessage(), e);
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
			
		};
		
		job.setName("RemoveTransaction instance");
		job.setUser(false);
		job.schedule();
	}
	
	/**
	 * remove instance
	 * 
	 * @param strID
	 */
	private static void removeNonTransactionInstance(final String strID) {
		Job job = new Job("Remove non transaction connection instance") { //$NON-NLS-1$
			@Override
			public IStatus run(IProgressMonitor monitor) {
				
				try {
					TadpoleSQLManager.removeAllInstance(strID);
				} catch(Exception e) {
					logger.error("remove user connection instance", e);
					return new Status(Status.WARNING, TadpoleEngineActivator.PLUGIN_ID, e.getMessage(), e);
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
			
		};
		
		job.setName("Remove normal instance");
		job.setUser(false);
		job.schedule();
	}
	
	/**
	 * 사용자 session을 invalidate시킵니다.
	 */
	public static void invalidate() {
		try {
			HttpSession sStore = RWT.getRequest().getSession();
			
			HttpSessionContext hsc = sStore.getSessionContext();
			Enumeration ids = hsc.getIds();
			while(ids.hasMoreElements()) {
				String id = (String)ids.nextElement();
				
				if(logger.isDebugEnabled()) logger.debug("==========================> " + hsc.getSession(id));
			}
			
		} catch(Exception e) {
			logger.error("user session invalidate", e);
		}
	}
	
//	public static String getPerspective() {
//		UserInfoDataDAO userInfo = SessionManager.getUserInfo(NAME.PERSPECTIVE.name(), "default");
//		return userInfo.getValue0();
//	}
	
//	public static void setPerspective(String persp) { 
//		// db update 
//		try {
//			TadpoleSystem_UserInfoData.updateUserInfoData(NAME.PERSPECTIVE.name(), persp);
//			// session update
//			SessionManager.setUserInfo(NAME.PERSPECTIVE.name(), persp);
//			SessionManager.resetPerspective();
//		} catch (Exception e) {
//			logger.error("Error change perspective", e);
//		}
//	}
	
	public static void resetPerspective() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			window.getActivePage().resetPerspective();	
		}
	}

}
