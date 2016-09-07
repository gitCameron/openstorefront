/*
 * Copyright 2016 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usu.sdl.openstorefront.web.test.highlight;

import edu.usu.sdl.openstorefront.core.entity.Highlight;
import edu.usu.sdl.openstorefront.web.test.BaseTestCase;

/**
 *
 * @author ccummings
 */
public class HighlightTest extends BaseTestCase
{

	private Highlight highlight = null;

	@Override
	protected void runInternalTest()
	{
		results.append("Creating highlight...<br>");
		highlight = new Highlight();
		highlight.setTitle("Highlight Test");
		highlight.setDescription("A test highlight for main page");
		highlight.setHighlightType(Highlight.TYPE_COMPONENT);
		highlight.setOrderingPosition(1);
		service.getSystemService().saveHightlight(highlight);
		highlight = (Highlight) highlight.find();

		results.append("Saving highlight...<br>");
		Highlight testHighlight = new Highlight();
		testHighlight.setTitle("Highlight Test");
		testHighlight = (Highlight) testHighlight.find();
		if (highlight.getHighlightId().equals(testHighlight.getHighlightId())) {
			results.append("Highlight saved successfully<br><br>");
		} else {
			failureReason.append("Unable to save highlight<br><br>");
		}

		results.append("Deactivating highlight...<br>");
		service.getSystemService().removeHighlight(highlight.getHighlightId());
		Highlight highlightRemoved = new Highlight();
		highlightRemoved.setTitle("Highlight Test");
		highlightRemoved.setActiveStatus(Highlight.INACTIVE_STATUS);
		highlight = (Highlight) highlightRemoved.find();

		if (highlight.getActiveStatus().equals(Highlight.INACTIVE_STATUS)) {
			results.append("Highlight successfully deactivated<br><br>");
		} else {
			failureReason.append("Unable to deactivate highlight<br><br>");
		}

		results.append("Activating highlight...");
		service.getSystemService().activateHighlight(highlight.getHighlightId());
		Highlight highlightActive = new Highlight();
		highlightActive.setTitle("Highlight Test");
		highlightActive.setActiveStatus(Highlight.ACTIVE_STATUS);
		highlight = (Highlight) highlightActive.find();

		if (highlight.getActiveStatus().equals(Highlight.ACTIVE_STATUS)) {
			results.append("Highlight successfully activated<br><br>");
		} else {
			failureReason.append("Unable to activate highlight<br><br>");
		}

		String highlightIdCheck = highlight.getHighlightId();
		results.append("Deleting highlight...<br>");
		service.getSystemService().deleteHighlight(highlightIdCheck);
		Highlight highlightDelete = new Highlight();
		highlightDelete.setHighlightId(highlightIdCheck);
		highlightDelete.setTitle("Highlight Test");
		highlight = (Highlight) highlightDelete.find();

		if (highlight == null) {
			results.append("Highlight deleted successfully<br><br>");
		} else {
			failureReason.append("Unable to delete highlight<br><br>");
		}
	}

	@Override
	public String getDescription()
	{
		return "Highlights Test";
	}

	@Override
	protected void cleanupTest()
	{
		super.cleanupTest();
		if (highlight != null) {
			service.getSystemService().deleteHighlight(highlight.getHighlightId());
		}
	}
}
