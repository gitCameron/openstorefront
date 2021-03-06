/*
 * Copyright 2015 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.core.sort;

import edu.usu.sdl.openstorefront.core.api.Service;
import edu.usu.sdl.openstorefront.core.api.ServiceProxyFactory;
import edu.usu.sdl.openstorefront.core.entity.ChecklistQuestion;
import edu.usu.sdl.openstorefront.core.entity.EvaluationSection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dshurtleff
 * @param <T>
 */
public class ChecklistQuestionComparator<T extends ChecklistQuestion>
		implements Comparator<T>
{

	private Map<String, Integer> sectionScore = new HashMap<>();

	public ChecklistQuestionComparator()
	{
		Service service = ServiceProxyFactory.getServiceProxy();
		List<EvaluationSection> sections = service.getLookupService().findLookup(EvaluationSection.class);
		sections.sort(new LookupComparator<>());

		int score = 1;
		for (EvaluationSection section : sections) {
			sectionScore.put(section.getCode(), score);
		}

	}

	@Override
	public int compare(T o1, T o2)
	{
		Integer score1 = sectionScore.get(o1.getEvaluationSection());
		Integer score2 = sectionScore.get(o2.getEvaluationSection());
		return score1.compareTo(score2);
	}

}
