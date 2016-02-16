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
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

@Resource
@Singleton
@Path("user")
public class DalUserResource {
	private static Logger log = Logger.getLogger(DalUserResource.class);

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
			@FormParam("userEmail") String userEmail) {
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
		try {
			int result = SpringBeanGetter.getDaoOfLoginUser().insertUser(user);
			if (result <= 0) {
				log.error("Add user failed, caused by db operation failed, pls check the log.");
				Status status = Status.ERROR;
				status.setInfo("Add operation failed.");
				return status;
			}
		} catch (Exception e) {
			log.error("Add user failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Add operation failed.");
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
			log.error("Update user failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Update operation failed.");
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
			log.error("Delete user failed, caused by db operation failed, pls check the log.");
			Status status = Status.ERROR;
			status.setInfo("Delete operation failed.");
			return status;
		}

		return Status.OK;
	}
}
