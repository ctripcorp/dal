package com.ctrip.platform.dal.daogen.resource;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.LoginUser;
import com.ctrip.platform.dal.daogen.utils.MD5Util;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

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
	public Status userSignIn(@FormParam("userNo") String userNo, @FormParam("password") String password) {
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
	public Status userSignUp(@FormParam("userNo") String userNo, @FormParam("userName") String userName,
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
				log.error("创建用户失败");
				status.setInfo("创建用户失败");
				return status;
			}
			status = status.OK;
		} catch (Exception e) {
			String message = e.getMessage() == null ? e.toString() : e.getMessage();
			log.error(message);
			status.setInfo(message);
			return status;
		}

		return status;
	}

}
