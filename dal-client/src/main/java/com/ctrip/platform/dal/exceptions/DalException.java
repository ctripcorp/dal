package com.ctrip.platform.dal.exceptions;

import java.sql.SQLException;

public class DalException extends SQLException{
	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;
	
	public DalException(String reason){
		super(reason);
	}
	
	public DalException(ErrorCode error){
		super(error.getMessage());
		this.errorCode = error;
	}

	public DalException(ErrorCode error, Throwable e){
		super(error.getMessage(), e);
		this.errorCode = error;
	}
	public DalException(ErrorCode error, Object... args) {
		super(String.format(error.getMessage(), args));
		this.errorCode = error;
	}

	public DalException(ErrorCode error, Throwable e, Object... args){
		super(String.format(error.getMessage(), args), e);
		this.errorCode = error;
	}
	
	@Override
	public int getErrorCode() {
		return this.errorCode.getCode();
	}
	
	public static DalException wrap(Throwable e) {
		return e instanceof DalException ? (DalException)e: new DalException(ErrorCode.Unknown, e);
	}
	
	public static DalException wrap(ErrorCode defaultError, Throwable e) {
		return e instanceof DalException ? (DalException)e: new DalException(defaultError, e);
	}
}