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
package edu.usu.sdl.openstorefront.service.manager;

import edu.usu.sdl.openstorefront.common.manager.Initializable;
import edu.usu.sdl.openstorefront.common.manager.PropertiesManager;
import edu.usu.sdl.openstorefront.core.entity.ComponentTag;
import edu.usu.sdl.openstorefront.core.view.ComponentSearchView;
import edu.usu.sdl.openstorefront.core.view.SearchResultAttribute;
import edu.usu.sdl.openstorefront.service.search.SearchServer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author dshurtleff
 */
public class SearchServerManager
	implements Initializable
{
	private static final Logger log = Logger.getLogger(SearchServerManager.class.getName());
	
	private static final String SOLR = "solr";
	private static final String ELASTICSEARCH = "elasticsearch";
	
	private static AtomicBoolean started = new AtomicBoolean(false);
	private static SearchServer searchServer;

	public static SearchServer getSearchServer()
	{
		return searchServer;
	}
	
	public static void init()
	{	
		String searchImplementation = PropertiesManager.getValue(PropertiesManager.KEY_SEARCH_SERVER,  SOLR).toLowerCase();
		switch(searchImplementation) 
		{
			case SOLR:
			{
				searchServer = new SolrManager();
			}
			break;
				
			case ELASTICSEARCH:
			{
				searchServer = new ElasticSearchManager();
			}
			break;
			default:
			{
				searchServer = new SolrManager();				
			}			
		}
		((Initializable)searchServer).initialize();
	}
	
	public static void cleanup()
	{
		if (searchServer != null) {
			((Initializable)searchServer).shutdown();
		}
	}	
	
	public static void updateSearchScore(String query, List<ComponentSearchView> views)
	{
		if (StringUtils.isNotBlank(query)) {
			String queryNoWild = query.replace("*", "").toLowerCase();
			for (ComponentSearchView view : views) {
				float score = 0;

				if (StringUtils.isNotBlank(view.getName())
						&& view.getName().toLowerCase().contains(queryNoWild)) {
					score += 100;
				}

				if (StringUtils.isNotBlank(view.getOrganization())
						&& view.getOrganization().toLowerCase().contains(queryNoWild)) {
					score += 50;
				}

				if (StringUtils.isNotBlank(view.getDescription())) {
					int count = StringUtils.countMatches(view.getDescription().toLowerCase(), queryNoWild);
					score += count * 5;
				}

				for (ComponentTag tag : view.getTags()) {
					int count = StringUtils.countMatches(tag.getText().toLowerCase(), queryNoWild);
					score += count * 5;
				}

				for (SearchResultAttribute attribute : view.getAttributes()) {
					int count = StringUtils.countMatches(attribute.getLabel().toLowerCase(), queryNoWild);
					score += count * 5;

					count = StringUtils.countMatches(attribute.getTypeLabel().toLowerCase(), queryNoWild);
					score += count * 5;
				}

				score = (float) score / 150f;
				if (score > 1) {
					score = 1;
				}

				view.setSearchScore(score);
			}
		}
	}
	
	@Override
	public void initialize()
	{
		SearchServerManager.init();
		started.set(true);
	}

	@Override
	public void shutdown()
	{
		SearchServerManager.cleanup();
		started.set(false);
	}

	@Override
	public boolean isStarted()
	{
		return started.get();
	}	
	
}
