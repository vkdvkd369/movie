rsForm=document.forms.rangeSelectForm;

rsForm;

$('#autoComplete').autocomplete({
	source : function(request, response) { //source: 입력시 보일 목록
	     $.ajax({
	           url : "/ajax/autocomplete.do"  
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

//  장르 체크박스 여부에 따라 활성화/비활성화
function checkDisableGen(form){    
	const textboxGen_elem = document.getElementById('genrechoose');
	textboxGen_elem.disabled = chkGenre.checked ? false : true;
	
 	if(textboxGen_elem.disabled) {
 	// - 텍스트박스가 비활성화 된 경우 : 텍스트박스 초기화
	  	textboxGen_elem.value = "장르 선택";
  	}else{
  	// - 텍스트박스가 활성화 된 경우 : 포커스 이동
	  	textboxGen_elem.focus();
  	}
 }
 	
//  영화인 체크박스 여부에 따라 활성화/비활성화
function checkDisablePe(form) {
	  const textboxPe_elem = document.getElementById('moviePeTe');
	  textboxPe_elem.disabled = chkPeople.checked ? false : true;
	  
	  if(textboxPe_elem.disabled) {
		  textboxPe_elem.value = null;
	  }else{
		  textboxPe_elem.focus();
	  }
}

//  키워드 체크박스 여부에 따라 활성화/비활성화
function checkDisableKey(form){    
	const textboxKey_elem = document.getElementById('autoComplete');
	textboxKey_elem.disabled = chkKeyword.checked ? false : true;
	
 	if(textboxKey_elem.disabled) {
	  	textboxKey_elem.value = null;
  	}else{
	  	textboxKey_elem.focus();
  	}
 }
	
// input text 숫자, 영어(대소문자), 한글(중성 제외)만
function OnInput(e){
	e.value=e.value.replace(/[^A-Za-z가-힣ㄱ-ㅎ0-9]/ig, '')
}

// 옵션 선택 유효성
function search_onclick(){
	theForm=document.rangeSelectForm;
	
	// 전체 체크 x	
	if(theForm.chkAir.checked!=true && theForm.chkGenre.checked!=true && theForm.chkPeople.checked!=true && 
			theForm.chkKeyword.checked!=true){
		alert("옵션을 선택해 주세요.");
		event.preventDefault();
	} 
	
	// 장르
	if(theForm.chkAir.checked!=true && theForm.chkGenre.checked!=false && theForm.chkPeople.checked!=true && 
			theForm.chkKeyword.checked!=true && theForm.genrechoose.value=="장르 선택"){
		alert("장르를 선택해 주세요.");
		event.preventDefault();
	} 
	
	// 현재상영중, 장르
	else if(theForm.chkPeople.checked!=true && theForm.chkAir.checked!=false && theForm.chkGenre.checked!=false && 
			theForm.chkKeyword.checked!=true && theForm.genrechoose.value=="장르 선택"){
		alert("장르를 선택해 주세요.");
		event.preventDefault();
	}
	
	// 현재상영중, 장르, 영화인
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=false && 
			theForm.chkGenre.checked!=false && theForm.chkKeyword.checked!=true && theForm.genrechoose.value=="장르 선택"){
		alert("장르를 선택해 주세요.");
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=false && 
			theForm.chkGenre.checked!=false && theForm.chkKeyword.checked!=true && theForm.moviePeTe.value==""){ 
		alert("영화인을 작성해 주세요.");
		document.getElementById("moviePeTe").focus();
		event.preventDefault();
	}
	
	// 장르, 영화인, 키워드
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=true && theForm.chkGenre.checked!=false && 
			theForm.chkKeyword.checked!=false && theForm.genrechoose.value=="장르 선택"){
		alert("장르를 선택해 주세요.");
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=true && 
			theForm.chkGenre.checked!=false && theForm.chkKeyword.checked!=false && theForm.moviePeTe.value==""){ // 
		alert("영화인을 작성해 주세요.");
		document.getElementById("moviePeTe").focus();
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=true && 
			theForm.chkGenre.checked!=false && theForm.chkKeyword.checked!=false && theForm.autoComplete.value==""){ // 
		alert("키워드를 작성해 주세요.");
		document.getElementById("autoComplete").focus();
		event.preventDefault();
	}
	
	// 영화인, 키워드
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=true && 
			theForm.chkGenre.checked!=true && theForm.chkKeyword.checked!=false && theForm.moviePeTe.value==""){ // 
		alert("영화인을 작성해 주세요.");
		document.getElementById("moviePeTe").focus();
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=true && 
			theForm.chkGenre.checked!=true && theForm.chkKeyword.checked!=false && theForm.autoComplete.value==""){ // 
		alert("키워드를 작성해 주세요.");
		document.getElementById("autoComplete").focus();
		event.preventDefault();
	}
	
	// 현재상영중, 장르, 영화인, 키워드
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=false && theForm.chkGenre.checked!=false && 
			theForm.chkKeyword.checked!=false && theForm.genrechoose.value=="장르 선택"){
		alert("장르를 선택해 주세요.");
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=false && 
			theForm.chkGenre.checked!=false && theForm.chkKeyword.checked!=false && theForm.moviePeTe.value==""){ // 
		alert("영화인을 작성해 주세요.");
		document.getElementById("moviePeTe").focus();
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=false && 
			theForm.chkGenre.checked!=false && theForm.chkKeyword.checked!=false && theForm.autoComplete.value==""){ // 
		alert("키워드를 작성해 주세요.");
		document.getElementById("autoComplete").focus();
		event.preventDefault();
	}
	
	// 영화인
	else if(theForm.chkPeople.checked=true && theForm.chkAir.checked!=true && theForm.chkGenre.checked!=true && 
			theForm.chkKeyword.checked!=true && theForm.moviePeTe.value==""){
		alert("영화인을 작성해 주세요.");
		document.getElementById("moviePeTe").focus();
		event.preventDefault();
	}
	
	// 장르, 영화인
	else if(theForm.chkPeople.checked!=false && theForm.chkAir.checked!=true && theForm.chkGenre.checked!=false && 
			theForm.chkKeyword.checked!=true && theForm.genrechoose.value=="장르 선택"){
		alert("장르를 선택해 주세요.");
		event.preventDefault();
	}
	else if(theForm.chkPeople.checked=true && theForm.chkAir.checked!=true && theForm.chkGenre.checked!=false && 
			theForm.chkKeyword.checked!=true && theForm.moviePeTe.value==""){
		alert("영화인을 작성해 주세요.");
		document.getElementById("moviePeTe").focus();
		event.preventDefault();
	}
	
	// 키워드
	else if(theForm.chkKeyword.checked!=false && theForm.chkAir.checked!=true && theForm.chkGenre.checked!=true && 
			theForm.chkPeople.checked!=true && theForm.autoComplete.value==""){
		alert("키워드를 작성해 주세요.");
		document.getElementById("autoComplete").focus();
		event.preventDefault();
	} 
	
	genInputValue(); // 유효성 검사 후 입력값 종합
}

// 입력값(체크 없으면 null, 있으면 값)
function genInputValue(){
	var valueByAir 
	var valueByGen = $('#genrechoose').val();
	var valueByPe = $('#moviePeTe').val();
	var valueByKey = $('#autoComplete').val();
	
	// 현재 상영작
	if($("input:checkbox[id='chkAir']").prop("checked")){
		valueByAir = "true";
	} else{valueByAir = null;}
	
	// 장르(genreId로 나옴)
	if($("input:checkbox[id='chkGenre']").prop("checked")){
		valueByGen = $('#genrechoose').val();
	}
	
	// 영화인
	if(moviePeTe.value==""){
		valueByPe = null;
	}
	
	// 키워드
	if(autoComplete.value==""){
		valueByKey = null;
	}
	
	alert('상영중=' + valueByAir + "\n" + '장르=' + valueByGen + "\n" + 
			'영화인=' + valueByPe + "\n" + '영화 키워드=' + valueByKey);
	event.preventDefault();
}
