var view = document.querySelectorAll(".view_item");

view.forEach(function(hi){
	hi.addEventListener("click", function(){
		view.forEach(function(hi){
			hi.classList.toggle("red");
		})
	})
})
