package io.camunda.example.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.example.service.TasklistService;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.zeebe.spring.client.ZeebeClientLifecycle;


@RestController
@RequestMapping("/meetings")
@CrossOrigin(origins = "*")
public class CamundaController {


	@Autowired
	private TasklistService tasklistService;
	
    @Autowired
    private ZeebeClientLifecycle client;
    
    @PostMapping("/new-meeting")
    public void newMeeting(@RequestParam(defaultValue = "COPIL", required = false) String meetingType){
        client.newCreateInstanceCommand()
            .bpmnProcessId("MeetingProcess")
            .latestVersion()
            .variables(Map.of("meetingType", meetingType))
            .send();
    }

    @PostMapping("/new-meeting-from-message")
    public void newMessageMeeting(@RequestParam(defaultValue = "Standup", required = false) String meetingType){
        client.newPublishMessageCommand()
        	.messageName("meetingMessage")
        	.correlationKey(UUID.randomUUID().toString())
        	.messageId("meetingMessage")
            .variables(Map.of("meetingType", meetingType))
            .send();
    }
    
    @PostMapping("{taskId}/notes")
    public Task takeNotes(@PathVariable String taskId, @RequestParam String notes) throws TaskListException{
    	return tasklistService.completeTask(taskId, Map.of("note", notes));
    }
    
    @GetMapping(value = "/tasks")
	public List<Task> tasks(@RequestParam(required = false) String assigneeId, 
			@RequestParam(required = false) Boolean assigned,
			@RequestParam(required = false) String state,
			@RequestParam(required = false) Integer pageSize)  throws TaskListException {
    	TaskState taskState=null;
    	if (state!=null) {
    		taskState = TaskState.valueOf(state);
    	}
		return tasklistService.getTasks(assigned, assigneeId, taskState, pageSize);		
	}

}
