<%@page pageEncoding="UTF-8" %>
<%
    String codegenpageflag = "1.3.4";
    request.setAttribute("codegenpageflag", codegenpageflag);
%>
<!DOCTYPE html>
<html lang='en'>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Ctrip DAO Generator</title>
    <link href="/static/images/favicon.ico" rel="shortcut icon"
          type="image/vnd.microsoft.icon"/>
    <link href="/static/assets/application.css?codegen=${codegenpageflag}"
          media="all" rel="stylesheet"/>
    <link href="/static/assets/print.css?codegen=${codegenpageflag}"
          media="print" rel="stylesheet"/>
</head>
<body class='ui_mars login-page application'>
<header class='navbar navbar-fixed-top navbar-gitlab'>
    <div class='navbar-inner'>
        <div class='container'>
            <div class='app_logo'>
                <a class="home" href=""><img alt="Logo white"
                                             src="/static/images/logo.png"/> </a>
            </div>
            <h1 class='title'></h1>
            <button class='navbar-toggle' data-target='.navbar-collapse'
                    data-toggle='collapse' type='button'>
                <span class='sr-only'>Toggle navigation</span> <i
                    class='fa fa-bars'></i>
            </button>
        </div>
    </div>
</header>
<div class='container navless-container'>
    <div class='content'>
        <div class='flash-container'>
            <div class='flash-alert'>请先登录。</div>
        </div>
        <div class='row prepend-top-20'>
            <div class='col-sm-5'>
                <div>
                    <div class='login-box'>
                        <div class='login-heading'>
                            <h3>登录</h3>
                        </div>
                        <div class='login-body'>
                            <div class="new_user" id="signin_user">
                                <input id="user_login" class="form-control top"
                                       placeholder="工号" required="required" autofocus="autofocus"
                                       type="text" data-toggle="tooltip" data-placement="top"/> <input
                                    id="user_password" class="form-control bottom"
                                    placeholder="密码" required="required" type="password"
                                    data-toggle="tooltip" data-placement="top"/>

                                <div class='remember-me checkbox'>
                                    <label for='user_remember_me'> <input
                                            id="user_remember_me" type="checkbox"/> <span>记住我</span>
                                    </label>
                                </div>
                                <div>
                                    <input type="button" id="signin" name="commit" value="登录"
                                           class="btn btn-save"/>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class='prepend-top-20'>
                        <div class='login-box'>
                            <div class='login-heading'>
                                <h3>创建账号</h3>
                            </div>
                            <div class='login-body'>
                                <div class="new_user" id="user_signup">
                                    <div>
                                        <input id="user_no_sign_up" class="form-control top"
                                               placeholder="工号" required="required" type="text"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div>
                                        <input id="user_name_sign_up" class="form-control middle"
                                               placeholder="姓名" required="required" type="text"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div>
                                        <input id="user_email_sign_up" class="form-control middle"
                                               placeholder="Email" required="required" type="email"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div class='form-group append-bottom-20'
                                         id='password-strength'>
                                        <input id="user_password_sign_up" class="form-control bottom"
                                               placeholder="密码" required="required" type="password"
                                               data-toggle="tooltip" data-placement="top"/>
                                    </div>
                                    <div></div>
                                    <div>
                                        <input type="button" id="signup" name="commit" value="创建"
                                               class="btn-create btn"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/static/assets/application.js?codegen=${codegenpageflag}"></script>
<script src="/static/js/login.js?codegen=${codegenpageflag}"></script>
<script src="/static/js/js.cookie.min.js?codegen=${codegenpageflag}"></script>
</body>
</html>
