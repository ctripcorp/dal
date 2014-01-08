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
        //icon: 'fa fa-wrench',
        nodes: [{
            id: 'all_confs',
            text: '配置',
            icon: 'fa fa-wrench',
            //group: true,
            expanded: true,
            nodes: [{
                id: 'db_conf',
                text: '数据库配置',
                icon: 'fa fa-file'
            },{
                id: 'node_conf',
                text: 'DAS节点配置',
                icon: 'fa fa-file'
            }]
        },{
            id: 'all_monitors',
            text: '监控',
            icon: 'fa fa-comment',
            //group: true,
            expanded: true,
            nodes: [{
                id: 'das_monitor',
                text: 'DAS节点监控',
                icon: 'fa fa-file'
            },{
                id: 'sql_monitor',
                text: 'SQL执行监控',
                icon: 'fa fa-file'
            }]
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