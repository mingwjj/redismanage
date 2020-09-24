<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLDecoder"%>
<%
	String path = request.getContextPath();
	String params = request.getParameter("params");

%>
<!DOCTYPE html>
<html lang="zh_CN">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title></title>
    <script src="<%=path%>/js/jquery-1.10.2.min.js"></script>

    <link href="<%=path%>/bootstrap-3.3.7/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="<%=path%>/bootstrap-3.3.7/dist/css/bootstrap-treeview.min.css" rel="stylesheet" />
    <script src="<%=path%>/bootstrap-3.3.7/dist/js/bootstrap.min.js"></script>
    <script src="<%=path%>/bootstrap-3.3.7/dist/js/bootstrap-treeview.min.js"></script>

    <link href="<%=path%>/bootstrap-table-1.8.1/dist/bootstrap-table.css" rel="stylesheet" />
    <script src="<%=path%>/bootstrap-table-1.8.1/dist/bootstrap-table.js"></script>
    <script src="<%=path%>/bootstrap-table-1.8.1/dist/locale/bootstrap-table-zh-CN.js"></script>
    
    <link href="<%=path%>/css/toastr.min.css" rel="stylesheet" />
    <script src="<%=path%>/js/toastr.min.js"></script>
    <script src="<%=path%>/js/Ewin.js"></script>
     
    <!--[if lt IE 9]>
      <script src="<%=path%>/js/html5shiv.min.js"></script>
      <script src="<%=path%>/js/respond.min.js"></script>
    <![endif]-->
  </head>
  <body style="padding-top:50px;">
	<div class="navbar navbar-default navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-1">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">缓存管理</a>
      </div>
      <div class="collapse navbar-collapse" id="navbar-collapse-1">
        <ul class="nav navbar-nav navbar-right">
          <li><a href="<%=path%>/index.jsp">组件维护</a></li>
          <li><a href="<%=path%>/configcenter/index?key=component_category" id="config">配置项维护</a></li>
          <li class="active"><a href="<%=path%>/manager/ConfigCenter/erds.jsp">资源目录</a></li>
          <li><a href="<%=path%>/configcenter/tam?key=aduser" id="tamcon">认证帐号</a></li>
          <li><a href="<%=path%>/configcenter/tam?key=adgroup" id="tamcongroup">认证群组</a></li>
          <li><a href="${pageContext.request.contextPath}/configcenter/exportData">导出数据</a></li>
            <li><a href="javascript:void(0);" onclick="initData();">初始化配置项</a></li>
            <li><a href="javascript:void(0);" onclick="syncLdapData();">同步目录数据</a></li>
        </ul>
      </div>
 
      </div>
    </div>
    
  <div class="panel-body" style="padding-bottom:0px;">
	<div class="row">
		<!-- left -->
		<div class="col-md-2">
			<div class="panel panel-info">
            <div class="panel-heading">资源目录</div>
            <div class="panel-body" id="leftTree">

            	
            </div>
            </div>
		</div>
		
		<!-- right -->
		<div class="col-md-10">
        <div class="panel panel-info">
            <div class="panel-heading">查询条件</div>
            <div class="panel-body">
                <form id="formSearch" class="form-horizontal">
                    <div class="form-group" style="margin-top:15px">
                        <label class="control-label col-sm-1" for="txt_search_field">配置项</label>
                        <div class="col-sm-3">
                        	<input name="key" type="hidden" id="key" value=""/>
                            <input type="text" class="form-control" id="txt_search_field">
                        </div>
                        <label class="control-label col-sm-1" for="txt_search_value">值</label>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" id="txt_search_value">
                        </div>
                        <div class="col-sm-4" style="text-align:left;">
                        	<div id="toolbar0" class="btn-group">
					            <button id="btn_query" type="button" class="btn btn-default">
					                <span class="glyphicon glyphicon-search" aria-hidden="true"></span>查询
					            </button>
					            <button id="btn_reset" type="button" class="btn btn-default">
					                <span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>重置
					            </button>
					        </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>       

        <div id="toolbar" class="btn-group">
            <button id="btn_add" type="button" class="btn btn-default">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>新增
            </button>
            <button id="btn_edit" type="button" class="btn btn-default">
                <span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>修改
            </button>
            <button id="btn_delete" type="button" class="btn btn-danger">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除
            </button>
        </div>
        <table id="tb_config"></table>
		<!--模态框-->
		<div class="modal fade addmd" tabindex="-1" role="dialog" id="myModal">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						<h4 id="myModalLabel" class="modal-title">增加</h4>
					</div>
					<div class="modal-body">
						<form>
							<div class="form-group">
								<label for="field">配置项</label>
								<input type="text" placeholder="配置项" id="field" name="field" class="form-control" />
							</div>
							<div class="form-group">
								<label for="value">值</label>
								<input type="text" placeholder="值" id="value" name="value" class="form-control"/>
							</div>
							<div class="form-group">
								<label for="value">描述</label>
								<input type="text" placeholder="描述" id="desc" name="desc" class="form-control"/>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default cancel" data-dismiss="modal">取消</button>
						<button id="btn_submit" type="button" class="btn btn-primary que_add">确定</button><!--确定添加-->
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div>
    </div><!-- /.right  -->
		
	</div><!-- /.row  -->
    </div><!-- /.panel-body  -->
	</body>
</html>
<SCRIPT LANGUAGE="JavaScript">

$(function () {
	toastr.options.positionClass = 'toast-center-center';

    $.ajax({
        type: "GET",
        url: '${pageContext.request.contextPath}/configcenter/getTree',
        data: null,
        dataType: "text",
        success: function(data){
            debugger;
            var dt = eval('['+data+']')

            $('#leftTree').treeview({
                data: dt,
                expand: false,
                onNodeSelected: function(event, data) {
                    //alert(data['id']);//获取选中node的id
                    $('#key').val(data['id']);
                    //var index = $("#leftTree a").index(this);
                    //jQuery("#leftTree a").eq(index).addClass("active").siblings().removeClass("active");

                    $('#tb_config').bootstrapTable('refresh');
                }

            });
            $('#key').val('erdso:merit');

        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert(XMLHttpRequest.status);
            alert(XMLHttpRequest.readyState);
            alert(textStatus);
        }
    });


	jQuery("#leftTree a").click(function(){
		$('#key').val(this.getAttribute('value'));
		var index = $("#leftTree a").index(this);
		jQuery("#leftTree a").eq(index).addClass("active").siblings().removeClass("active");
		
		$('#tb_config').bootstrapTable('refresh');
	});
	
    //1.初始化Table
    var oTable = new TableInit();
    oTable.Init();

    //2.初始化Button的点击事件
    var oButtonInit = new ButtonInit();
    oButtonInit.Init();

});

var TableInit = function () {
    var oTableInit = new Object();
    //初始化Table
    oTableInit.Init = function () {
        $('#tb_config').bootstrapTable({
            url: '${pageContext.request.contextPath}/configcenter/ccenterList',         //请求后台的URL（*）
            method: 'post',                      //请求方式（*）
            contentType: 'application/x-www-form-urlencoded',//method为POST时，必须写此属性，否则会中文乱码
            toolbar: '#toolbar',                //工具按钮用哪个容器
            striped: true,                      //是否显示行间隔色
            cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
            pagination: false,                   //是否显示分页（*）
            sortable: false,                     //是否启用排序
            sortOrder: "asc",                   //排序方式
            queryParams: oTableInit.queryParams,//传递参数（*）
            sidePagination: 'server',           //分页方式：client客户端分页，server服务端分页（*）
            pageNumber:1,                       //初始化加载第一页，默认第一页
            pageSize: 10,                       //每页的记录行数（*）
            pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
            //search: true,                       //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
            strictSearch: true,
            showColumns: true,                  //是否显示所有的列
            //showRefresh: true,                  //是否显示刷新按钮
            minimumCountColumns: 2,             //最少允许的列数
            clickToSelect: true,                //是否启用点击选中行
            height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
            uniqueId: 'field',                     //每一行的唯一标识，一般为主键列
            showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
            cardView: false,                    //是否显示详细视图
            detailView: false,                   //是否显示父子表
            columns: [{
                checkbox: true
            }, {
                field : 'field',
                title : '配置项'
            },{
                field : 'value',
                title : '值'
            },{
                field : 'desc',
                title : '描述'
            } ]
        });
    };

    //得到查询的参数
    oTableInit.queryParams = function (params) {
        var temp = {   //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
            //limit: params.limit,   //页面大小
            //offset: params.offset,  //页码
            field: $('#txt_search_field').val(),
            value: $('#txt_search_value').val(),
            key:$('#key').val()
        };
        return temp;
    };
    return oTableInit;
};


var ButtonInit = function () {
    var oInit = new Object();
    var postdata = {};
    

    oInit.Init = function () {
        //初始化页面上面的按钮事件
        $('#btn_query').click(function () {
        	$('#tb_config').bootstrapTable('refresh');
        });
        
        $('#btn_reset').click(function () {
        	$('#txt_search_field').val('');
            $('#txt_search_value').val('');
        });
        
        $('#btn_add').click(function () {
            $('#myModalLabel').text('新增');
            $('#myModal').find('.form-control').val('');
            $('#myModal').modal()
            postdata.action = 'add';
        });

        $('#btn_edit').click(function () {
            var arrselections = $('#tb_config').bootstrapTable('getSelections');
            if (arrselections.length > 1) {
                toastr.warning('只能选择一行进行编辑');
                return;
            }
            if (arrselections.length <= 0) {
                toastr.warning('请选择有效数据');
                return;
            }
            $('#myModalLabel').text('编辑');
            
            $('#field').val(arrselections[0].field);
            $('#value').val(arrselections[0].value);
            $('#desc').val(arrselections[0].desc);

            postdata.action = 'update';
            //postdata.field_old = "{'field':'"+arrselections[0].field+"','value':'"+arrselections[0].value+"','desc':'"+arrselections[0].desc+"'}";
            postdata.field_old = arrselections[0].field;
            $('#myModal').modal();
        });
        
        $('#btn_delete').click(function () {
            var arrselections = $('#tb_config').bootstrapTable('getSelections');
            var fields = '';
            if (arrselections.length <= 0) {
                toastr.warning('请选择有效数据');
                return;
            } else {
            	var flag = true;
            	for (var i=0; i<arrselections.length; i++) {
            		/**
            		if (flag) {
	            		fields += "{'field':'"+arrselections[i].field+"','value':'"+arrselections[i].value+"','desc':'"+arrselections[i].desc+"'}";;
	            		flag = false;
            		} else {
            			fields += ';' + "{'field':'"+arrselections[i].field+"','value':'"+arrselections[i].value+"','desc':'"+arrselections[i].desc+"'}";;
            		}
            		**/
            		if (flag) {
	            		fields += arrselections[i].field;
	            		flag = false;
            		} else {
            			fields += ',' + arrselections[i].field;
            		}
            	}
            }

            Ewin.confirm({ message: '确认要删除选择的数据吗？' }).on(function (e) {
                if (!e) {
                    return;
                }
                $.ajax({
                    type: 'post',
                    url: '${pageContext.request.contextPath}/configcenter/ConfigCenter_delete',
                    data: { 
                    	key : $('#key').val(),
                    	fields : fields
                    },
                    success: function (data, status) {
                        if (status == 'success') {
                            toastr.success('提交数据成功');
                            $('#tb_config').bootstrapTable('refresh');
                        }
                    },
                    error: function () {
                        toastr.error('Error');
                    },
                    complete: function () {

                    }

                });
            });
        });
        
        $('#btn_submit').click(function () {
            postdata.key = $('#key').val();
            postdata.field = $('#field').val();
            postdata.value = $('#value').val();
            if (null == postdata.field || "" == postdata.field
            		|| null == postdata.value || "" == postdata.value) {
            	toastr.error('代码和名称都不能为空！');
            	return;
            }
            $.ajax({
                type: 'post',
                url: '${pageContext.request.contextPath}/configcenter/ConfigCenter_addOrUpdate4ajax',
                data: { 
                	action : postdata.action,
                	key : $('#key').val(),
                	field : $('#field').val(),
                	field_old : postdata.field_old,
                    value : $('#value').val(),
                    desc : $('#desc').val()
                },
                success: function (data, status) {
                    if (status == 'success') {
                    	if (data == 'exist') {
                    		toastr.success('新增的键已存在，请输入唯一的键值');
                    	} else if (data == 'commexist') {
                    		toastr.success('新增的键在公共的配置项中已存在，请输入唯一的键值');
                    	} else {
                        	$('#myModal').modal('hide');
                            toastr.success('提交数据成功');
                            $('#tb_config').bootstrapTable('refresh');
                    		
                    	}
                    }
                },
                error: function () {
                    toastr.error('Error');
                },
                complete: function () {

                }

            });
        });
   	};

    return oInit;
};

var initData = function() {
	$.ajax({
        type: 'get',
        url: '${pageContext.request.contextPath}/configcenter/initData',
        success: function (data, status) {
            if (status == 'success') {
            	toastr.success('初始化数据成功！');
            	$('#config')[0].click();
            } else {
            	toastr.error('Error');
            }
        },
        error: function () {
            toastr.error('Error');
        },
        complete: function () {

        }

    });
}

var syncLdapData = function() {
    $.ajax({
        type: 'get',
        url: '${pageContext.request.contextPath}/configcenter/syncLdapData',
        success: function (data, status) {
            if (status == 'success') {
                toastr.success('初始化数据成功！');
                $('#config')[0].click();
            } else {
                toastr.error('Error');
            }
        },
        error: function () {
            toastr.error('Error');
        },
        complete: function () {

        }

    });
}



</SCRIPT>
	