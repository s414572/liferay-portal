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

package com.liferay.portal.lar;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.ExportImportHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zsolt Szabo
 */
public class ExportImportHelperImpl implements ExportImportHelper {

	@Override
	public long[] getLayoutIds(List<Layout> layouts) {
		long[] layoutIds = new long[layouts.size()];

		for (int i = 0; i < layouts.size(); i++) {
			Layout layout = layouts.get(i);

			layoutIds[i] = layout.getLayoutId();
		}

		return layoutIds;
	}

	@Override
	public long[] getParentLayoutIds(
			long groupId, boolean privateLayout, long[] layoutIds)
		throws Exception {

		long[] parentLayoutIds = new long[0];

		if (layoutIds == null) {
			return parentLayoutIds;
		}

		for (int i = 0; i < layoutIds.length; i++) {
			Layout layout = LayoutLocalServiceUtil.getLayout(
				groupId, privateLayout, layoutIds[i]);

			List<Layout> parentLayouts = getParentLayouts(layout);

			long[] curParentLayoutIds = getLayoutIds(parentLayouts);

			for (long parentLayoutId : curParentLayoutIds) {
				if (!ArrayUtil.contains(parentLayoutIds, parentLayoutId)) {
					parentLayoutIds = ArrayUtil.append(
						parentLayoutIds, parentLayoutId);
				}
			}
		}

		return parentLayoutIds;
	}

	protected List<Layout> getParentLayouts(Layout layout)
		throws PortalException, SystemException {

		List<Layout> parentLayouts = new ArrayList<Layout>();

		Layout parentLayout = layout;

		while (parentLayout.getParentLayoutId() > 0) {
			parentLayout = LayoutLocalServiceUtil.getLayout(
				layout.getGroupId(), layout.isPrivateLayout(),
				parentLayout.getParentLayoutId());

			parentLayouts.add(parentLayout);
		}

		return parentLayouts;
	}

}