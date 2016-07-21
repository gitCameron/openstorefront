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
package edu.usu.sdl.openstorefront.service.io.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dshurtleff
 */
public class DataMapper
{
	private String field;
	private boolean rootField;
	private List<DataTransform> transforms = new ArrayList<>();
	private Class entityClass;
	private String entityField;

	public DataMapper()
	{
	}
	
	public Object applyTransforms(String value) {
		Object transformedData = value;
		for (DataTransform dataTransform : transforms) {
			transformedData = dataTransform.transform(transformedData);
		}
		return transformedData;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public List<DataTransform> getTransforms()
	{
		return transforms;
	}

	public void setTransforms(List<DataTransform> transforms)
	{
		this.transforms = transforms;
	}

	public String getEntityField()
	{
		return entityField;
	}

	public void setEntityField(String entityField)
	{
		this.entityField = entityField;
	}

	public Class getEntityClass()
	{
		return entityClass;
	}

	public void setEntityClass(Class entityClass)
	{
		this.entityClass = entityClass;
	}

	public boolean getRootField()
	{
		return rootField;
	}

	public void setRootField(boolean rootField)
	{
		this.rootField = rootField;
	}
	
}