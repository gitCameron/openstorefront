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

package edu.usu.sdl.openstorefront.web.rest.model;

import edu.usu.sdl.openstorefront.storage.model.AttributeType;
import java.util.List;

/**
 *
 * @author jlaw
 */
public class AttributeCodeWrapper
{
	private List<AttributeCodeView> data;
	private long totalNumber;

	
	public AttributeCodeWrapper(){
	}
	
	/**
	 * @return the data
	 */
	public List<AttributeCodeView> getData()
	{
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<AttributeCodeView> data)
	{
		this.data = data;
	}

	/**
	 * @return the totalNumber
	 */
	public long getTotalNumber()
	{
		return totalNumber;
	}

	/**
	 * @param totalNumber the totalNumber to set
	 */
	public void setTotalNumber(long totalNumber)
	{
		this.totalNumber = totalNumber;
	}
}