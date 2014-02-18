jQuery(document).ready(function () {

    $('#main_layout').height($(document).height() - 50);

    $('#main_layout').w2layout({
        name: 'main_layout',
        panels: [{
            type: 'left',
            size: 270,
            resizable: true,
            style: 'border-right: 1px solid silver;'
        }, {
            type: 'main',
            style: 'background-color: white;'
        }]
    });

    $(window).resize(function () {
        $('#main_layout').height($(document).height() - 50);
    });

    //Begin tree side bar
    w2ui['main_layout'].content('left', $().w2sidebar({
        name: 'sidebar',
        img: null,
        topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a href="javascript:;" onclick="reloadProjects();"><i class="fa fa-refresh"></i>刷新</a></div>',
        nodes: [{
            id: 'all_projects',
            text: '所有项目',
            icon: 'fa fa-folder-o',
            // plus: true,
            group: true,
        }],
    }));

    w2ui['main_layout'].content('main', '<div id="code_editor" class="code_edit" style="height:100%"></div>');
    //End tree side bar

    var editor = ace.edit("code_editor");
    editor.setTheme("ace/theme/monokai");
    editor.getSession().setMode("ace/mode/csharp");

    reloadProjects();

});


var reloadProjects = function () {
    cblock($("body"));
    var currentElement = w2ui['sidebar'];
    var nodes = [];
    $.each(currentElement.nodes[0].nodes, function (index, value) {
        nodes.push(value.id);
    });
    currentElement.remove.apply(currentElement, nodes);
    $.get("/rest/project", function (data) {
        var new_nodes = [];
        //data = JSON.parse(data);
        $.each(data, function (index, value) {
            new_nodes.push({
                id: value.id,
                text: value.name,
                namespace: value.namespace,
                icon: 'fa fa-folder',
                group1: true,
                plus: true,
                //expanded: true,
                onClick: function (event) {
                    var id = event.target;
                },
                plus: true,
                onExpand: function (event) {
                    // var centerY = null;
                    var currentElement = event.object;

                    if (undefined == currentElement.nodes || currentElement.nodes.length == 0) {
                        cblock($("#main_layout"));
                        $.get("/rest/file?id=" + currentElement.id, function (data) {
                            var allNodes = [];
                            $.each(data, function (index, value) {
                                allNodes.push({
                                    id: sprintf("%s_file_%s", currentElement.id, allNodes.length),
                                    real_id: value.id,
                                    text: value.name,
                                    icon: 'fa fa-file',
                                    type: "file",
                                    onClick: function (event) {
                                        if (event.object.type == "file") {
                                            $.get("/rest/file/content?id=" + event.object.real_id + "&name=" + event.object.text, function (data) {
                                                //var real_data = JSON.parse(data);
                                                ace.edit("code_editor").setValue(data);
                                                if (event.object.text.match(/cs$/)) {
                                                    ace.edit("code_editor").getSession().setMode("ace/mode/csharp");
                                                } else if (event.object.text.match(/java$/)) {
                                                    ace.edit("code_editor").getSession().setMode("ace/mode/java");
                                                }
                                            });
                                        }
                                    }
                                });
                            });
                            if(allNodes.length > 0)
                                currentElement.icon = "fa fa-folder-open-o";
                            w2ui['sidebar'].add(currentElement, allNodes);
                            $("#main_layout").unblock();
                        });
                    }
                    w2ui['sidebar'].refresh();
                },
                onCollapse: function (event) {
                    //console.log(this == w2ui["sidebar"]);
                    event.object.icon = "fa fa-folder-o";
                    w2ui['sidebar'].refresh();
                },
            });
        });
        currentElement.add('all_projects', new_nodes);
        currentElement.nodes[0].expanded = true;
        currentElement.refresh();
        $("body").unblock();
    });
};