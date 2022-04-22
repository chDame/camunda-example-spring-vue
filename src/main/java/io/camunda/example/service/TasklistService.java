package io.camunda.example.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.auth.SaasAuthentication;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;

@Service
public class TasklistService {
	@Value("${zeebe.client.cloud.taskList}")
	private String taskList;
	@Value("${zeebe.client.cloud.clusterId}")
	private String clusterId;
	@Value("${zeebe.client.cloud.clientId}")
	private String clientId;
	@Value("${zeebe.client.cloud.clientSecret}")
	private String clientSecret;

	private CamundaTaskListClient client;

	private CamundaTaskListClient getTasklistClient() throws TaskListException {
		if (client == null) {
			client = new CamundaTaskListClient.Builder().taskListUrl(taskList + clusterId)
					.authentication(new SaasAuthentication(clientId, clientSecret)).build();
		}
		return client;
	}

	public List<Task> getTasks(Boolean assigned, String assigneeId, TaskState state, Integer pageSize) throws TaskListException {
		return getTasklistClient().getTasksWithVariables(assigned, assigneeId, state, pageSize);
	}

	public Task completeTask(String taskId, Map<String, Object> inputs) throws TaskListException {
		return getTasklistClient().completeTask(taskId, inputs);
	}

}
