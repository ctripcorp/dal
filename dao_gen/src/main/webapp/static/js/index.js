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
            id: "gen_code",
            text: 'Generate Code',
            icon: 'fa fa-play'
        },{
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
                },{
                    type: 'break'
                }, {
                    type: 'button',
                    id: 'genCode',
                    caption: '生成代码',
                    icon: 'fa fa-play'
                },],
                onClick: function (target, data) {
                    switch (target) {
                    case 'refreshDAO':
                        break;
                    case 'addDAO':
                        $("select[id$=databases] > option:gt(0)").remove();
                        $("#page1").modal();
                        $.get("/rest/db/dbs", function (data) {
                            //data = JSON.parse(data);
                            $.each(data, function (index, value) {
                                $('#databases').append($('<option>', {
                                    value: value.name,
                                    text: value.name
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

    $(document.body).on('change', '#databases', function(event){
        $("select[id$=tables] > option:gt(0)").remove();
        if($("#databases").val() == "_please_select")
            return;
        if($("#auto_sql").hasClass('active')){
            $.get("/rest/db/tables?db_name="+$("#databases").val(), function(data){
                $.each(data.ids, function(index, value){
                    $('#tables').append($('<option>', {
                        value: value,
                        text: value
                    }));
                });
            });
        }
    });    

    $(document.body).on('change', '#tables', function(event){
        $("select[id$=fields_left] > option").remove();
        $("select[id$=fields_condition] > option:gt(0)").remove();
        if($("#tables").val() == "_please_select")
            return;

        var url = sprintf("/rest/db/fields?table_name=%s&db_name=%s", $(this).val(), $("#databases").val());

        $.get(url, function (data) {
            $.each(data, function (index, value) {
               $("#fields_left").append($('<option>', {
                    value: value.name,
                    text: sprintf("%s%s%s", 
                        value.name, value.indexed?"*":"",
                        value.primary?"+":"")
                }));
               $("#fields_condition").append($('<option>', {
                    value: value.name,
                    text: value.name
                }));
            });

        });
    });    


    $(document.body).on('change', "#inputWalls", function(event){
        if(this.value == "Select"){
            $("#crud_type").hide();
            $("#operation_fields").show();
            $("#where_condition").show();
        }
        else{
            $("#crud_ratio").val("spa_sp3");
            if(!$("#crud_ratio[value='spa_sp3']").parent().hasClass('active')){
                $("#crud_ratio[value='spa_sp3']").parent().addClass('active');
                $("#crud_ratio[value='sql']").parent().removeClass('active');
            }
            //$("#crud_ratio[value='spa_sp3']").attr('checked', 'checked');
            $("#crud_type").show();
            $("#operation_fields").hide();
            $("#where_condition").hide();
        }
    });

    $(document.body).on('change', "#crud_ratio", function(event){
        if(this.value == "spa_sp3"){
            $("#operation_fields").hide();
            $("#where_condition").hide();
        }
        else{
            var currentVal = $("#operationType > .control-group > div >label.active").children().val();
            if(currentVal == "Delete"){
                $("#operation_fields").hide();
                $("#where_condition").show();
            }else if(currentVal == "Update"){
                $("#operation_fields").show();
                $("#where_condition").show();
            }else{
                $("#operation_fields").show();
                $("#where_condition").hide();
            }
        }
    });

    $("button.move").click(function () {
        $.each($('#fields_left').find(":selected"), function (index, value) {
            $("#fields_right").append(value);
        });
    });

    $("button.moveall").click(function(){
        $("#fields_left option").each(function () {
            $("#fields_right").append($(this));
        });
    });

    $("button.remove").click(function () {
        $.each($('#fields_right').find(":selected"), function (index, value) {
            $("#fields_left").append(value);
        });
    });

    $("button.removeall").click(function () {
        $("#fields_right option").each(function () {
            $("#fields_left").append($(this));
        });
    });

    $("#add_condition").click(function(){
        var selectedField = $("#fields_condition").val();
        var selectedCondition = $("#condition_values").val();
        if(selectedField != "-1" && selectedCondition != "-1"){
            $("#selected_condition").append($('<option>', {
                value: sprintf("%s_%s",selectedField,selectedCondition),
                text: sprintf("%s %s",$("#fields_condition").find(":selected").text()
                    ,$("#condition_values").find(":selected").text())
            }));
        }
    });

    $("#del_condition").click(function(){
        $("#selected_condition").find(":selected").remove();
    });

    $("#save_dao").click(function(){
        if($("#auto_sql").hasClass('active')){
            var postData = {};
            postData["project_id"] = w2ui['grid'].current_project;
            postData["task_type"] = "auto";
            postData["db_name"] = $("#databases").val();
            postData["table_name"] = $("#tables").val();
            postData["class_name"] = $("#class_name").val();
            postData["method_name"] = $("#method_name").val();
            postData["sql_style"] = $("#sql_style").val();
            postData["sql_type"] = $("#crud_type > .control-group > .btn-group > label.active").children().val();
            postData["crud_type"] = $("#operationType > .control-group > div >label.active").children().val();
            postData["action"] = "insert";
            var selectedFields = [];
            var selectedConditions = [];

            $.each($("#fields_right option"), function(index,value){
                selectedFields.push($(value).val());
            });
            $.each($("#selected_condition option"), function(index,value){
                selectedConditions.push($(value).val());
            });

            postData["fields"] = selectedFields.join(",");
            postData["condition"] = selectedConditions.join(",");

            $.post("/rest/task", postData, function(data){
                w2ui["grid_toolbar"].click('refreshDAO', null);
            });
        }
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
                id: value.id,
                text: value.name,
                namespace: value.namespace,
                icon: 'fa fa-tasks',
                onClick:function(event){
                    var id = event.target;
                    $.get("/rest/task?project_id="+id,function(data){
                        console.log(data);
                    });
                    w2ui['grid'].current_project = id;
                }
            });
        });
        currentElement.add('all_projects',new_nodes);
        currentElement.nodes[0].expanded=true;
        currentElement.refresh();
        $("body").unblock();
    });
};