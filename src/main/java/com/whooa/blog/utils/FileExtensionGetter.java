package com.whooa.blog.utils;

import com.google.common.io.Files;

public class FileExtensionGetter {

	public static String get(String fileName) {
		/*
		 * 먼저 주어진 파일 이름이 비어 있는지 확인하고 파일 이름이 비어 있지 않으면 주어진 파일 이름을 추상 경로 이름으로 변환하여 File 인스턴스를 생성하고, File의 getName() 메서드를 호출한다.
		 * 추상 경로 이름이 나타내는 파일의 이름을 반환하거나 주어진 파일 이름이 비어 있으면 빈 문자열을 반환한다. 
		 * 반환 값에 따라 String 클래스의 내장 메서드인 lastIndexOf(char)를 사용하여 '.'의 마지막 발생 인덱스를 가져온니다.
		 * 1. 확장자가 없는 경우 빈 문자열을 반환한다.
		 * 2. 확장자만 있는 경우 점 뒤의 문자열을 반환한다(e.g., .gitignore).
		 */
		return Files.getFileExtension(fileName);
	}
}