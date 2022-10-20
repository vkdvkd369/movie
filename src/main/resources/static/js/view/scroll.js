window.addEventListener("scroll", infiniteScroll);

let isUpdateList = true;    

function infiniteScroll(){
    const currentScroll = window.scrollY;
    const windowHeight = window.innerHeight;
    const bodyHeight = document.body.clientHeight;
    const paddingBottom = 200;
    if(currentScroll + windowHeight + paddingBottom >= bodyHeight){
        if(isUpdateList){
            isUpdateList = false;
            
            -- after fetch API --
            isUpdateList = true;
        }
    }
}