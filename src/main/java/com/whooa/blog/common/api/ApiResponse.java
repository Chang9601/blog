package com.whooa.blog.common.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import com.whooa.blog.common.code.Code;

public class ApiResponse<T> {
	private final Metadata metadata;
	private final T data;

	private ApiResponse(final int code, final String message, final T data, final String[] details) {
		this.metadata = new Metadata(code, message, details);
		this.data = data;
	}

	public static <T> ApiResponse<T> handleSuccess(final int code, final String message, final T data, final String[] details) {
		final int successCode = code != -1 ? code : Code.OK.getCode();
		final String successMessage = message != null ? message : Code.OK.getMessage();
		
		return new ApiResponse<T>(successCode, successMessage, data, details);
	}
	
	public static <T> ApiResponse<T> handleFailure(final int code, final String message, final T data, final String[] details) {
		final int failureCode = code != -1 ? code : Code.INTERNAL_SERVER_ERROR.getCode();
		final String failureMessage = message != null ? message : Code.INTERNAL_SERVER_ERROR.getMessage();
		
		return new ApiResponse<T>(failureCode, failureMessage, data, details);		
	}
	
	// com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class
	// Spring Boot는 기본적으로 내장된 Jackson 라이브러리를 가지는데 직렬화 과정에서 접근 제한자가 public 이거나 게터와 세터를 이용하기 때문에 필드가 private으로 선언되어있으면 JSON 변환 과정에서 문제가 발생한다.
	// 직렬화는 객체를 JSON으로 변환하는 것을 의미한다. 역직렬화는 이의 반대이다.
	// 기본적인 해결법은 게터를 사용하는 것이다.
	public Metadata getMetadata() {
		return metadata;
	}

	public T getData() {
		return data;
	}
	
	@Override
	public String toString() {
		return "ApiResponse [metadata=" + metadata + ", data=" + data + "]";
	}

	// 중첩 클래스를 사용하는 이유.
	// 1. 논리적으로 연관된 클래스들을 하나의 패키지에 묶을 수 있다.
	// 2. 가독성이 향상되고 유지보수가 쉬운 코드를 만들 수 있다.
	
	// 내부 클래스 vs. 정적 중첩 클래스.
	// 내부 클래스는 인스턴스 메서드와 인스턴스 필드와 같이 외부 클래스의 인스턴스와 연결되어 있어 외부 클래스의 메서드와 필드에 접근할 수 있다. 내부 클래스의 인스턴스는 외부 클래스의 인스턴스가 존재해야만 존재할 수 있다.
	// 정적 중첩 클래스는 클래스 메서드와 클래스 필드와 같이 행동하며 외부 클래스의 메서드와 필드에 접근할 수 없다. 정적 중첩 클래스의 인스턴스는 외부 클래스의 인스턴스가 존재할 필요가 없다.
	
	// 어느 것을 사용해야 하는가?
	// 외부 클래스를 참조할 이유가 없다면 정적 중첩 클래스를 사용한다.
	// 1. 내부 클래스는 외부 클래스의 인스턴스화 후 인스턴스화가 가능하며 두 인스턴스의 관계 정보는 내부 클래스의 인스턴스 안에 만들어져 메모리 공간을 더 차지하고 생성 시간도 더 걸린다.
	// 2. 내부 클래스의 인스턴스가 외부 클래스의 인스턴스를 참조를 가지고 있기 때문에 가비지 컬렉터는 외부 클래스의 인스턴스를 수거 대상으로 보지 않기에 가비지 컬렉션에서 제외된다.
	public static class Metadata {
		private final int code;
		private final String messsage;
		private final long timestamp;
		private final String[] details;
		
		private Metadata(final int code, final String message, final String[] details) {
			this.code = code;
			this.messsage = message;
			this.details = details;
			this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		}

		public int getCode() {
			return code;
		}

		public String getMesssage() {
			return messsage;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public String[] getDetails() {
			return details;
		}

		@Override
		public String toString() {
			return "Metadata [code=" + code + ", messsage=" + messsage + ", timestamp=" + timestamp + ", details="
					+ Arrays.toString(details) + "]";
		}
	}
}