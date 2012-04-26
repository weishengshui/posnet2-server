package exception;

/**
 * description：数据解析异常
 * @copyright binfen.cc
 * @projectName external
 * @time 2012-4-26   上午11:49:41
 * @author Seek
 */
public class DataParseException extends Exception {

	private static final long serialVersionUID = -1059577048475750211L;

	public DataParseException() {
		super();
	}

	public DataParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataParseException(String message) {
		super(message);
	}

	public DataParseException(Throwable cause) {
		super(cause);
	}
	
}
