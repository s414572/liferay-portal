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

package com.liferay.portalweb.socialofficehome.activities.activitiesblockedsouser.sousviewactivitiesblockedsouser3;

import com.liferay.portalweb.portal.BaseTestCase;
import com.liferay.portalweb.portal.util.RuntimeVariables;

/**
 * @author Brian Wing Shun Chan
 */
public class SOUs1_ViewActivitiesBlockedSOUser3Test extends BaseTestCase {
	public void testSOUs1_ViewActivitiesBlockedSOUser3()
		throws Exception {
		selenium.selectWindow("null");
		selenium.selectFrame("relative=top");
		selenium.open("/user/socialoffice01/so/dashboard");
		assertEquals(RuntimeVariables.replace("Activities"),
			selenium.getText(
				"xPath=(//h1[@class='portlet-title']/span)[contains(.,'Activities')]"));
		assertEquals(RuntimeVariables.replace("Connections"),
			selenium.getText("link=Connections"));
		selenium.clickAt("link=Connections",
			RuntimeVariables.replace("Connections"));
		selenium.waitForPageToLoad("30000");
		selenium.waitForVisible("//div[@class='no-activities']");
		Thread.sleep(1000);
		assertEquals(RuntimeVariables.replace("There are no activities."),
			selenium.getText("//div[@class='no-activities']"));
		assertTrue(selenium.isElementNotPresent(
				"//div[@class='activity-user-name']"));
		assertTrue(selenium.isElementNotPresent(
				"//div[@class='activity-action']"));
		assertTrue(selenium.isElementNotPresent("//div[@class='activity-body']"));
		assertFalse(selenium.isTextPresent(
				"Social03 Office03 User03 in Open Site Name"));
		assertFalse(selenium.isTextPresent("Wrote a new forum post."));
		assertFalse(selenium.isTextPresent("Forums Thread1 Message Subject"));
		assertFalse(selenium.isTextPresent("Forums Thread1 Message Body"));
		assertFalse(selenium.isTextPresent("Social03 Office03 User03"));
		assertFalse(selenium.isTextPresent("Microblogs Post1"));
	}
}