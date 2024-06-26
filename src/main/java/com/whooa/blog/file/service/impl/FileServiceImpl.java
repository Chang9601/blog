package com.whooa.blog.file.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.file.dto.FileDto;
import com.whooa.blog.file.entity.FileEntity;
import com.whooa.blog.file.exception.DirectoryNotCreatedException;
import com.whooa.blog.file.exception.FileNotDownloadedException;
import com.whooa.blog.file.exception.FileNotFoundException;
import com.whooa.blog.file.exception.FileNotStoredException;
import com.whooa.blog.file.exception.InvalidFilePathException;
import com.whooa.blog.file.mapper.FileMapper;
import com.whooa.blog.file.property.FileProperty;
import com.whooa.blog.file.service.FileService;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.utils.FileExtensionGetter;

import jakarta.annotation.PostConstruct;

@Service
public class FileServiceImpl implements FileService {
	private Path path;
	
	public FileServiceImpl(FileProperty fileProperty) {
		/*
		 * Paths 클래스는 경로 문자열 또는 URI를 변환하여 Path를 반환하는 정적 메서드로만 구성된다.
		 * get() 메서드는 경로 문자열 또는 결합될 때 경로 문자열을 형성하는 일련의 문자열을 Path로 변환한다.
		 * toAbsolutePath() 메서드는 절대 경로를 나타내는 Path 객체를 반환한다.
		 * normalize() 메서드는 메서드는 중복 이름 요소가 제거된 경로를 반환한다.
		 * 파일 경로의 맥락에서 정규화는 경로를 그 의미나 위치를 바꾸지 않으면서 가장 간단한 형태로 변환하는 것을 의미한다.
		 * 보통 연속된 경로 구분자('/')나 부모 디렉토리 참조('..')와 같은 중복 이름 요소를 제거한다.
		 * 예를 들어, /user/home/../files -> /user/files
		 */
		this.path = Paths.get(fileProperty.getPath()).toAbsolutePath().normalize();
	}
	
	@PostConstruct
	public void initialize() {
		try {
			/*
			 * Files 클래스는 파일, 디렉토리 또는 다른 종류의 파일에 대해 작동하는 정적 메서드만으로 구성된다.
			 * createDirectories() 메서드는 모든 존재하지 않는 상위 디렉토리를 먼저 생성하여 디렉토리를 생성한다. 
			 * createDirectory() 메서드와는 달리 이미 존재하는 디렉토리이기 때문에 디렉토리를 생성할 수 없는 경우 예외가 발생하지 않는다.
			 */
			Files.createDirectories(this.path);
		} catch (IOException exception) {
			throw new DirectoryNotCreatedException(Code.DIRECTY_NOT_CREATED, new String[] {"업로드 디렉터리를 생성할 수 없습니다."});
		}
	}

	@Override
	public FileDto upload(MultipartFile file, PostEntity postEntity) {
		/* 정규화된 경로를 생성하여 "path/.."과 내부 단순 점과 같은 시퀀스를 제거한다. */	
		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
			
			if (originalFilename.contains("..")) {
				throw new InvalidFilePathException(Code.INVALID_PATH_SEQUENCE, new String[] {"파일 이름 " + originalFilename + "이 유효하지 않습니다."});
			}
		
			/*
			 * resolve() 메서드는 주어진 경로 문자열을 Path로 변환하고 Path에 대해 resolve() 메서드에서 정확하게 지정된 방식으로 해결한다.
			 * 예를 들어, 이름 분리자가 "/"이고 경로가 "foo/bar"를 나타내는 경우 이 메서드를 "gus" 경로 문자열과 함께 호출하면 Path "foo/bar/gus"가 생성된다.
			 */
			Path uploadPath = this.path.resolve(originalFilename);
			/*
			 * 입력 스트림에서 파일로 모든 바이트를 복사하며 반환되면 입력 스트림은 스트림의 끝에 위치한다.
			 * 기본적으로 대상 파일이 이미 존재하거나 심볼릭 링크인 경우 복사가 실패하지만 REPLACE_EXISTING 옵션이 지정된 경우 대상 파일이 이미 존재하면 그 파일이 비어있는 디렉터리가 아닌 경우에만 대상 파일이 교체된다. 
			 * 대상 파일이 이미 존재하고 심볼릭 링크인 경우 심볼릭 링크가 교체된다.
			 */
			Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
			
			String filename = uploadPath.getFileName().toString();
			String filePath = uploadPath.toString();
			Long fileSize = file.getSize();
			String fileExtension = FileExtensionGetter.get(filename);
			String mimeType = file.getContentType();
			
			FileEntity fileEntity = new FileEntity(filename, filePath, fileSize, fileExtension, mimeType);
			fileEntity.setPost(postEntity);
						
			return FileMapper.INSTANCE.toDto(fileEntity);
		} catch (IOException exception) {
			throw new FileNotStoredException(Code.FILE_NOT_STORED, new String[] {"파일 " + originalFilename + "을 저장할 수 없습니다."});
		}
	}

	@Override
	public Resource downalod(String filename) {
		try {
			Path downloadPath = this.path.resolve(filename).normalize();
			Resource resource = new UrlResource(downloadPath.toUri());
			
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException(Code.NOT_FOUND, new String[] {"파일 " + filename + "을 찾을 수 없습니다."});
			}
		} catch (MalformedURLException exception) {
			throw new FileNotDownloadedException(Code.FILE_NOT_DOWNLOADED, new String[] {"파일 " + filename + "을 다운로드할 수 없습니다."});
		}
	}
}