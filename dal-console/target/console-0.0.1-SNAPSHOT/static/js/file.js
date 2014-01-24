
//zTree异步加载,配置信息
var setting = {
    async: {
        enable: true,
        url: getUrl,
        type: "get"
    },
    check: {
        enable: false,
        nocheckInherit: false,
        chkboxType: {
            "Y": "s",
            "N": "s"
        },
        chkDisabledInherit: false
    },
    data: {
        simpleData: {
            enable: true
        }
    },
    callback: {
        onAsyncSuccess: onAsyncSuccess,
        onAsyncError: onAsyncError,
        onClick: zTreeOnClick
    },
    view: {
        fontCss: getFontCss
    }
};

function getFontCss(treeId, treeNode) {
    return ( !! treeNode.highlight) ? {
        color: "#A60000",
        "font-weight": "bold"
    } : {
        color: "#333",
        "font-weight": "normal"
    };
}

function getUrl(treeId, treeNode) {
	var name = treeNode.name;
	var type = treeNode.type;
	if(type == "all"){
		name = "all";
	}
    var param = "id=" + treeNode.id + "&type=" + type + "&name=" + name;
    return "/rest/daogen/file?" + param;
}

function onAsyncSuccess(event, treeId, treeNode, msg) {
    if (!msg || msg.length == 0) {
        return;
    }
    var zTree = $.fn.zTree.getZTreeObj("project_tree");
    zTree.updateNode(treeNode);
    zTree.selectNode(treeNode.children[0]);
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    var zTree = $.fn.zTree.getZTreeObj("project_tree");
    treeNode.icon = "";
    zTree.updateNode(treeNode);
}

function zTreeOnClick(event, treeId, treeNode) {
    if (!treeNode.isParent) {
        $.get("/rest/daogen/file/content?id=" + treeNode.id + "&name=" + treeNode.name, function (data) {
            //var real_data = JSON.parse(data);
            ace.edit("code_editor").setValue(data);
            if (treeNode.name.match(/cs$/)) {
                ace.edit("code_editor").getSession().setMode("ace/mode/csharp");
            } else if (treeNode.name.match(/java$/)) {
                ace.edit("code_editor").getSession().setMode("ace/mode/java");
            }
        });
    }
}


var zNodes = [{
    name: "所有项目",
    title: "所有项目",
    id: "-1",
    type: "all",
    isParent: true,
    nocheck: true
}];

jQuery(document).ready(function () {

    var editor = ace.edit("code_editor");
            editor.setTheme("ace/theme/monokai");
            editor.getSession().setMode("ace/mode/csharp");

	$('#widget').height($( document ).height()-80).split({orientation:'vertical',limit:10, position:270});

    $.fn.zTree.init($("#project_tree"), setting, zNodes);

    $( window ).resize(function() {
  $('#widget').height($( document ).height()-80);
});

});