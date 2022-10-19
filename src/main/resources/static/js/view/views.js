let view_mode_links = document.querySelectorAll(".view_mode ul li");
let sel_links = document.querySelectorAll(".sel ul li"); 

let viewWraps = document.querySelectorAll(".view_wrap");
let searchViewWrap = document.querySelector("#searched > .view_wrap");
let selViewWrap = document.querySelector("#selected > .view_wrap");
let all_items = document.querySelectorAll(".view_item");
let searched_tab = document.getElementById("searched");
let selected_tab = document.getElementById("selected");
let selected_num = document.getElementById("selected_num");

sel = false; // false = search, true = select
view_mode =false; // false = list view, true = grid view

function changeViewMode(view_mode){
    
    let contents = document.querySelectorAll(".content");
    if(view_mode){
        viewWraps.forEach(function(viewWrap){
            viewWrap.classList.remove("list-view");
            viewWrap.classList.add("grid-view");
            contents.forEach(function(content){
                content.style.display = "none";
            });
        })
    }
    else{
        viewWraps.forEach(function(viewWrap){
            viewWrap.classList.remove("grid-view");
            viewWrap.classList.add("list-view");
            contents.forEach(function(content){
                content.style.display = "block";
            });
        })
    }
}

view_mode_links.forEach(function(link){
    link.addEventListener("click", function(){
        view_mode_links.forEach(function(li){
            li.classList.remove("active");
        })
        link.classList.add("active");

        if(link.classList.contains("li-list")){
            view_mode = false;
            changeViewMode(view_mode);
        }else{
            view_mode = true;
            changeViewMode(view_mode);
        }

    });
});

function changeTab(sel){

    if(sel){
        searched_tab.style.display = "none";
        selected_tab.style.display = "block";
    }
    else{
        searched_tab.style.display = "block";
        selected_tab.style.display = "none";
    }
    
}

sel_links.forEach(function(link){
    link.addEventListener("click", function(){
        sel_links.forEach(function(li){
            li.classList.remove("active");
        })
        link.classList.add("active");

        if(link.classList.contains("li-search")){
            sel = false;
            changeTab(sel);
            document.querySelectorAll(".view_item.selected").forEach(function(item){
            searchViewWrap.append(item)});
        }else{
            sel = true;
            changeTab(sel);
            document.querySelectorAll(".view_item.selected").forEach(function(item){
            selViewWrap.append(item)});
        }
    });
});

all_items.forEach(function(item){
    item.addEventListener("click", function(){
        item.classList.toggle("selected");
        if(sel){
            searchViewWrap.append(item);
        }
        let num = document.querySelectorAll(".view_item.selected").length;

        if(num == 0){
            selected_num.innerText = "빔";
        }else{
            selected_num.innerText = num;
        }
    })});