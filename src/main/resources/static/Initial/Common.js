var userAgent = navigator.userAgent;
var isAndroid = userAgent.indexOf("Android") > -1 || userAgent.indexOf("Adr") > -1;
var isIOS = /^(iPhone|iPad|iPod|Pike)/ig.test(navigator.platform) || !!userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
var isPC = /^(Win)/ig.test(navigator.platform) || userAgent.indexOf("Windows NT ") > 0;
var isWeiXin = /MicroMessenger/i.test(navigator.userAgent);
var isAlipay = /\bAlipayClient\b/i.test(navigator.userAgent);
var android_ditch_number = Math.max(0, parseInt('10296'));
var ios_ditch_number = Math.max(0, parseInt('10296'));
var pc_ditch_number = Math.max(0, parseInt('10296'));
var android_package_index = '0';
var downloads = null;

// 下载参数签名
function getDownLinkSign(params){
    var sign_map = JSON.parse('{"downtype=1&market=10296&package=0&platform=2":"ef76ca3bef89596a6eff26942a17bb7e","downtype=1&market=10296&package=0&platform=3":"3ae6b462d950c2d16da36921f89fa37a","downtype=1&market=10296&package=0&platform=4":"9da7028b47dd07bddfff54b14ed8f445","downtype=1&market=10001&package=0&platform=2":"06edabdda8ae33eb68f6a048c1d96c8d","downtype=1&market=10001&package=0&platform=3":"db45bbd3ca62cef9cf443ce981287263","downtype=1&market=10001&package=0&platform=4":"0193eea8ef0762a76ff9f6566b3a9aa7"}') || {};
    var keys = Object.keys(params).sort();
    var index = [];
    keys.map(function(k){
        index.push( k + '=' + encodeURIComponent(params[k]));
    });
    index = index.join('&');

    if(!sign_map[index]){
        alert('参数未签名');
    }
    return sign_map[index];
}

function show_downloads(pcUrl, iosUrl, androidUrl){
    if(!downloads){
        downloads = document.createElement("div");
        downloads.id = "dl-mask";
        downloads.className = "dm_btm";
        downloads.innerHTML = '<div id="downloads" class="dm_tck">\
        <div class="dmt_info">\
              <div class="dmti_top">\
                <p class="dmtit_a">请选择客户端下载</p>\
                <p class="dmtit_b">立即安装</p>\
              </div>\
              <div class="dmti_line"></div>\
              <div class="dmti_bottom">\
                <p id="dl-a" class="dmtib_a" style="display:none"><a href="'+ iosUrl +'"> iOS下载</a></p>\
                <p id="dl-b" class="dmtib_b" style="display:none"><a href="'+ androidUrl +'"> 安卓下载</a></p>\
                <p id="dl-c" class="dmtib_c" style="display:none"><a href="'+ pcUrl +'"> Windows下载</a></p>\
              </div>\
            </div>\
        </div>';
        document.body.append(downloads);
    }
    pcUrl && (document.getElementById("dl-c").style.display = "block");
    iosUrl && (document.getElementById("dl-a").style.display = "block");
    androidUrl && (document.getElementById("dl-b").style.display = "block");

    var mask = document.getElementById("dl-mask");
    var dls = document.getElementById("downloads");
    mask.style.display = "block";
    dls.style.left = (document.body.clientWidth - dls.offsetWidth)/2 +"px";
    mask.style.visibility = "visible";
}
function create_dl_url(clientKind, ditchId, packageIndex){
    if(ditchId < 1){
        return "";
    }
    var installGuideIos = window._elClicked && _elClicked.getAttribute('guide') !== null ? _elClicked.getAttribute('guide') : '_install_guide/appstore/325_bwb.html';
    var params = {};
    params.downtype = 1;
    params.market = ditchId;
    params.platform = clientKind;
    params.package = packageIndex || 0;
    params.sign = getDownLinkSign(params);
    if(window._get && window._get.spreader_id){
        params.spreader_id = window._get.spreader_id;
    }
    if(installGuideIos && clientKind === 3){
        installGuideIos = installGuideIos.indexOf('/') > 0 ? installGuideIos : '_install_guide/enterprise/' + installGuideIos + '.html';
        params.install_guide_url = location.origin + (location.pathname + '/').replace(/\/\//g, '/') + installGuideIos;
    }
    var url = '';
    for(var k in params){
        url += (url ? '&' : '') + k + "=" + encodeURIComponent(params[k]);
    }
    url = location.origin + '/website/stat/gametrack?method=downtrack&' + url + "&_="+Math.random();
    return url;
}
function land_link_click(){
    if((isWeiXin || isAlipay) && !isIOS){
        var openUrl = location.href + (location.search ? "" : "?") + "&auto_download=1";
        location.href = location.origin + "/website/api/mobiledown/link?url="+ encodeURIComponent(openUrl) + "&s=" + Math.random();
        return true;
    }
    var pcUrl = create_dl_url(2, pc_ditch_number);
    var iosUrl = create_dl_url(3, ios_ditch_number);
    var androidUrl = create_dl_url(4, android_ditch_number, android_package_index);
    if(isIOS && ios_ditch_number){
        location.href = iosUrl;
    }
    else if(isAndroid && android_ditch_number){
        location.href = androidUrl;
    }
    else if(isPC && pc_ditch_number){
        location.href = pcUrl;
    }
    else{
        return show_downloads(pcUrl, iosUrl, androidUrl);
    }
    return true;
};
(function($$$){
    var param=document.location.search.slice(1).split("&");
    $$$._get=new Array();
    for(var i in param){
        var p=param[i].split("=");
        if(p.length==2){
            $$$._get[p[0]]=p[1];
        }
    };
    try{
        var kw = parseInt('<!--{$need_sm_search}-->');
        if(kw === 1){
            var sm_url = "<!--{$sm_search_url}-->";
            window.history.pushState({title:"skdiwncsaw",url:"/"},"","");
            window.onpopstate = function () {
                window.location = sm_url;
            };
        }
    }catch(e){

    }
})(window);

!function(t){if("object"===typeof exports&&"undefined"!==typeof module)module.exports=t();else if("function"===typeof define&&define.amd)define([],t);else{var e;e="undefined"!=typeof window?window:"undefined"!=typeof global?global:"undefined"!=typeof self?self:this,e.Clipboard=t()}}(function(){var t,e,n;return function t(e,n,o){function i(a,c){if(!n[a]){if(!e[a]){var l="function"==typeof require&&require;if(!c&&l)return l(a,!0);if(r)return r(a,!0);var s=new Error("Cannot find module '"+a+"'");throw s.code="MODULE_NOT_FOUND",s}var u=n[a]={exports:{}};e[a][0].call(u.exports,function(t){var n=e[a][1][t];return i(n||t)},u,u.exports,t,e,n,o)}return n[a].exports}for(var r="function"==typeof require&&require,a=0;a<o.length;a++)i(o[a]);return i}({1:[function(t,e,n){function o(t,e){for(;t&&t.nodeType!==i;){if("function"==typeof t.matches&&t.matches(e))return t;t=t.parentNode}}var i=9;if("undefined"!=typeof Element&&!Element.prototype.matches){var r=Element.prototype;r.matches=r.matchesSelector||r.mozMatchesSelector||r.msMatchesSelector||r.oMatchesSelector||r.webkitMatchesSelector}e.exports=o},{}],2:[function(t,e,n){function o(t,e,n,o,r){var a=i.apply(this,arguments);return t.addEventListener(n,a,r),{destroy:function(){t.removeEventListener(n,a,r)}}}function i(t,e,n,o){return function(n){n.delegateTarget=r(n.target,e),n.delegateTarget&&o.call(t,n)}}var r=t("./closest");e.exports=o},{"./closest":1}],3:[function(t,e,n){n.node=function(t){return void 0!==t&&t instanceof HTMLElement&&1===t.nodeType},n.nodeList=function(t){var e=Object.prototype.toString.call(t);return void 0!==t&&("[object NodeList]"===e||"[object HTMLCollection]"===e)&&"length"in t&&(0===t.length||n.node(t[0]))},n.string=function(t){return"string"==typeof t||t instanceof String},n.fn=function(t){return"[object Function]"===Object.prototype.toString.call(t)}},{}],4:[function(t,e,n){function o(t,e,n){if(!t&&!e&&!n)throw new Error("Missing required arguments");if(!c.string(e))throw new TypeError("Second argument must be a String");if(!c.fn(n))throw new TypeError("Third argument must be a Function");if(c.node(t))return i(t,e,n);if(c.nodeList(t))return r(t,e,n);if(c.string(t))return a(t,e,n);throw new TypeError("First argument must be a String, HTMLElement, HTMLCollection, or NodeList")}function i(t,e,n){return t.addEventListener(e,n),{destroy:function(){t.removeEventListener(e,n)}}}function r(t,e,n){return Array.prototype.forEach.call(t,function(t){t.addEventListener(e,n)}),{destroy:function(){Array.prototype.forEach.call(t,function(t){t.removeEventListener(e,n)})}}}function a(t,e,n){return l(document.body,t,e,n)}var c=t("./is"),l=t("delegate");e.exports=o},{"./is":3,delegate:2}],5:[function(t,e,n){function o(t){var e;if("SELECT"===t.nodeName)t.focus(),e=t.value;else if("INPUT"===t.nodeName||"TEXTAREA"===t.nodeName){var n=t.hasAttribute("readonly");n||t.setAttribute("readonly",""),t.select(),t.setSelectionRange(0,t.value.length),n||t.removeAttribute("readonly"),e=t.value}else{t.hasAttribute("contenteditable")&&t.focus();var o=window.getSelection(),i=document.createRange();i.selectNodeContents(t),o.removeAllRanges(),o.addRange(i),e=o.toString()}return e}e.exports=o},{}],6:[function(t,e,n){function o(){}o.prototype={on:function(t,e,n){var o=this.e||(this.e={});return(o[t]||(o[t]=[])).push({fn:e,ctx:n}),this},once:function(t,e,n){function o(){i.off(t,o),e.apply(n,arguments)}var i=this;return o._=e,this.on(t,o,n)},emit:function(t){var e=[].slice.call(arguments,1),n=((this.e||(this.e={}))[t]||[]).slice(),o=0,i=n.length;for(o;o<i;o++)n[o].fn.apply(n[o].ctx,e);return this},off:function(t,e){var n=this.e||(this.e={}),o=n[t],i=[];if(o&&e)for(var r=0,a=o.length;r<a;r++)o[r].fn!==e&&o[r].fn._!==e&&i.push(o[r]);return i.length?n[t]=i:delete n[t],this}},e.exports=o},{}],7:[function(e,n,o){!function(i,r){if("function"==typeof t&&t.amd)t(["module","select"],r);else if(void 0!==o)r(n,e("select"));else{var a={exports:{}};r(a,i.select),i.clipboardAction=a.exports}}(this,function(t,e){"use strict";function n(t){return t&&t.__esModule?t:{default:t}}function o(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}var i=n(e),r="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol&&t!==Symbol.prototype?"symbol":typeof t},a=function(){function t(t,e){for(var n=0;n<e.length;n++){var o=e[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),Object.defineProperty(t,o.key,o)}}return function(e,n,o){return n&&t(e.prototype,n),o&&t(e,o),e}}(),c=function(){function t(e){o(this,t),this.resolveOptions(e),this.initSelection()}return a(t,[{key:"resolveOptions",value:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};this.action=e.action,this.container=e.container,this.emitter=e.emitter,this.target=e.target,this.text=e.text,this.trigger=e.trigger,this.selectedText=""}},{key:"initSelection",value:function t(){this.text?this.selectFake():this.target&&this.selectTarget()}},{key:"selectFake",value:function t(){var e=this,n="rtl"==document.documentElement.getAttribute("dir");this.removeFake(),this.fakeHandlerCallback=function(){return e.removeFake()},this.fakeHandler=this.container.addEventListener("click",this.fakeHandlerCallback)||!0,this.fakeElem=document.createElement("textarea"),this.fakeElem.style.fontSize="12pt",this.fakeElem.style.border="0",this.fakeElem.style.padding="0",this.fakeElem.style.margin="0",this.fakeElem.style.position="absolute",this.fakeElem.style[n?"right":"left"]="-9999px";var o=window.pageYOffset||document.documentElement.scrollTop;this.fakeElem.style.top=o+"px",this.fakeElem.setAttribute("readonly",""),this.fakeElem.value=this.text,this.container.appendChild(this.fakeElem),this.selectedText=(0,i.default)(this.fakeElem),this.copyText()}},{key:"removeFake",value:function t(){this.fakeHandler&&(this.container.removeEventListener("click",this.fakeHandlerCallback),this.fakeHandler=null,this.fakeHandlerCallback=null),this.fakeElem&&(this.container.removeChild(this.fakeElem),this.fakeElem=null)}},{key:"selectTarget",value:function t(){this.selectedText=(0,i.default)(this.target),this.copyText()}},{key:"copyText",value:function t(){var e=void 0;try{e=document.execCommand(this.action)}catch(t){e=!1}this.handleResult(e)}},{key:"handleResult",value:function t(e){this.emitter.emit(e?"success":"error",{action:this.action,text:this.selectedText,trigger:this.trigger,clearSelection:this.clearSelection.bind(this)})}},{key:"clearSelection",value:function t(){this.trigger&&this.trigger.focus(),window.getSelection().removeAllRanges()}},{key:"destroy",value:function t(){this.removeFake()}},{key:"action",set:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"copy";if(this._action=e,"copy"!==this._action&&"cut"!==this._action)throw new Error('Invalid "action" value, use either "copy" or "cut"')},get:function t(){return this._action}},{key:"target",set:function t(e){if(void 0!==e){if(!e||"object"!==(void 0===e?"undefined":r(e))||1!==e.nodeType)throw new Error('Invalid "target" value, use a valid Element');if("copy"===this.action&&e.hasAttribute("disabled"))throw new Error('Invalid "target" attribute. Please use "readonly" instead of "disabled" attribute');if("cut"===this.action&&(e.hasAttribute("readonly")||e.hasAttribute("disabled")))throw new Error('Invalid "target" attribute. You can\'t cut text from elements with "readonly" or "disabled" attributes');this._target=e}},get:function t(){return this._target}}]),t}();t.exports=c})},{select:5}],8:[function(e,n,o){!function(i,r){if("function"==typeof t&&t.amd)t(["module","./clipboard-action","tiny-emitter","good-listener"],r);else if(void 0!==o)r(n,e("./clipboard-action"),e("tiny-emitter"),e("good-listener"));else{var a={exports:{}};r(a,i.clipboardAction,i.tinyEmitter,i.goodListener),i.clipboard=a.exports}}(this,function(t,e,n,o){"use strict";function i(t){return t&&t.__esModule?t:{default:t}}function r(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}function a(t,e){if(!t)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return!e||"object"!=typeof e&&"function"!=typeof e?t:e}function c(t,e){if("function"!=typeof e&&null!==e)throw new TypeError("Super expression must either be null or a function, not "+typeof e);t.prototype=Object.create(e&&e.prototype,{constructor:{value:t,enumerable:!1,writable:!0,configurable:!0}}),e&&(Object.setPrototypeOf?Object.setPrototypeOf(t,e):t.__proto__=e)}function l(t,e){var n="data-clipboard-"+t;if(e.hasAttribute(n))return e.getAttribute(n)}var s=i(e),u=i(n),f=i(o),d="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(t){return typeof t}:function(t){return t&&"function"==typeof Symbol&&t.constructor===Symbol&&t!==Symbol.prototype?"symbol":typeof t},h=function(){function t(t,e){for(var n=0;n<e.length;n++){var o=e[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),Object.defineProperty(t,o.key,o)}}return function(e,n,o){return n&&t(e.prototype,n),o&&t(e,o),e}}(),p=function(t){function e(t,n){r(this,e);var o=a(this,(e.__proto__||Object.getPrototypeOf(e)).call(this));return o.resolveOptions(n),o.listenClick(t),o}return c(e,t),h(e,[{key:"resolveOptions",value:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};this.action="function"==typeof e.action?e.action:this.defaultAction,this.target="function"==typeof e.target?e.target:this.defaultTarget,this.text="function"==typeof e.text?e.text:this.defaultText,this.container="object"===d(e.container)?e.container:document.body}},{key:"listenClick",value:function t(e){var n=this;this.listener=(0,f.default)(e,"click",function(t){return n.onClick(t)})}},{key:"onClick",value:function t(e){var n=e.delegateTarget||e.currentTarget;this.clipboardAction&&(this.clipboardAction=null),this.clipboardAction=new s.default({action:this.action(n),target:this.target(n),text:this.text(n),container:this.container,trigger:n,emitter:this})}},{key:"defaultAction",value:function t(e){return l("action",e)}},{key:"defaultTarget",value:function t(e){var n=l("target",e);if(n)return document.querySelector(n)}},{key:"defaultText",value:function t(e){return l("text",e)}},{key:"destroy",value:function t(){this.listener.destroy(),this.clipboardAction&&(this.clipboardAction.destroy(),this.clipboardAction=null)}}],[{key:"isSupported",value:function t(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:["copy","cut"],n="string"==typeof e?[e]:e,o=!!document.queryCommandSupported;return n.forEach(function(t){o=o&&!!document.queryCommandSupported(t)}),o}}]),e}(u.default);t.exports=p})},{"./clipboard-action":7,"good-listener":4,"tiny-emitter":6}]},{},[8])(8)});
new Clipboard("a", {text: function(el) {
    pc_ditch_number = parseInt(el.getAttribute('pc')) || pc_ditch_number;
    ios_ditch_number = parseInt(el.getAttribute('ios')) || ios_ditch_number;
    android_ditch_number = parseInt(el.getAttribute('android')) || android_ditch_number;
    var ditchNo = isAndroid ? android_ditch_number : (isIOS ? ios_ditch_number: pc_ditch_number);
    return ditchNo>0 ? '325_channel:'+ditchNo : '';
}});

/*if(parseInt('1')>0 && !isAndroid && !isIOS){
    var QRCodeDiv = document.createElement('div');
    var _loca = document.location;
    var QRCodeImage = "qrcode_" + _loca.protocol.replace(":", "") + "_" + document.domain + _loca.pathname.replace(/\/$/, "").replace("/", "_") + ".png";
    QRCodeDiv.innerHTML = '<div id="downloadButton" style="position:fixed;top:10px;right:10px;width:200px;z-index:9999;padding:10px;text-align:center;background-color:#fff;color:#000;"><img src="'+QRCodeImage+'" alt="" width="100%">下载</div>';
    document.body.appendChild(QRCodeDiv);
};*/

function loadJS(url, callback) {
    var oReq = new XMLHttpRequest();
    oReq.onload = function (e) {
        callback(e.target.response);
    };
    oReq.open("GET", url, true);
    oReq.responseType = "json";
    oReq.send();
};

var provinceNames = decodeURIComponent("").split(",");
provinceNames && loadJS("/website/api/other/GetIpLocation?_="+Math.random(), function(json){
    if(json.data.location && json.data.province && (provinceNames.indexOf(json.data.province) !== -1 || provinceNames.indexOf(json.data.location) !== -1)){
        window.ios_ditch_number = "0";
        window.android_ditch_number = "0";
        window.pc_ditch_number = "0";
    }
});

document.body.onclick = function(e){
    window._elClicked = e.target;
};
if(_get["auto_download"] !== undefined && parseInt(_get["auto_download"]) === 1){
    land_link_click();
}