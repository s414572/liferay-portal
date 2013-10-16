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

package com.liferay.portal.kernel.lar;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.Layout;

import java.util.List;

/**
 * @author Zsolt Szabo
 */
public class ExportImportHelperUtil {

	public static long[] collectAllParentLayoutIds(
			long groupId, boolean privateLayout, long[] layoutIds)
		throws Exception {

		return getExportImportHelper().collectAllParentLayoutIds(
			groupId, privateLayout, layoutIds);
	}

	public static ExportImportHelper getExportImportHelper() {
		return _exportImportHelper;
	}

	public List<Layout> collectParentLayouts(Layout layout)
		throws PortalException, SystemException {

		return getExportImportHelper().collectParentLayouts(layout);
	}

	public long[] getLayoutIds(List<Layout> layouts) {
		return getExportImportHelper().getLayoutIds(layouts);
	}

	public void setExportImportHelper(ExportImportHelper exportImportHelper) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_exportImportHelper = exportImportHelper;
	}

	private static ExportImportHelper _exportImportHelper;

}