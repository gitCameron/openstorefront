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
package edu.usu.sdl.describe.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author dshurtleff
 */
@Root(name="Assertion", strict = false)
public class Assertion
{
	@Attribute(required = false)
	String type;
	
	@Element(name="StringStatement", required = false)			
	private String statement;
	
	@Element(name="StructuredStatement", required = false)			
	private StructuredStatement structuredStatement;

	public Assertion()
	{
	}

	public String getStatement()
	{
		return statement;
	}

	public void setStatement(String statement)
	{
		this.statement = statement;
	}

	public StructuredStatement getStructuredStatement()
	{
		return structuredStatement;
	}

	public void setStructuredStatement(StructuredStatement structuredStatement)
	{
		this.structuredStatement = structuredStatement;
	}

}
