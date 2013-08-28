package {{product_line}}.{{domain}}.{{app_name}}.dao.request;

public interface Request {
	
	/**
	 * Each version of Request should implement this method
	 * @return
	 */
	public int getProtocolVersion();

}
