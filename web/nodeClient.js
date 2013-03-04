//import
var http = require('http');//kobler til web tjner bibliotek
var mdns = require('mdns'); // kobler til mdns bibliotek
var url = require('url'); //kobler til url parsing
var soap = require('soap');  //kobler til SOAP bibliotek
var fs = require('fs'); //kobler til IO bibliotek



var browser = mdns.createBrowser(mdns.tcp('http'));//lager mdns browser
var mDNSUrl; //variabel for WSDL url
//bestemmer hendelser
browser.on('serviceUp', function(service) {
    if (service.name === 'WebService') {//navn til min mDNS service på tjener side
        console.log('service up: ', service.name + ' ' + service.addresses[0] + ':' + service.port + service.txtRecord.ServiceURL);
        //url til REST service
        var webResUrl = service.addresses[0] + ':' + service.port + service.txtRecord.resturl;

        //fungerer ikke, feil i WSDL parser
//        WSDLUrl = "http://"+service.addresses[0] + ':' + service.port + service.txtRecord.WSDLURL;
//        console.log(WSDLUrl);  
//        soap.createClient(WSDLUrl, function(err, client) {
//            //console.log(client);
//        });
//bruker REST service
    }
});

browser.on('serviceDown', function(service) {
    console.log("service down: ", service);
});
browser.start(); //setter mdns browser i gang
var services = mdns.browseThemAll();
var server = http.createServer(function(request, response) {
    response.writeHead(200, {"Content-Type": "text/html"});
    var indexPage = fs.readFileSync('form.html').toString().split("\n");
    response.end(indexPage);

});
server.listen(8000);
console.log("Server running at http://127.0.0.1:8000/");