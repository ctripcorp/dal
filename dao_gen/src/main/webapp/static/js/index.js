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
        topHTML: '<div style="background-color: #eee; padding: 10px 5px 10px 20px; border-bottom: 1px solid silver"><a href="javascript:;"><i class="fa fa-plus"></i>添加项目</a>&nbsp;&nbsp;<a href="javascript:;"><i class="fa fa-refresh"></i>刷新</a></div>',
        menu: [{
            id: 1,
            text: 'Edit',
            icon: 'fa fa-edit'
        }, {
            id: 2,
            text: 'Delete',
            icon: 'fa fa-times'
        }],
        nodes: [{
            id: 'all_projects',
            text: 'All Projects',
            icon: 'fa fa-folder-o',
            plus: true,
            onExpand: function (event) {
                // var centerY = null;
                $("#main_layout").block({
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
                var currentElement = this;
                currentElement.nodes[0].icon = "fa fa-folder-open-o";
                if(undefined != this.nodes && this.nodes.length > 0){
                    
                    if(undefined == currentElement.nodes[0].nodes || currentElement.nodes[0].nodes.length == 0){
                        // this.add('all_projects', {
                        //     id: 'level-2',
                        //     text: 'All Projects',
                        //     icon: 'fa fa-folder-o'
                        // });
                        $.get("/rest/file?type=all", function(data){
                            $.each(data, function(index,value){
                                currentElement.add('all_projects',{
                                    id: value.id,
                                    text: value.name,
                                    icon: 'fa fa-play'
                                });
                            });
                        });
                    }
                }
                $("#main_layout").unblock();
                currentElement.refresh();
            },
            onCollapse: function(event){
                //console.log(this == w2ui["sidebar"]);
                this.nodes[0].icon = "fa fa-folder-o";
                this.refresh();
            },
        }],
    }));
    //End tree side bar

    w2ui['main_layout'].content('main', $().w2grid({ 
        name: 'grid', 
        show: { 
            toolbar: true,
            footer: true,
            toolbarAdd: true,
            toolbarDelete: true,
            //toolbarSave: true,
            toolbarEdit: true
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


});