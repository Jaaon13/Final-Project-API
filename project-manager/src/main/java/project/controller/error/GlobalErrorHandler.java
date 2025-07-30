package project.controller.error;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	private enum LogStatus {
		STACK_TRACE, MESSAGE_ONLY
	}
	
	@Data
	private class ExceptionMessage { // Message Sent Back In JSON
		private String message;
		private String statusReason;
		private int statusCode;
		private String timeStamp;
		private String uri;
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ExceptionMessage handleNSEE(NoSuchElementException e, WebRequest webRequest) {
		return buildExceptionMessage(e, HttpStatus.NOT_FOUND, webRequest, LogStatus.MESSAGE_ONLY);
	}
	
	@ExceptionHandler(DuplicateEntryException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public ExceptionMessage handleDEE(DuplicateEntryException e, WebRequest webRequest) {
		return buildExceptionMessage(e, HttpStatus.CONFLICT, webRequest, LogStatus.MESSAGE_ONLY);
	}
	
	private ExceptionMessage buildExceptionMessage(Exception e, HttpStatus status, WebRequest webRequest,
			LogStatus logStatus) { // Builds & Then Returns The Exception Message

		String message = e.toString();
		String statusReason = status.getReasonPhrase();
		int statusCode = status.value();
		String timeStamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		String uri = null;
		
		if(webRequest instanceof ServletWebRequest swr) {
			
			uri = swr.getRequest().getRequestURI();
			
		}
		
		if(logStatus == LogStatus.MESSAGE_ONLY ) {
			log.error("Exception: {}", e.toString());
		} else {
			log.error("Exception: {}", e);
		}
		
		ExceptionMessage emsg =  new ExceptionMessage();
		
		emsg.setMessage(message);
		emsg.setStatusCode(statusCode);
		emsg.setStatusReason(statusReason);
		emsg.setTimeStamp(timeStamp);
		emsg.setUri(uri);
		
		return emsg;
		
	}
	
}
