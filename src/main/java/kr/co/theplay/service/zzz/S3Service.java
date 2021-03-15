package kr.co.theplay.service.zzz;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@NoArgsConstructor
public class S3Service {
    public static final String CLOUD_FRONT_DOMAIN_NAME = "d2tkmpefgqef0b.cloudfront.net";
    public static final int MAXIMUM_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(String currentFilePath, MultipartFile file) throws IOException {
        // AWS S3 서버에 업로드

        // 이미지 크기가 너무 크다면 에러
        if (file.getSize() > MAXIMUM_FILE_SIZE) {
            return "EXCEED";
        }

        // 고유한 key 값을 갖기위해 현재 시간을 postfix로 붙여줌
        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmsss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        // 리턴받는 fileName을 받아서 다시 Controller에서는 Local DataBase에 정보를 삽입하는 Service를 call 한다.
        return fileName;
    }

    public String edit(String currentFilePath, MultipartFile file) throws IOException {
        // AWS S3 서버에 업로드

        // 이미지 크기가 너무 크다면 에러
        if (file.getSize() > MAXIMUM_FILE_SIZE) {
            throw new FileUploadException("imageSizeExcessLimit");
        }

        // 고유한 key 값을 갖기위해 현재 시간을 postfix로 붙여줌
        SimpleDateFormat date = new SimpleDateFormat("yyyymmddHHmmsss");
        String fileName = file.getOriginalFilename() + "-" + date.format(new Date());

        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = s3Client.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                s3Client.deleteObject(bucket, currentFilePath);
            }
        }

        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        // 리턴받는 fileName을 받아서 다시 Controller에서는 Local DataBase에 정보를 삽입하는 Service를 call 한다.
        return fileName;
    }

    public void delete(String currentFilePath) throws IOException {
        if ("".equals(currentFilePath) == false && currentFilePath != null) {
            boolean isExistObject = s3Client.doesObjectExist(bucket, currentFilePath);

            if (isExistObject == true) {
                s3Client.deleteObject(bucket, currentFilePath);
            }
        }
    }
}
