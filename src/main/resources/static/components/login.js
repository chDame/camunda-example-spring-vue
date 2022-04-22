Vue.component('login',{
  template: '<form>'
  +'<div class="form-floating mb-3">'
    +'<input type="text" id="inputUsername" class="form-control" v-model="login"  placeholder="username"/>'
    +'<label for="inputUsername">Username</label>'
  +'</div>'

  +'<div class="form-floating mb-3">'
    +'<input type="password" id="inputPass" class="form-control" v-model="password"  placeholder="password"/>'
    +'<label for="inputPass">Password</label>'
  +'</div>'

  +'<button type="button" class="btn btn-primary btn-block mb-4" v-on:click="authenticate()"><i class="bi bi-send"></i> Sign in</button>'
  +'</form>',
  data: function() {
	return {"login":null, "password":null}
  },
  methods: {
	authenticate(obj) {
		this.$store.user.name=this.login;
		this.$store.auth=true;
		
	}
  }
});