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
        topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a id="addProj" href="javascript:;"><i class="fa fa-plus"></i>添加项目</a>&nbsp;&nbsp;<a href="javascript:;" onclick="reloadProjects();"><i class="fa fa-refresh"></i>刷新</a></div>',
        menu: [{
            id: "edit_proj",
            text: 'Edit',
            icon: 'fa fa-edit'
        }, {
            id: "del_proj",
            text: 'Delete',
            icon: 'fa fa-times'
        }],
        onMenuClick: function(event){
           switch(event.menuItem.id){
            case "edit_proj":
                $("#project_id").val(event.target);
                var project = w2ui['sidebar'].get(event.target);
                if(project != undefined){
                    $("#name").val(project.text);
                    $("#namespace").val(project.namespace);
                }
                $("#projectModal").attr("is_update", "1");
                $("#projectModal").modal();
                break;
            case "del_proj":
                if(confirm("Are you sure to delete this project?")){
                    var post_data = {};

                    post_data["id"] = event.target;
                    post_data["action"] = "delete";
                    $.post("/rest/project", post_data, function(data){
                        reloadProjects();
                    });
                }
                break;
           }
        },
        nodes: [{
            id: 'all_projects',
            text: '所有项目',
            icon: 'fa fa-folder-o',
            // plus: true,
            group: true,
        }],
    }));
    //End tree side bar

    w2ui['main_layout'].content('main', $().w2grid({ 
        name: 'grid', 
        show: { 
            toolbar: true,
            footer: true,
            toolbarReload: false,
            toolbarColumns: false,
            //toolbarSearch: false,
            toolbarAdd: false,
            toolbarDelete: false,
            //toolbarSave: true,
            toolbarEdit: false
        },
        toolbar: {
                items: [{
                    type: 'break'
                }, {
                    type: 'button',
                    id: 'refreshDAO',
                    caption: '刷新',
                    icon: 'fa fa-refresh'
                }, {
                    type: 'button',
                    id: 'addDAO',
                    caption: '添加DAO',
                    icon: 'fa fa-plus'
                }, {
                    type: 'button',
                    id: 'editDAO',
                    caption: '修改DAO',
                    icon: 'fa fa-edit'
                }, {
                    type: 'button',
                    id: 'delDAO',
                    caption: '删除DAO',
                    icon: 'fa fa-times'
                }, ],
                onClick: function (target, data) {
                    switch (target) {
                    case 'refreshDAO':
                        break;
                    case 'addDAO':
                        $("#page1").modal();
                        $.get("/rest/db/dbs", function (data) {
                            //data = JSON.parse(data);
                            $.each(data.ids, function (index, value) {
                                $('#databases').append($('<option>', {
                                    value: value,
                                    text: value
                                }));
                            });
                        });
                        break;
                    case 'editDAO':
                        break;
                    case 'delDAO':
                        break;
                    }
                }
        },
        searches: [             
            { field: 'dbname', caption: 'Database', type: 'text' },
            { field: 'daoname', caption: 'DAO Name', type: 'text' },
            { field: 'type', caption: 'DAO Type', type: 'text' },
        ],
        columns: [              
            { field: 'dbname', caption: 'Database', size: '10%', sortable: true, attr: 'align=center' },
            { field: 'daoname', caption: 'DAO Name', size: '20%', sortable: true },
            { field: 'funcname', caption: 'Function Name', size: '20%', sortable: true },
            { field: 'type', caption: 'DAO Type', size: '20%' },
            { field: 'preview', caption: 'Preview', size: '30%' },
        ],
        onAdd: function (event) {
            w2alert('add');
        },
        onEdit: function (event) {
            w2alert('edit');
        },
        onDelete: function (event) {
            console.log('delete has default behaviour');
        },
        onSave: function (event) {
            w2alert('save');
        },
        records: []
    })); 

    $(document.body).on('click', '#addProj', function(event){
        $("#projectModal").attr("is_update", "0");
        $("#projectModal").modal();
    });

    $(document.body).on('click', '#save_proj', function(event){
        var post_data = {};

        var currentid = $("#project_id").val();
        if($("#projectModal").attr("is_update") == "1" && 
            currentid!= undefined && currentid != ""){
            post_data["action"] = "update";
            post_data["id"] = currentid;
        }else{
            post_data["action"] = "insert";
        }
        post_data["name"] = $("#name").val();
        post_data["namespace"] = $("#namespace").val();
        

        $.post("/rest/project", post_data, function (data) {
            $("#projectModal").modal('hide');
            reloadProjects();
        });
    });    


    reloadProjects();

});


var reloadProjects = function(){
    $("body").block({
        message: '<img src="/static/images/ajax-loading.gif" align="">',
        // centerY: centerY != undefined ? centerY : true,
        css: {
            top: '10%',
            border: 'none',
            padding: '2px',
            backgroundColor: 'none'
        },
        overlayCSS: {
            backgroundColor: '#000',
            opacity: 0.05,
            cursor: 'wait'
        }
    });
    var currentElement = w2ui['sidebar'];
    var nodes = [];
    $.each(currentElement.nodes[0].nodes, function(index, value){
        nodes.push(value.id);
    });
    currentElement.remove.apply(currentElement, nodes);
    $.get("/rest/project", function(data){
        var new_nodes = [];
        //data = JSON.parse(data);
        $.each(data, function(index,value){
            new_nodes.push({
                id: value._id.$oid,
                text: value.name,
                namespace: value.namespace,
                icon: 'fa fa-tasks'
            });
        });
        currentElement.add('all_projects',new_nodes);
        currentElement.nodes[0].expanded=true;
        currentElement.refresh();
        $("body").unblock();
    });
};