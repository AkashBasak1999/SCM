console.log("This is log file")
//using java script and jquery($) together
const toggleSidebar=()=>{
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display","none")
		$(".content").css("margin-left","5%")				
	}else{
		$(".sidebar").css("display","block")
		$(".content").css("margin-left","10%")		
	}
};

let mode=false;
let dark=document.querySelector("#dark")
let light=document.querySelector("#light")
let sidemode=document.querySelector("#sidemode")

dark.addEventListener('click',()=>{
	if(mode===false){
		sidemode.classList.remove("sidebar")
		sidemode.classList.add("sidebardark")			
		mode=true;
	}
})
light.addEventListener('click',()=>{
	if(mode===true){
		sidemode.classList.remove("sidebardark")
		sidemode.classList.add("sidebar")			
		mode=false;
	}
})