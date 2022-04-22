Vue.component('my-header',{ template: '<nav class="navbar navbar-light bg-dark text-light">'+
		'<div class="container-fluid">'+
			'<a class="navbar-brand text-light" href="#">Hello {{$store.user.name}}</a>'+
		'</div>'+
	'</nav>'
 });
Vue.component('main-page',{
  template: '<div><my-header></my-header>'+
	'<div class="container-fluid bg-light">'+
		'<div class="row flex-nowrap">'+
			'<div class="col-auto px-0">'+
				'<side-bar></side-bar>'+
			'</div>'+
			'<main class="col ps-md-2 pt-2">'+
				'<h2>{{title}}</h2>'+
				'<mytasks v-if="$store.state==\'mytasks\'"></mytasks>'+
				'<unassignedtasks v-if="$store.state==\'unassignedtasks\'"></unassignedtasks>'+
				'<archivedtasks v-if="$store.state==\'archivedtasks\'"></archivedtasks>'+
			'</main>'+
		'</div>'+
	'</div>'+
	'<meeting-note-modal></meeting-note-modal>'+
	'<new-meeting-modal></new-meeting-modal></div>',
	computed: {
		title() {
			if (this.$store.state=='mytasks') {
				return 'My tasks';
			}
			if (this.$store.state=='unassignedtasks') {
				return 'Unassigned tasks';
			}
			return 'My closed tasks';
		}
	}
});
Vue.component('mytasks',{
  template: '<div><task v-for="task in tasks" :task="task"></task></div>',
  data() {
    return {
      tasks: []
	}
  },
  created: function () {
    axios.get('/meetings/tasks?assigneeId='+this.$store.user.name+'&state=CREATED').then(response => {
		this.tasks = response.data; 
	}).catch(error => {
		alert(error.message); 
	})
  }
});
Vue.component('unassignedtasks',{
  template: '<task v-for="task in tasks" :task="task"></task>',
  data() {
    return {
      tasks: []
	}
  },
  created: function () {
    axios.get('/meetings/tasks?assigned=false&state=CREATED').then(response => {
		this.tasks = response.data; 
	}).catch(error => {
		alert(error.message); 
	})
  }
});
Vue.component('archivedtasks',{
  template: '<task v-for="task in tasks" :task="task"></task>',
  data() {
    return {
      tasks: []
	}
  },
  created: function () {
    axios.get('/meetings/tasks?assigneeId='+this.$store.user.name+'&state=COMPLETED').then(response => {
		this.tasks = response.data; 
	}).catch(error => {
		alert(error.message); 
	})
  }
});
Vue.component('task',{
  template: '<div v-if="!task.done" class="card" style="width: 18rem;">'+
  	'<div class="card-body">'+
    	'<h5 class="card-title">{{ task.name }}</h5><h6 class="card-subtitle mb-2 text-muted">{{ task.processName }}</h6>'+
    	'<p class="card-text">{{ task.creationTime }}</p>'+
    	'<a @click="openTask()" class="card-link">Open</a>'+
    '</div></div>',
  props:["task"],
  methods: {
	openTask() {
		this.$store.task=this.task;
		let modal = new bootstrap.Modal(document.getElementById('meetingNoteModal'), {});
		modal.show();
	}
  }
});
Vue.component('side-bar',{
  template: '<div id="sidebar" class="border-end">'+
		'<div id="sidebar-nav" class="list-group bg-secondary border-0 rounded-0 text-sm-start">'+		
			'<ul class="navbar-nav me-auto mb-2 mb-lg-0">'+
				'<li class="nav-item"><a class="nav-link text-light p-2" @click="openTasks(\'mytasks\')"><i class="bi bi-person-check"></i> My tasks</a></li>'+
				'<li class="nav-item"><a class="nav-link text-light p-2" @click="openTasks(\'unassignedtasks\')"><i class="bi bi-person-x"></i> Unassigned tasks</a></li>'+
				'<li class="nav-item"><a class="nav-link text-light p-2" @click="openTasks(\'archivedtasks\')"><i class="bi bi-check-square"></i> My closed tasks</a></li>'+
				'<li class="nav-item"><a class="nav-link text-light p-2" @click="newMeeting()"><i class="bi bi-chat"></i> New meeting</a></li>'+
			'</ul>'+
		'</div>'+
	'</div>',
  methods: {
	  openTasks(state) {
		this.$store.state=state;
	  },
	  newMeeting() {
		let modal = new bootstrap.Modal(document.getElementById('newMeetingModal'), {});
		modal.show();
	  }
  }
});
Vue.component('meeting-note-modal',{
  template: '<div class="modal fade" id="meetingNoteModal" ref="meetingNoteModal" tabindex="-1">'+
  '<div class="modal-dialog">'+
    '<div class="modal-content">'+
      '<div class="modal-header bg-secondary text-light">'+
        '<h5 class="modal-title">{{date}} {{meetingType}} Meeting notes</h5>'+
        '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>'+
      '</div>'+
      '<div class="modal-body">'+
        '<div class="input-group mb-3">'+
			'<span class="input-group-text">Name</span>'+
			'<input type="text" class="form-control" placeholder="Notes" v-model="data.note">'+
        '</div>'+
	  '</div>'+
      '<div class="modal-footer">'+
        '<button type="button" class="btn btn-primary" @click="saveData" data-bs-dismiss="modal">Save</button>'+
        '<button class="btn btn-link" data-bs-dismiss="modal">Cancel</button>'+
      '</div>'+
    '</div>'+
  '</div>'+
'</div>',
  data() {
    return {
		data: {
		  note: ""
		}
	}
  },
  methods: {
	  saveData() {
		 axios.post('/meetings/'+this.$store.task.id+'/notes?notes='+this.data.note).then(response => {
			Vue.set(this.$store.task, 'done', true);
		 }).catch(error => {
			alert(error.message); 
		 }) 
	  }
  },
  computed: {
	meetingType() {
		if (this.$store.task.variables) {
			for(i=0;i<this.$store.task.variables.length;i++) {
				variable = this.$store.task.variables[i];
				if (variable.name=="meetingType") {
					return variable.previewValue;
				}
			}
		}
		return "";
	},
	date() {
	  	return this.$store.task.creationTime.substring(0,10);
	}
  }
});
Vue.component('new-meeting-modal',{
  template: '<div class="modal fade" id="newMeetingModal" ref="newMeetingModal" tabindex="-1">'+
  '<div class="modal-dialog">'+
    '<div class="modal-content">'+
      '<div class="modal-header bg-secondary text-light">'+
        '<h5 class="modal-title">New meeting</h5>'+
        '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>'+
      '</div>'+
      '<div class="modal-body">'+
		'<div class="input-group mb-3">'+
		  '<label class="input-group-text" for="meetingType">Meeting type</label>'+
		  '<select class="form-select" id="meetingType" v-model="data.meetingType">'+
			'<option>COPIL</option>'+
			'<option>COPROJ</option>'+
			'<option>Standup</option>'+
			'<option>Coffee</option>'+
		  '</select>'+
		'</div>'+
      '</div>'+
      '<div class="modal-footer">'+
        '<button type="button" class="btn btn-primary" data-bs-dismiss="modal" @click="newMeeting()">New Meeting</button>'+
        '<button class="btn btn-link" data-bs-dismiss="modal">Cancel</button>'+
      '</div>'+
    '</div>'+
  '</div>'+
'</div>',
  data() {
    return {
		data: {
		  meetingType:"COPIL"
		}
	}
  },
  methods: {
	  newMeeting() {
	    axios.post('/meetings/new-meeting?meetingType='+this.data.meetingType).then(response => {
			
		}).catch(error => {
			alert(error.message); 
		})
	  }
  }
});