/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.auth;

import com.liferay.portal.action.LayoutAction;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.model.PortletConstants;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.util.Encryptor;
import com.liferay.util.PwdGenerator;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Amos Fong
 * @author Tomas Polesovsky
 */
public class SessionAuthToken implements AuthToken {

	@Override
	public void check(HttpServletRequest request) throws PrincipalException {
		String origin = GetterUtil.getString(request.getAttribute(
			AuthTokenUtil.AUTH_TOKEN_ORIGIN));

		if (Validator.isNotNull(origin)) {
			origin = LayoutAction.class.getName();
		}

		checkCSRFToken(request, origin);
	}

	public void checkCSRFToken(HttpServletRequest request, String origin)
		throws PrincipalException {

		if (!PropsValues.AUTH_TOKEN_CHECK_ENABLED) {
			return;
		}

		String sharedSecret = ParamUtil.getString(request, "p_auth_secret");

		if (isValidSharedSecret(sharedSecret)) {
			return;
		}

		long companyId = PortalUtil.getCompanyId(request);

		if (origin.equals(LayoutAction.class.getName())) {
			String ppid = ParamUtil.getString(request, "p_p_id");

			String portletNamespace = PortalUtil.getPortletNamespace(ppid);

			String strutsAction = ParamUtil.getString(
				request, portletNamespace + "struts_action");

			if (isPortletCSRFWhitelisted(companyId, ppid, strutsAction)) {
				return;
			}
		}

		String csrfToken = ParamUtil.getString(request, "p_auth");

		String sessionToken = getSessionAuthenticationToken(
			request, _CSRF, false);

		if (!csrfToken.equals(sessionToken)) {
			throw new PrincipalException("Invalid authentication token");
		}
	}

	@Override
	public String getToken(HttpServletRequest request) {
		return getSessionAuthenticationToken(request, _CSRF, true);
	}

	@Override
	public String getToken(
		HttpServletRequest request, long plid, String portletId) {

		return getSessionAuthenticationToken(
			request, PortletPermissionUtil.getPrimaryKey(plid, portletId),
			true);
	}

	public boolean isPortletCSRFWhitelisted(
		long companyId, String portletId, String strutsAction) {

		String rootPortletId = PortletConstants.getRootPortletId(portletId);

		Set<String> whitelist = PortalUtil.getAuthTokenIgnorePortlets();

		if (whitelist.contains(rootPortletId)) {
			return true;
		}

		if (Validator.isNotNull(strutsAction)) {
			Set<String> whitelistActions =
				PortalUtil.getAuthTokenIgnoreActions();

			if (whitelistActions.contains(strutsAction) &&
				isValidStrutsAction(companyId, rootPortletId, strutsAction)) {

				return true;
			}
		}

		return false;
	}

	public boolean isValidSharedSecret(String sharedSecret) {
		if (Validator.isNull(sharedSecret)) {
			return false;
		}

		if (Validator.isNull(PropsValues.AUTH_TOKEN_SHARED_SECRET)) {
			return false;
		}

		return sharedSecret.equals(
			Encryptor.digest(PropsValues.AUTH_TOKEN_SHARED_SECRET));
	}

	protected String getSessionAuthenticationToken(
		HttpServletRequest request, String key, boolean createToken) {

		HttpSession session = request.getSession();

		String tokenKey = WebKeys.AUTHENTICATION_TOKEN.concat(key);

		String sessionAuthenticationToken = (String)session.getAttribute(
			tokenKey);

		if (createToken && Validator.isNull(sessionAuthenticationToken)) {
			sessionAuthenticationToken = PwdGenerator.getPassword();

			session.setAttribute(tokenKey, sessionAuthenticationToken);
		}

		return sessionAuthenticationToken;
	}

	protected boolean isValidStrutsAction(
		long companyId, String portletId, String strutsAction) {

		try {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				companyId, portletId);

			if (portlet == null) {
				return false;
			}

			String strutsPath = strutsAction.substring(
				1, strutsAction.lastIndexOf(CharPool.SLASH));

			if (strutsPath.equals(portlet.getStrutsPath()) ||
				strutsPath.equals(portlet.getParentStrutsPath())) {

				return true;
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	private static final String _CSRF = "#CSRF";

}