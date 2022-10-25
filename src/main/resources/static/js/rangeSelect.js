rsForm=document.forms.rangeSelectForm;

rsForm;

// 자동 완성
$('#movieKeyword').autocomplete({
	source : function(request, response) { //source: 입력시 보일 목록
	     $.ajax({
	           url : "/ajax/autocompleteTitle.do"  
	         , type : "POST"
	         , dataType: "JSON"
	         , data : {"keyword": request.term}	// 검색 키워드
	         , success : function(rep){ 	// 성공
	             response(
	                 $.map(rep, function(item, index) {
	                     return {
	                    	     label : item  	// 목록에 표시되는 값
	                           , value : item 	// 선택 시 input
	                           , idx : index
	                     }; 	 
	                 })
	             );    //response
	         }
	         ,error : function(){ //실패
	             
	         }
	     });
	}
	,focus : function(event, ui) { // 방향키로 자동완성단어 선택 가능하게 만들어줌	
			return false;
	}
	,minLength: 1// 최소 글자수
	,autoFocus : true // true == 첫 번째 항목에 자동으로 초점이 맞춰짐
	,delay: 100	//autocomplete 딜레이 시간(ms)
	,select : function(evt, ui) { 
      	// 아이템 선택시 실행 ui.item 이 선택된 항목을 나타내는 객체, lavel/value/idx를 가짐
			console.log(ui.item.label);
			console.log(ui.item.idx);
	 }
})

$('#personName').autocomplete({
	source : function(request, response) { //source: 입력시 보일 목록
	     $.ajax({
	           url : "/ajax/autocompleteName.do"  
	         , type : "POST"
	         , dataType: "JSON"
	         , data : {"keyword": request.term}	// 검색 키워드
	         , success : function(rep){ 	// 성공
	             response(
	                 $.map(rep, function(item, index) {
	                     return {
	                    	     label : item  	// 목록에 표시되는 값
	                           , value : item 	// 선택 시 input
	                           , idx : index
	                     }; 	 
	                 })
	             );    //response
	         }
	         ,error : function(){ //실패
	             
	         }
	     });
	}
	,focus : function(event, ui) { // 방향키로 자동완성단어 선택 가능하게 만들어줌	
			return false;
	}
	,minLength: 1// 최소 글자수
	,autoFocus : true // true == 첫 번째 항목에 자동으로 초점이 맞춰짐
	,delay: 100	//autocomplete 딜레이 시간(ms)
	,select : function(evt, ui) { 
      	// 아이템 선택시 실행 ui.item 이 선택된 항목을 나타내는 객체, lavel/value/idx를 가짐
			console.log(ui.item.label);
			console.log(ui.item.idx);
	 }
})



document.forms.rangeSelectForm.chkAir.addEventListener("click",(e)=>{
	const allInput = document.getElementsByClassName("dab");
	for(item of allInput){
		if(item.type!="checkbox"){
			item.disabled != e.target.checked;
		}
		else{
			item.disabled = e.target.checked;
		}
	}
	

	
})
	
document.forms.rangeSelectForm.chkKeyword.addEventListener("click",(e)=>{
	if(e.target.checked){
		document.forms.rangeSelectForm.movieKeyword.disabled = false;
		document.forms.rangeSelectForm.movieKeyword.focus();
	}else{
		document.forms.rangeSelectForm.movieKeyword.disabled = true;
		document.forms.rangeSelectForm.movieKeyword.value = null;
	}
});
	
document.forms.rangeSelectForm.chkPerson.addEventListener("click",(e)=>{
	if(e.target.checked){
		document.forms.rangeSelectForm.personName.disabled = false;
		document.forms.rangeSelectForm.personName.focus();
	}else{
		document.forms.rangeSelectForm.personName.disabled = true;
		document.forms.rangeSelectForm.personName.value = null;
	}
});

document.forms.rangeSelectForm.chkGenre.addEventListener("click",(e)=>{
	if(e.target.checked){
		document.forms.rangeSelectForm.genreId.disabled = false;
		document.forms.rangeSelectForm.genreId.focus();
	}else{
		document.forms.rangeSelectForm.genreId.disabled = true;
		document.forms.rangeSelectForm.genreId.value = "-1";
	}
});
	
// input text 숫자, 영어(대소문자), 한글(중성 제외)만
function OnInput(e){
	e.value=e.value.replace(/[^A-Za-z가-힣ㄱ-ㅎ0-9]/ig, '')
}

// 옵션 선택 유효성
document.getElementById("btn").addEventListener("click", (e)=>{
	e.preventDefault();
	if(!(rangeSelectForm.chkAir.checked || rangeSelectForm.chkGenre.checked+rangeSelectForm.chkKeyword.checked+rangeSelectForm.chkPerson.checked)){
		alert("검색 조건을 선택해주세요");
		return;
	}

	let message = "";
	
	let genreVal = rangeSelectForm.chkGenre.checked && rangeSelectForm.genreId.value == -1;
	let personVal = rangeSelectForm.chkPerson.checked && rangeSelectForm.personName.value == "";
	let keywordVal = rangeSelectForm.chkKeyword.checked && rangeSelectForm.movieKeyword.value == "";
	if(genreVal){
		message+="장르를 선택해주세요";
	}
	if(personVal){
		genreVal?message+="\n영화인을 입력해주세요":message+="영화인을 입력해주세요";
	}
	if(keywordVal){
		personVal||genreVal?message+="\n키워드를 입력해주세요":message+="키워드를 입력해주세요";
	}

	if(message!=""){
		alert(message);
		return;
	}
	
	rangeSelectForm.submit();
})



