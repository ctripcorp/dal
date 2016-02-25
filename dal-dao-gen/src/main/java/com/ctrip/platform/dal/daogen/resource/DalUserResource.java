package com.ctrip.platform.dal.daogen.resource;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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

	//
	static {
		SpringBeanGetter.initializeApplicationContext();
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

		// password

		LoginUser user = new LoginUser();
		user.setUserNo(userNo);
		user.setUserName(userName);
		user.setUserEmail(userEmail);
		try {
			int result = SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
			if (result <= 0) {
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
	@Path("validate")
	public Status validateUser(@FormParam("userId") String userNo, @FormParam("password") String password) {
		Status status = Status.ERROR;
		if (userNo == null || userNo.isEmpty()) {
			status.setInfo("User Number can't be null.");
			return status;
		}

		if (password == null || password.isEmpty()) {
			status.setInfo("Password can't be null.");
			return status;
		}

		try {
			LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
			if (user != null) {
				String pw = user.getPassword();
				if (pw != null && pw.equals(MD5Util.parseStrToMd5L32(password))) {
					status = Status.OK;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			status.setInfo(e.getMessage());
		}

		return status;
	}

	@POST
	@Path("exist")
	public Status isUserExists(@FormParam("userNo") String userNo) {
		Status status = Status.ERROR;
		if (userNo == null || userNo.isEmpty()) {
			status.setInfo("工号不能为空");
			return status;
		}

		try {
			LoginUser user = SpringBeanGetter.getDaoOfLoginUser().getUserByNo(userNo);
			if (user != null && user.getUserNo().equals(userNo)) {
				status.setInfo("工号已存在");
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
}
