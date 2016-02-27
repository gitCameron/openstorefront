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
package edu.usu.sdl.openstorefront.core.entity;

import edu.usu.sdl.openstorefront.core.annotation.APIDescription;
import edu.usu.sdl.openstorefront.core.annotation.FK;
import edu.usu.sdl.openstorefront.core.annotation.PK;
import edu.usu.sdl.openstorefront.core.annotation.ValidValueType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dshurtleff
 */
@APIDescription("Holds error information for file history")
public class FileHistoryError
		extends StandardEntity<FileHistoryError>
{

	@NotNull
	@PK(generated = true)
	private String fileHistoryErrorId;

	@NotNull
	@FK(value = FileHistory.class)
	private String fileHistoryId;

	private Integer recordNumber;
	private String errorMessage;

	@ValidValueType(value = "", lookupClass = FileHistoryErrorType.class)
	@FK(value = FileHistoryErrorType.class)
	private String fileHistoryErrorType;

	public FileHistoryError()
	{
	}

	public String getFileHistoryErrorId()
	{
		return fileHistoryErrorId;
	}

	public void setFileHistoryErrorId(String fileHistoryErrorId)
	{
		this.fileHistoryErrorId = fileHistoryErrorId;
	}

	public String getFileHistoryId()
	{
		return fileHistoryId;
	}

	public void setFileHistoryId(String fileHistoryId)
	{
		this.fileHistoryId = fileHistoryId;
	}

	public Integer getRecordNumber()
	{
		return recordNumber;
	}

	public void setRecordNumber(Integer recordNumber)
	{
		this.recordNumber = recordNumber;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getFileHistoryErrorType()
	{
		return fileHistoryErrorType;
	}

	public void setFileHistoryErrorType(String fileHistoryErrorType)
	{
		this.fileHistoryErrorType = fileHistoryErrorType;
	}

}