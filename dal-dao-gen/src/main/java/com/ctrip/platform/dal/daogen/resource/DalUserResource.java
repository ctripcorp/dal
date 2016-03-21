package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.MD5Util;
import com.ctrip.platform.dal.daogen.utils.RequestUtil;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Resource
@Singleton
@Path("user")
public class DalUserResource {
    private static Logger log = Logger.getLogger(DalUserResource.class);

    private static final String userNumberNullMessage = "工号不能为空";

    private static final String userNumberExistMessage = "工号已存在";

    private static final String userNameNullMessage = "姓名不能为空";

    private static final String emailNullMessage = "Email 不能为空";

    private static final String passwordNullMessage = "密码不能为空";

    private static final String loginFailMessage = "用户名或密码不正确";

    // TBD
    static {
        SpringBeanGetter.refreshApplicationContext();
    }

    @GET
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    public List<LoginUser> getAllUsers() {
        List<LoginUser> users = SpringBeanGetter.getDaoOfLoginUser().getAllUsers();
        return users;
    }

    @POST
    @Path("add")
    public Status addUser(@FormParam("userNo") String userNo, @FormParam("userName") String userName,
                          @FormParam("userEmail") String userEmail, @FormParam("password") String password) {
        if (userNo == null) {
            log.error(String.format("Add user failed, caused by illegal parameters:userNo=%s", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (userName == null) {
            log.error(String.format("Add user failed, caused by illegal parameters:userName=%s", userName));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (userEmail == null) {
            log.error(String.format("Add user failed, caused by illegal parameters:userEmail=%s", userEmail));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        password = MD5Util.parseStrToMd5L32(password);
        LoginUser user = new LoginUser();
        user.setUserNo(userNo);
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        user.setPassword(password);

        try {
            int result = SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
            if (result < 1) {
                log.error("Add user failed, caused by db operation failed, pls check the log.");
                Status status = Status.ERROR;
                status.setInfo("Add operation failed.");
                return status;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            Status status = Status.ERROR;
            status.setInfo(e.getMessage());
            return status;
        }

        return Status.OK;
    }

    @POST
    @Path("update")
    public Status update(@FormParam("userId") int userId, @FormParam("userNo") String userNo,
                         @FormParam("userName") String userName, @FormParam("userEmail") String userEmail) {
        if (userNo == null) {
            log.error(String.format("Update user failed, caused by illegal parameters:userNo=%s", userNo));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (userName == null) {
            log.error(String.format("Update user failed, caused by illegal parameters:userName=%s", userName));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        if (userEmail == null) {
            log.error(String.format("Update user failed, caused by illegal parameters:userEmail=%s", userEmail));
            Status status = Status.ERROR;
            status.setInfo("Illegal parameters.");
            return status;
        }

        LoginUser user = new LoginUser();
        user.setId(userId);
        user.setUserNo(userNo);
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        try {
            int result = SpringBeanGetter.getDaoOfLoginUser().updateUser(user);
            if (result < 1) {
                log.error("Update user failed, caused by db operation failed, pls check the log.");
                Status status = Status.ERROR;
                status.setInfo("Update operation failed.");
                return status;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            Status status = Status.ERROR;
            status.setInfo(e.getMessage());
            return status;
        }

        return Status.OK;
    }

    @POST
    @Path("delete")
    public Status delete(@FormParam("userId") int userId) {
        try {
            int result = SpringBeanGetter.getDaoOfLoginUser().deleteUser(userId);
            if (result < 1) {
                log.error("Delete user failed, caused by db operation failed, pls check the log.");
                Status status = Status.ERROR;
                status.setInfo("Delete operation failed.");
                return status;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            Status status = Status.ERROR;
            status.setInfo(e.getMessage());
            return status;
        }

        return Status.OK;
    }

    @POST
    @Path("signin")
    public Status userSignIn(@Context HttpServletRequest request, @FormParam("userNo") String userNo, @FormParam("password") String password) {
        Status status = Status.ERROR;
        if (userNo == null || userNo.isEmpty()) {
            status.setInfo(userNumberNullMessage);
            return status;
        }

        if (password == null || password.isEmpty()) {
            status.setInfo(passwordNullMessage);
            return status;
        }

        try {
            LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            if (user != null) {
                String pw = user.getPassword();
                if (pw != null && pw.equals(MD5Util.parseStrToMd5L32(password))) {
                    status = Status.OK;
                    setSession(request, user);
                    return status;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            status.setInfo(e.getMessage());
        }

        status.setInfo(loginFailMessage);
        return status;
    }

    private void setSession(ServletRequest request, LoginUser user) {
        HttpSession session = RequestUtil.getSession(request);
        session.setAttribute(Consts.USER_INFO, user);
        session.setAttribute(Consts.USER_NAME, user.getUserName());
    }

    @POST
    @Path("exist")
    public Status isUserExists(@FormParam("userNo") String userNo) {
        Status status = Status.ERROR;
        if (userNo == null || userNo.isEmpty()) {
            status.setInfo(userNumberNullMessage);
            return status;
        }

        try {
            LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
            if (user != null && user.getUserNo().equals(userNo)) {
                status.setInfo(userNumberExistMessage);
                return status;
            }
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.toString() : e.getMessage();
            log.error(message);
            status.setInfo(message);
            return status;
        }

        status = Status.OK;
        return status;
    }

    @POST
    @Path("signup")
    public Status userSignUp(@Context HttpServletRequest request, @FormParam("userNo") String userNo, @FormParam("userName") String userName,
                             @FormParam("userEmail") String userEmail, @FormParam("password") String password) {
        Status status = Status.ERROR;
        if (userNo == null || userNo.isEmpty()) {
            status.setInfo(userNumberNullMessage);
            return status;
        }
        if (userName == null || userName.isEmpty()) {
            status.setInfo(userNameNullMessage);
            return status;
        }
        if (password == null || password.isEmpty()) {
            status.setInfo(passwordNullMessage);
            return status;
        }
        if (userEmail == null || userEmail.isEmpty()) {
            status.setInfo(emailNullMessage);
            return status;
        }

        password = MD5Util.parseStrToMd5L32(password);
        LoginUser user = new LoginUser();
        user.setUserNo(userNo);
        user.setUserName(userName);
        user.setUserEmail(userEmail);
        user.setPassword(password);

        try {
            int result = SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
            if (result < 1) {
                log.error("用户创建失败");
                status.setInfo("用户创建失败");
                return status;
            }
            setSession(request, user);
            status = status.OK;
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.toString() : e.getMessage();
            log.error(message);
            status.setInfo(message);
            return status;
        }

        return status;
    }

    @GET
    @Path("isSuperUser")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean isSuperUser(@Context HttpServletRequest request) {
        Boolean result = RequestUtil.isSuperUser(request);
        if (result != null) {
            return result.booleanValue();
        }

        HttpSession session = RequestUtil.getSession(request);
        String userNo = RequestUtil.getUserNo(request);
        boolean value = DalGroupResource.validate(userNo);
        session.setAttribute(Consts.SUPER_USER, value);
        return value;
    }

    @GET
    @Path("isDefaultUser")
    public boolean isDefaultUser(@Context HttpServletRequest request) {
        return UserInfoResource.isDefaultInstance(request);
    }

    @GET
    @Path("isDefaultSuperUser")
    public boolean isDefaultSuperUser(@Context HttpServletRequest request) {
        boolean result = true;
        result &= isDefaultUser(request);
        result &= isSuperUser(request);
        return result;
    }

    @POST
    @Path("checkPassword")
    public boolean checkPassword(@Context HttpServletRequest request, @FormParam("password") String password) {
        boolean result = false;
        if (password == null || password.isEmpty()) {
            return result;
        }

        String userNo = RequestUtil.getUserNo(request);
        LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
        if (user != null) {
            String pass = MD5Util.parseStrToMd5L32(password);
            String userPass = user.getPassword();
            if (userPass != null && !userPass.isEmpty()) {
                if (pass.equals(userPass)) {
                    result = true;
                }
            }
        }
        return result;
    }

    @POST
    @Path("changePassword")
    public boolean changePassword(@Context HttpServletRequest request, @FormParam("password") String password) {
        boolean result = false;
        if (password == null || password.isEmpty()) {
            return result;
        }

        try {
            LoginUser user = RequestUtil.getUserInfo(request);
            String pass = MD5Util.parseStrToMd5L32(password);
            user.setPassword(pass);
            result = SpringBeanGetter.getDaoOfLoginUser().updateUserPassword(user) > 0;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return result;
    }

    @POST
    @Path("logOut")
    public void logOut(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        UserInfoResource.getInstance().logOut(request, response);
    }
}
