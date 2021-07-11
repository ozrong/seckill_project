var page = require("webpage").create();
var fs = require("fs");
page.open("http://10.16.108.124/resources/ozr/public/getItem.html?id=1",function(status){
           console.log("status=="+status);
           var isInit = "0";
           setInterval(function(){
           	if(isInit !="1"){
           		page.evaluate(function(){
           			initView();
           		});
           		isInit = page.evaluate(function(){
                        return hasInit();
           		});
           	}else{
           		fs.write("getIIIItme.html",page.content,"W");
                phantom.exit();
           	}
              },1000);
 });
           