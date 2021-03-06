/*
 * Copyright 2014 Space Dynamics Laboratory - Utah State University Research Foundation.
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
package edu.usu.sdl.openstorefront.service.manager.resource;

import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptions;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import edu.usu.sdl.openstorefront.common.exception.OpenStorefrontRuntimeException;
import edu.usu.sdl.openstorefront.common.manager.PropertiesManager;
import edu.usu.sdl.openstorefront.core.entity.ErrorTypeCode;
import edu.usu.sdl.openstorefront.core.entity.FeedbackTicket;
import edu.usu.sdl.openstorefront.service.manager.JiraManager;
import edu.usu.sdl.openstorefront.service.manager.model.ConnectionModel;
import edu.usu.sdl.openstorefront.service.manager.model.JiraIssueModel;
import edu.usu.sdl.openstorefront.service.manager.model.JiraIssueType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Represents Client
 *
 * @author dshurtleff
 */
public class JiraClient
		implements AutoCloseable
{

	private static final Logger log = Logger.getLogger(JiraClient.class.getName());

	private static final String BASE_API_URL = "/rest/api/2/";

	private final ConnectionModel connectionModel;
	private JiraRestClient restClient;
	private boolean alive = false;

	public JiraClient(ConnectionModel connectionModel)
	{
		this.connectionModel = connectionModel;
	}

	public void initConnection()
	{
		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		try {
			restClient = factory.createWithBasicHttpAuthentication(new URI(connectionModel.getUrl()), connectionModel.getUsername(), connectionModel.getCredential());
			alive = true;
		} catch (URISyntaxException ex) {
			throw new OpenStorefrontRuntimeException("Jira Server url is mal-formed ", "Check server url in properties.", ex, ErrorTypeCode.INTEGRATION);
		}
	}

	private JiraRestClient getRestClient()
	{
		if (alive) {
			return restClient;
		} else {
			throw new OpenStorefrontRuntimeException("Jira client has been closed.", "Obtain a new client from Manager.", ErrorTypeCode.INTEGRATION);
		}
	}

	private WebTarget getRestTarget(String service)
	{
		Client client = ClientBuilder.newClient().register(new BasicAuthenticator(connectionModel.getUsername(), connectionModel.getCredential()));
		WebTarget webTarget = client.target(connectionModel.getUrl() + BASE_API_URL + service);
		return webTarget;
	}

	public Issue getTicket(String ticket)
	{
		return getRestClient().getIssueClient().getIssue(ticket).claim();
	}

	public void deleteTicket(String ticketKey)
	{
		Promise<Void> promise = getRestClient().getIssueClient().deleteIssue(ticketKey, true);
		promise.claim();
	}

	public BasicIssue submitTicket(FeedbackTicket feedbackTicket)
	{
		BasicProject basicProject = null;
		IssueType issueType = null;

		GetCreateIssueMetadataOptions options = new GetCreateIssueMetadataOptionsBuilder()
				.withProjectKeys(PropertiesManager.getValueDefinedDefault(PropertiesManager.KEY_JIRA_FEEDBACK_PROJECT))
				.build();
		Iterable<CimProject> cimProjects = getRestClient().getIssueClient().getCreateIssueMetadata(options).claim();
		if (cimProjects != null) {
			for (CimProject cimProject : cimProjects) {
				//There should only be one
				basicProject = cimProject;
				for (IssueType issueTypeInProject : cimProject.getIssueTypes()) {
					if (issueTypeInProject.getName().equalsIgnoreCase(PropertiesManager.getValueDefinedDefault(PropertiesManager.KEY_JIRA_FEEDBACK_ISSUETYPE))) {
						issueType = issueTypeInProject;
						break;
					}
				}
			}
		}

		if (basicProject == null || issueType == null) {
			throw new OpenStorefrontRuntimeException("Unable to find project or issue type in jira.", "Check application properties and JIRA for correct values.");
		}

		IssueInputBuilder issueBuilder = new IssueInputBuilder(basicProject, issueType);
		issueBuilder.setDescription(feedbackTicket.fullDescription());
		issueBuilder.setSummary(feedbackTicket.fullSubject());

		Promise<BasicIssue> promise = getRestClient().getIssueClient().createIssue(issueBuilder.build());
		BasicIssue issue = promise.claim();
		return issue;
	}

	public Iterable<CimProject> getAllProjectMetaInformation(String projectKey, String issueTypeName)
	{
		GetCreateIssueMetadataOptions options = new GetCreateIssueMetadataOptionsBuilder()
				.withProjectKeys(projectKey)
				.withExpandedIssueTypesFields()
				.withIssueTypeNames(issueTypeName)
				.build();
		Iterable<CimProject> cimProjects = getRestClient().getIssueClient().getCreateIssueMetadata(options).claim();
		return cimProjects;
	}

	public Map<String, CimFieldInfo> getProjectIssueTypeFields(String projectKey, String issueTypeName)
	{
		Map<String, CimFieldInfo> fields = new HashMap<>();
		Iterable<CimProject> cimProjects = getAllProjectMetaInformation(projectKey, issueTypeName);
		cimProjects.forEach(project -> {
			project.getIssueTypes().forEach(type -> {
				fields.putAll(type.getFields());
			});
		});
		return fields;
	}

	public Iterable<BasicProject> getAllProjects()
	{
		Iterable<BasicProject> projects = getRestClient().getProjectClient().getAllProjects().claim();
		return projects;
	}

	public List<JiraIssueModel> getIssueTypesForProject(String projectKey)
	{
		List<JiraIssueModel> issueTypeNames = new ArrayList<>();
		GetCreateIssueMetadataOptions options = new GetCreateIssueMetadataOptionsBuilder()
				.withProjectKeys(projectKey)
				.build();
		Iterable<CimProject> cimProjects = getRestClient().getIssueClient().getCreateIssueMetadata(options).claim();
		cimProjects.forEach(project -> {
			project.getIssueTypes().forEach(type -> {
				JiraIssueModel temp = new JiraIssueModel();
				temp.setDescription(type.getDescription());
				temp.setId(type.getId());
				temp.setName(type.getName());
				issueTypeNames.add(temp);
			});
		});
		return issueTypeNames;
	}

	public ServerInfo getServerInfo()
	{
		ServerInfo serverInfo = getRestClient().getMetadataClient().getServerInfo().claim();
		return serverInfo;
	}

	public List<JiraIssueType> getProjectStatusForAllIssueTypes(String projectKey)
	{
		List<JiraIssueType> jiraIssueTypes;

		WebTarget webTarget = getRestTarget("project/" + projectKey + "/statuses");
		Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
		jiraIssueTypes = response.readEntity(new GenericType<List<JiraIssueType>>()
		{
		});

		return jiraIssueTypes;
	}

	@Override
	public void close()
	{
		log.log(Level.FINEST, "Releasing jira connection.");
		try {
			restClient.close();
		} catch (IOException ex) {
			log.log(Level.WARNING, "Failed to close JIRA connection. ", ex);
		} finally {
			alive = false;
			JiraManager.releaseClient(this);
		}
	}

	public boolean isAlive()
	{
		return alive;
	}

}
