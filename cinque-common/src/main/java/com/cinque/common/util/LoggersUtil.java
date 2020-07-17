package com.cinque.common.util;

import java.time.Instant;

import org.slf4j.Logger;

public class LoggersUtil {
	
	private static final String ERROR_LOGGING_FORMAT = "{ Timestamp: [ %s ]; userId: [ %s ]; ActionName: [ %s ]; ErrorCode:[ %s ]; Description: [ %s ], Exception: [ %s ]; Stack Trace: %s }";
	private static final String LOGGING_FORMAT = "{ Timestamp: [ %s ]; userId: [ %s ]; ActionName: [ %s ], ServiceType: [ %s ], ServiceStatus: [ %s ], StatusCode: [ %s ], Description: [ %s ] }";
	private static String STACK_TRACE_SEPARATOR = ", ";
	
	/**
	 * Method to create info/debug/warn level log details.
	 * 
	 * @param userId
	 * @param seesionId
	 * @param actionName
	 * @return
	 */
	private static String logMessage(String userId, String actionName, String serviceType,
			String serviceStatus, String statusCode, String logMessage) {

		return String.format(LOGGING_FORMAT, Instant.now(), userId, actionName, serviceType, serviceStatus,
				statusCode, logMessage);
	}

	public static void info(Logger logger, String userId, String actionName, String serviceType,
			String serviceStatus, String statusCode, String logMessage) {
		if (logger.isInfoEnabled()) {
			logger.info(logMessage(userId, actionName, serviceType, serviceStatus, statusCode,
					logMessage));
		}
	}

	public static void debug(Logger logger, String userId, String actionName, String serviceType,
			String serviceStatus, String statusCode, String logMessage) {
		if (logger.isDebugEnabled()) {
			logger.debug(logMessage(userId, actionName, serviceType, serviceStatus, statusCode,
					logMessage));
		}
	}

	public static void warn(Logger logger, String userId, String actionName, String serviceType,
			String serviceStatus, String statusCode, String logMessage) {
		if (logger.isWarnEnabled()) {
			logger.warn(logMessage(userId, actionName, serviceType, serviceStatus, statusCode,
					logMessage));
		}
	}

	public static void error(Logger logger, String userId, String actionName, String serviceType,
			String serviceStatus, String statusCode, Throwable t, String logMessage) {
		if (logger.isErrorEnabled()) {

			logger.error(logMessage(userId, actionName, serviceType, serviceStatus, statusCode,
					logMessage));
			logger.error(logErrorMessage(userId, actionName, t, logMessage));
		}
	}

	private static String stackTraceToString(Throwable t) {
		String stackTraceString = null;
		if (t != null) {
			boolean isFirst = true;
			StackTraceElement[] traces = t.getStackTrace();
			StringBuilder stackTraceStringBuilder = new StringBuilder();
			for (StackTraceElement trace : traces) {
				if (!isFirst) {
					stackTraceStringBuilder.append(STACK_TRACE_SEPARATOR);
				} else {
					isFirst = false;
				}
				stackTraceStringBuilder.append(trace.toString());
			}
			stackTraceString = stackTraceStringBuilder.toString();
		}
		return stackTraceString;
	}
	
	/**
	 * Method to create error level log message.
	 * 
	 * @param userId
	 * @param seesionId
	 * @param actionName
	 * @param t
	 * @return
	 */
	private static String logErrorMessage(String userId, String actionName, Throwable t,
			String logMessage) {
		String errorMessage = null;
		String stackTrace = null;
		Integer errorCode = null;
		String description = logMessage;
		
		if (t != null) {
//			if (t instanceof XXXXException) {
//				XXXXException ne = (XXXException) t;
//				errorCode = ne.getErrorCode();
//				description = ne.getDescription();
//			}
			errorMessage = t.getMessage();
			stackTrace = stackTraceToString(t);
		}
		
		return String.format(ERROR_LOGGING_FORMAT, Instant.now(), userId, actionName, errorCode, description,
				errorMessage, stackTrace);
	}

}
