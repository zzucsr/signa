var downLoadKey = $("#downLoadKey").val();
var agentId = $("#agentId").val();
var channelId =  $("#channelId").val();
if (downLoadKey != null&& downLoadKey != "") {
    //openinstall初始化时将与openinstall服务器交互，应尽可能早的调用
    /*web页面向app传递的json数据(json string/js Object)，应用被拉起或是首次安装时，通过相应的android/ios api可以获取此数据*/
    var data = {channelId: channelId, agentId: agentId};//openinstall.js中提供的工具函数，解析url中的所有查询参数
    new OpenInstall({
        /*appKey必选参数，openinstall平台为每个应用分配的ID*/
        appKey : downLoadKey,
        /*可选参数，自定义android平台的apk下载文件名；个别andriod浏览器下载时，中文文件名显示乱码，请慎用中文文件名！*/
        //apkFileName : 'com.fm.openinstalldemo-v2.2.0.apk',
        /*可选参数，是否优先考虑拉起app，以牺牲下载体验为代价*/
        //preferWakeup:true,
        /*自定义遮罩的html*/
        mask:function(){
            return '<div id=\'openinstall_shadow\' style=\'position:fixed;left:0;top:0;background:rgba(37, 38, 37, 0.5);filter:alpha(opacity=50);width:100%;height:100%;z-index:10000;\'>\n' +
                '        <div style="float: right"><img style="height: 4rem;" src="img/base/point.png"></div>\n' +
                '    </div>';
        },
        /*openinstall初始化完成的回调函数，可选*/
        onready : function() {
            var m = this, button = document.getElementById("downloadButton");
            button.style.visibility = "visible";
            /*在app已安装的情况尝试拉起app*/
            m.schemeWakeup();
            /*用户点击某个按钮时(假定按钮id为downloadButton)，安装app*/
            button.onclick = function() {
                m.wakeupOrInstall();
                return false;
            }
        }
    }, data);
}
function downloadGame() {
    var isIos = $("#isIos").val();
    var downLoadUrl = $("#downLoadUrl").val();
    var uuid = getUUID();
    copyToClipboard(uuid);
    //生成记录
    $.ajax({
        url:"/bindIp4DownloadPlayer?agentId="+ agentId +"&channelId="+channelId+"&uuid="+ uuid,
        type: "get",
        dataType: "json",
        success:function(data) {
        }
    });
    if (downLoadKey != null && downLoadKey != "") {
        document.getElementById("downloadButton").click();
    } else {
        window.location.href = downLoadUrl;
    }
}
function getUUID() {
    var uuidPre = $("#uuidPre").val();
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) s[i] = hexDigits.substr(Math.floor(16 * Math.random()), 1);
    s[14] = "4";
    s[19] = hexDigits.substr(3 & s[19] | 8, 1);
    s[8] = s[13] = s[18] = s[23] = "-";
    return uuidPre+(s.join(""));
}
function copyToClipboard(str) {
    var isIos = $("#isIos").val();
    if (!isIos || isIos == "false") {
        var textArea = document.getElementById("clipBoard");
        if (!textArea) {
            textArea = document.createElement("textarea");
            textArea.id = "clipBoard";
            textArea.textContent = str;
            document.body.appendChild(textArea);
        }
        textArea.select();
        try {
            document.execCommand("copy");
            document.body.removeChild(textArea);
        } catch (err) {}
    } else {
        var el = document.createElement("input");
        el.value = str;
        el.style.opacity = "0";
        document.body.appendChild(el);
        var editable = el.contentEditable;
        var readOnly = el.readOnly;
        el.contentEditable = "true";
        el.readOnly = false;
        var range = document.createRange();
        range.selectNodeContents(el);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
        el.setSelectionRange(0, 999999);
        el.contentEditable = editable;
        el.readOnly = readOnly;
        document.execCommand("copy");
        el.blur();
    }
}