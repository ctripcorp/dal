$(function () {
    $('#main_layout').height($(document).height() - 50);
    $('#main_layout').w2layout({
        name: 'main_layout',
        panels: [{
            type: 'left',
            size: 271,
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

    // Begin tree side bar
    w2ui['main_layout'].content('left', $().w2sidebar({
        name: 'sidebar',
        img: null,
        topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a href="javascript:;" onclick="reloadProjects();"><i class="glyphicon glyphicon-refresh"></i>刷新</a></div>',
        nodes: [{
            id: 'all_projects',
            text: '所有项目',
            icon: 'glyphicon glyphicon-folder-open',
            // plus: true,
            group: true
        }],
        menu: [{
            id: "download",
            text: '下载Zip包',
            icon: 'glyphicon glyphicon-play'
        }],
        onMenuClick: function (event) {
            switch (event.menuItem.id) {
                case "download":
                    cblock($("body"));
                    var obj = w2ui['sidebar'].get(event.target);
                    var url = "/rest/file/download?id=" + obj.project_id + "&name=" + obj.relativeName;
                    if (obj.project_id == undefined) {
                        url = "/rest/file/download?id=" + obj.id;
                    }

                    $.get(url, function (data) {
                        $("body").unblock();
                        window.location.href = data;
                    });
                    break;
            }
        }
    }));

    w2ui['main_layout'].content('main', '<div id="code_editor" class="code_edit" style="height:100%"></div>');
    // End tree side bar

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
        // data = JSON.parse(data);
        $.each(data, function (index, value) {
            new_nodes.push({
                id: value.id,
                text: value.name,
                namespace: value.namespace,
                icon: 'glyphicon glyphicon-folder-close',
                group1: true,
                plus: true,
                // expanded: true,
                onClick: function (event) {
                    var id = event.target;
                },
                onExpand: function (event) {
                    expandNode(event.object, value.id, true);
                },
                onCollapse: function (event) {
                    // console.log(this
                    // ==
                    // w2ui["sidebar"]);
                    event.object.icon = "glyphicon glyphicon-folder-close";
                    w2ui['sidebar'].refresh();
                }
            });
        });
        currentElement.add('all_projects', new_nodes);
        currentElement.nodes[0].expanded = true;
        currentElement.refresh();
        $("body").unblock();

        var currentUrl = window.location.href;

        if (currentUrl.indexOf("#") != -1) {
            var currentFile = currentUrl.substring(currentUrl.indexOf("#") + 1);
            var splitedFile = currentFile.split("&");
            var project_id, name;
            $.each(splitedFile, function (index, value) {
                if (value.indexOf("=") != -1) {
                    var lastStep = value.split("=");
                    if (lastStep.length == 2) {
                        if (lastStep[0] == "id") {
                            project_id = lastStep[1];
                        } else if (lastStep[0] == "name") {
                            name = lastStep[1];
                        }
                    }
                }
            });
            if (project_id != undefined && name != undefined) {
                // expandNode(w2ui['sidebar'].get(project_id),
                // project_id, true);
                w2ui['sidebar'].expand(project_id);

                $.get("/rest/file/content?" + currentFile, function (data) {
                    // var real_data =
                    // JSON.parse(data);
                    ace.edit("code_editor").setValue(data);
                    if (name.match(/cs$/)) {
                        ace.edit("code_editor").getSession().setMode("ace/mode/csharp");
                    } else if (name.match(/java$/)) {
                        ace.edit("code_editor").getSession().setMode("ace/mode/java");
                    }
                });
            }
        }
    });
};

var expandNode = function (currentElement, project_id, from_root) {
    if (undefined == currentElement.nodes || currentElement.nodes.length == 0) {
        var url = "/rest/file?parent=true&id=" + project_id;
        if (currentElement.relativeName != undefined) {
            url = url + "&name=" + currentElement.relativeName;
        }
        if (from_root != undefined) {
            url = url + "&root=" + from_root;
        }
        cblock($("#main_layout"));
        $.get(url, function (data) {
            var allNodes = [];
            $.each(data, function (index, value) {
                if (value.parent) {
                    allNodes.push({
                        id: value.currentId,
                        project_id: project_id,
                        text: value.name,
                        relativeName: value.relativeName,
                        icon: 'glyphicon glyphicon-folder-close',
                        type: 'folder',
                        group1: true,
                        plus: true,
                        onExpand: function (event) {
                            expandNode(event.object, project_id, false);
                        },
                        onCollapse: function (event) {
                            event.object.icon = "glyphicon glyphicon-folder-close";
                            w2ui['sidebar'].refresh();
                        }
                    });
                } else {
                    allNodes.push({
                        id: value.currentId,
                        project_id: project_id,
                        text: value.name,
                        relativeName: value.relativeName,
                        icon: 'glyphicon glyphicon-file',
                        type: "file",
                        onClick: function (event) {
                            window.location.href = "file.jsp#id=" + project_id + "&name=" + event.object.relativeName;
                            $.get("/rest/file/content", {
                                id: project_id,
                                name: event.object.relativeName
                            }, function (data) {
                                // var
                                // real_data
                                // =
                                // JSON.parse(data);
                                ace.edit("code_editor").setValue(data);
                                if (event.object.text.match(/cs$/)) {
                                    ace.edit("code_editor").getSession().setMode("ace/mode/csharp");
                                } else if (event.object.text.match(/java$/)) {
                                    ace.edit("code_editor").getSession().setMode("ace/mode/java");
                                }
                            });
                        }
                    });
                }
            });
            if (allNodes.length > 0)
                currentElement.icon = "glyphicon glyphicon-folder-open";
            w2ui['sidebar'].add(currentElement, allNodes);
            $("#main_layout").unblock();
        });
    }
    w2ui['sidebar'].refresh();
};