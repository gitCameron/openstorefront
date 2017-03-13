/*
 * Copyright 2017 Space Dynamics Laboratory - Utah State University Research Foundation.
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
import edu.usu.sdl.openstorefront.core.annotation.ConsumeField;
import java.io.Serializable;
import javax.persistence.Version;

/**
 *
 * @author dshurtleff
 */
@APIDescription("This is part of the alert entity")
public class UserManagementAlertOption
	implements Serializable
{
	@ConsumeField
	private Boolean alertOnUserRegistration;
	
	@ConsumeField
	private Boolean alertOnUserNeedsApproval;		

	@Version
	private String storageVersion;

	public UserManagementAlertOption()
	{
	}

	public Boolean getAlertOnUserRegistration()
	{
		return alertOnUserRegistration;
	}

	public void setAlertOnUserRegistration(Boolean alertOnUserRegistration)
	{
		this.alertOnUserRegistration = alertOnUserRegistration;
	}

	public Boolean getAlertOnUserNeedsApproval()
	{
		return alertOnUserNeedsApproval;
	}

	public void setAlertOnUserNeedsApproval(Boolean alertOnUserNeedsApproval)
	{
		this.alertOnUserNeedsApproval = alertOnUserNeedsApproval;
	}

	public String getStorageVersion()
	{
		return storageVersion;
	}

	public void setStorageVersion(String storageVersion)
	{
		this.storageVersion = storageVersion;
	}
	
	
	
}
