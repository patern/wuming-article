package com.wuming.common.oss.core;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.AsyncProcessObjectRequest;
import com.aliyun.oss.model.AsyncProcessObjectResult;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.wuming.common.constant.Constants;
import com.wuming.common.oss.constant.OssConstant;
import com.wuming.common.oss.entity.UploadResult;
import com.wuming.common.oss.enums.AccessPolicyType;
import com.wuming.common.oss.exception.OssException;
import com.wuming.common.oss.properties.OssProperties;
import com.wuming.common.utils.DateUtils;
import com.wuming.common.utils.SecurityUtils;
import com.wuming.common.utils.file.FileUtils;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

/**
 * S3 存储协议 所有兼容S3协议的云厂商均支持
 * 阿里云 腾讯云 七牛云 minio
 *
 * @author AprilWind
 */
public class OssClient {

    /**
     * 服务商
     */
    private final String configKey;

    /**
     * 配置属性
     */
    private final OssProperties properties;

    /**
     * Amazon S3 异步客户端
     */
    private final S3AsyncClient client;

    /**
     * Amazon S3 异步客户端
     */
    private OSS oss;

    /**
     * 用于管理 S3 数据传输的高级工具
     */
    private final S3TransferManager transferManager;

    /**
     * AWS S3 预签名 URL 的生成器
     */
    private final S3Presigner presigner;

    public static final String SLASH = "/";

    /**
     * 构造方法
     *
     * @param configKey     配置键
     * @param ossProperties Oss配置属性
     */
    public OssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
        try {
            // 创建 AWS 认证信息
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey()));

            // MinIO 使用 HTTPS 限制使用域名访问，站点填域名。需要启用路径样式访问
            boolean isStyle = !StringUtils.containsAny(properties.getEndpoint(), OssConstant.CLOUD_SERVICE);

            // 创建AWS基于 Netty 的 S3 客户端
            this.client = S3AsyncClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .endpointOverride(URI.create(getEndpoint()))
                    .region(of())
                    .forcePathStyle(isStyle)
                    .httpClient(NettyNioAsyncHttpClient.builder()
                            .connectionTimeout(Duration.ofSeconds(60)).build())
                    .build();

            //AWS基于 CRT 的 S3 AsyncClient 实例用作 S3 传输管理器的底层客户端
            this.transferManager = S3TransferManager.builder().s3Client(this.client).build();

            // 创建 S3 配置对象
            S3Configuration config = S3Configuration.builder().chunkedEncodingEnabled(false)
                    .pathStyleAccessEnabled(isStyle).build();

            // 创建 预签名 URL 的生成器 实例，用于生成 S3 预签名 URL
            this.presigner = S3Presigner.builder()
                    .region(of())
                    .credentialsProvider(credentialsProvider)
                    .endpointOverride(URI.create(getDomain()))
                    .serviceConfiguration(config)
                    .build();
            oss = new OSSClientBuilder().build(properties.getEndpoint(), properties.getAccessKey(), properties.getSecretKey());
        } catch (Exception e) {
            if (e instanceof OssException) {
                throw e;
            }
            throw new OssException("配置错误! 请检查系统配置:[" + e.getMessage() + "]");
        }
    }

    /**
     * 上传文件到 Amazon S3，并返回上传结果
     *
     * @param filePath    本地文件路径
     * @param key         在 Amazon S3 中的对象键
     * @param md5Digest   本地文件的 MD5 哈希值（可选）
     * @param contentType 文件内容类型
     * @return UploadResult 包含上传后的文件信息
     * @throws OssException 如果上传失败，抛出自定义异常
     */
    public UploadResult upload(Path filePath, String key, String md5Digest, String contentType) {
        try {
            // 构建上传请求对象
            FileUpload fileUpload = transferManager.uploadFile(
                    x -> x.putObjectRequest(
                                    y -> y.bucket(properties.getBucketName())
                                            .key(key)
                                            .contentMD5(StringUtils.isNotEmpty(md5Digest) ? md5Digest : null)
                                            .contentType(contentType)
                                            // 用于设置对象的访问控制列表（ACL）。不同云厂商对ACL的支持和实现方式有所不同，
                                            // 因此根据具体的云服务提供商，你可能需要进行不同的配置（自行开启，阿里云有acl权限配置，腾讯云没有acl权限配置）
                                            //.acl(getAccessPolicy().getObjectCannedACL())
                                            .build())
                            .addTransferListener(LoggingTransferListener.create())
                            .source(filePath).build());

            // 等待上传完成并获取上传结果
            CompletedFileUpload uploadResult = fileUpload.completionFuture().join();
            String eTag = uploadResult.response().eTag();

            // 提取上传结果中的 ETag，并构建一个自定义的 UploadResult 对象
//            UploadResult uploadResult1 = new UploadResult();
//            uploadResult1.setUrl(getUrl() + "/" + key);
//            uploadResult1.setFilename(key);
//            uploadResult1.seteTag(eTag);
            return getResult(key, eTag);
        } catch (Exception e) {
            // 捕获异常并抛出自定义异常
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        } finally {
            // 无论上传是否成功，最终都会删除临时文件
            FileUtils.del(filePath);
        }
    }

    /**
     * 上传 InputStream 到 Amazon S3
     *
     * @param inputStream 要上传的输入流
     * @param key         在 Amazon S3 中的对象键
     * @param length      输入流的长度
     * @param contentType 文件内容类型
     * @return UploadResult 包含上传后的文件信息
     * @throws OssException 如果上传失败，抛出自定义异常
     */
    public UploadResult upload(InputStream inputStream, String key, Long length, String contentType) {
        // 如果输入流不是 ByteArrayInputStream，则将其读取为字节数组再创建 ByteArrayInputStream
        if (!(inputStream instanceof ByteArrayInputStream)) {
            inputStream = new ByteArrayInputStream(IoUtil.readBytes(inputStream));
        }
        try {
            // 创建异步请求体（length如果为空会报错）
            BlockingInputStreamAsyncRequestBody body = BlockingInputStreamAsyncRequestBody.builder()
                    .contentLength(length)
                    .subscribeTimeout(Duration.ofSeconds(30))
                    .build();

            // 使用 transferManager 进行上传
            Upload upload = transferManager.upload(
                    x -> x.requestBody(body)
                            .putObjectRequest(
                                    y -> y.bucket(properties.getBucketName())
                                            .key(key)
                                            .contentType(contentType)
                                            // 用于设置对象的访问控制列表（ACL）。不同云厂商对ACL的支持和实现方式有所不同，
                                            // 因此根据具体的云服务提供商，你可能需要进行不同的配置（自行开启，阿里云有acl权限配置，腾讯云没有acl权限配置）
                                            //.acl(getAccessPolicy().getObjectCannedACL())
                                            .build())
                            .build());

            // 将输入流写入请求体
            body.writeInputStream(inputStream);

            // 等待文件上传操作完成
            CompletedUpload uploadResult = upload.completionFuture().join();
            String eTag = uploadResult.response().eTag();

//            UploadResult uploadResult1 = new UploadResult();
//            uploadResult1.setUrl(getUrl() + SLASH + key);
//            uploadResult1.setFilename(key);
//            uploadResult1.seteTag(eTag);
            // 提取上传结果中的 ETag，并构建一个自定义的 UploadResult 对象
            return getResult(key, eTag);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }

    private UploadResult getResult(String objectKey, String eTag) {
//        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(properties.getBucketName(), objectKey);
//        req.setMethod(HttpMethod.GET); // 设置HTTP请求方法为GET，你也可以设置为PUT或其他HTTP方法，取决于你的需求。
//        Calendar ca = Calendar.getInstance();
//        ca.add(Calendar.DATE,30);
//        req.setExpiration(ca.getTime()); // 设置URL过期时间。
//        URL url = oss.generatePresignedUrl(req); // 生成URL。

        UploadResult uploadResult1 = new UploadResult();
        uploadResult1.setUrl(getUrl() + SLASH + objectKey);
        uploadResult1.setFileName(objectKey);
        uploadResult1.seteTag(eTag);
//        uploadResult1.setInvalidDate(ca.getTime());
        return uploadResult1;
    }

    /**
     * 下载文件从 Amazon S3 到临时目录
     *
     * @param path 文件在 Amazon S3 中的对象键
     * @return 下载后的文件在本地的临时路径
     * @throws OssException 如果下载失败，抛出自定义异常
     */
    public Path fileDownload(String path) {
        // 构建临时文件
        Path tempFilePath = FileUtils.createTempFile().toPath();
        // 使用 S3TransferManager 下载文件
        FileDownload downloadFile = transferManager.downloadFile(
                x -> x.getObjectRequest(
                                y -> y.bucket(properties.getBucketName())
                                        .key(removeBaseUrl(path))
                                        .build())
                        .addTransferListener(LoggingTransferListener.create())
                        .destination(tempFilePath)
                        .build());
        // 等待文件下载操作完成
        downloadFile.completionFuture().join();
        return tempFilePath;
    }

    /**
     * 下载文件从 Amazon S3 到 输出流
     *
     * @param key      文件在 Amazon S3 中的对象键
     * @param out      输出流
     * @param consumer 自定义处理逻辑
     * @return 输出流中写入的字节数（长度）
     * @throws OssException 如果下载失败，抛出自定义异常
     */
    public void download(String key, OutputStream out, Consumer<Long> consumer) {
        try {
            // 构建下载请求
            DownloadRequest<ResponseInputStream<GetObjectResponse>> downloadRequest = DownloadRequest.builder()
                    // 文件对象
                    .getObjectRequest(y -> y.bucket(properties.getBucketName())
                            .key(key)
                            .build())
                    .addTransferListener(LoggingTransferListener.create())
                    // 使用订阅转换器
                    .responseTransformer(AsyncResponseTransformer.toBlockingInputStream())
                    .build();
            // 使用 S3TransferManager 下载文件
            Download<ResponseInputStream<GetObjectResponse>> responseFuture = transferManager.download(downloadRequest);


            // 输出到流中
            try (ResponseInputStream<GetObjectResponse> responseStream = responseFuture.completionFuture().join().result()) { // auto-closeable stream
                if (consumer != null) {
                    consumer.accept(responseStream.response().contentLength());
                }
                copyStream(responseStream, out);
//                responseStream.transferTo(out); // 阻塞调用线程 blocks the calling thread
            }
        } catch (Exception e) {
            throw new OssException("文件下载失败，错误信息:[" + e.getMessage() + "]");
        }
    }

    // Java 8 流复制实现
    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
    }

    /**
     * 删除云存储服务中指定路径下文件
     *
     * @param path 指定路径
     */
    public void delete(String path) {
        try {
            client.deleteObject(
                    x -> x.bucket(properties.getBucketName())
                            .key(removeBaseUrl(path))
                            .build());
        } catch (Exception e) {
            throw new OssException("删除文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }

    /**
     * 获取私有URL链接
     *
     * @param objectKey   对象KEY
     * @param expiredTime 链接授权到期时间
     */
    public String getPrivateUrl(String objectKey, Duration expiredTime) {
        // 使用 AWS S3 预签名 URL 的生成器 获取对象的预签名 URL
        URL url = presigner.presignGetObject(
                        x -> x.signatureDuration(expiredTime)
                                .getObjectRequest(
                                        y -> y.bucket(properties.getBucketName())
                                                .key(objectKey)
                                                .build())
                                .build())
                .url();
        return url.toString();
    }

    /**
     * 获取私有URL链接
     *
     * @param objectKey 对象KEY
     * @param expired   链接授权到期时间
     */
    public String getOssUploadPath(String objectKey, Long expired) {
        Date expiration = new Date(new Date().getTime() + expired * 1000L);
//        if (StringUtils.isBlank(objectKey)){
        objectKey = SecurityUtils.getLoginUser().getUser().getUserId() +
                "_" + Calendar.getInstance().getTimeInMillis() + "." + FileUtils.getSuffix(objectKey);
//        }
        // 生成预签名URL。
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(properties.getBucketName(),
                objectKey, HttpMethod.PUT);
        request.getHeaders().put("Content-Type", "video/webm");
        // 设置过期时间。
        request.setExpiration(expiration);
        // 通过HTTP PUT请求生成预签名URL。
        URL signedUrl = oss.generatePresignedUrl(request);
        // 打印预签名URL。
        return signedUrl.toString();
    }

    public Map<String, String> getPostOssUploadPath(String objectKey, Long expired) {
        String key = SecurityUtils.getLoginUser().getUser().getUserId() +
                "_" + Calendar.getInstance().getTimeInMillis() + "." + FileUtils.getSuffix(objectKey);
        // 设置表单Map。
        Map<String, String> formFields = new LinkedHashMap<String, String>();
        // 设置文件名称。
        formFields.put("key", key);
        // 设置Content-Disposition。
        formFields.put("Content-Disposition", "attachment;filename=" + key);
        // 设置回调参数。
        // Callback callback = new Callback();
        // 设置回调服务器地址，例如http://oss-demo.oss-cn-hangzhou.aliyuncs.com:23450或http://127.0.0.1:9090。
        // callback.setCallbackUrl(callbackServerUrl);
        // 设置回调请求消息头中Host的值，如oss-cn-hangzhou.aliyuncs.com。
        // callback.setCallbackHost(callbackServerHost);
        // 设置发起回调时请求Body的值。
        // callback.setCallbackBody("{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}}");
        // 设置发起回调请求的Content-Type。
        // callback.setCalbackBodyType(Callback.CalbackBodyType.JSON);
        // 设置发起回调请求的自定义参数，由Key和Value组成，Key必须以x:开始，且必须小写。
        // callback.addCallbackVar("x:var1", "value1");
        // callback.addCallbackVar("x:var2", "value2");
        // 在表单Map中设置回调参数。
        // setCallBack(formFields, callback);
        // 设置OSSAccessKeyId。
        formFields.put("OSSAccessKeyId", properties.getAccessKey());

        long now = System.currentTimeMillis() / 1000;
        ZonedDateTime dtObj = ZonedDateTime.ofInstant(Instant.ofEpochSecond(now), ZoneId.of("UTC"));
        ZonedDateTime dtObjPlus3h = dtObj.plusSeconds(expired);
        //请求过期时间
        DateTimeFormatter expirationTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String expirationTime = dtObjPlus3h.format(expirationTimeFormatter);

        String policy = "{\"expiration\":\""+expirationTime+"\",\"conditions\":[{\"bucket\":\""+properties.getBucketName()+"\"}]}";
        String encodePolicy = new String(org.apache.commons.codec.binary.Base64.encodeBase64(policy.getBytes()));
        // 设置policy。
        formFields.put("policy", encodePolicy);
        // 生成签名。
        String signaturecom = com.aliyun.oss.common.auth.ServiceSignature.create().computeSignature(properties.getSecretKey(), encodePolicy);
        // 设置签名。
        formFields.put("Signature", signaturecom);
        return formFields;
    }

    public String convertVideo(String key) {
        String fileName = FileUtils.getNameNotSuffix(key) + ".mp4";
        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        // 构建视频处理样式字符串以及视频转码处理参数。
        String style = String.format("video/convert,f_mp4,vcodec_h264,fps_25,fpsopt_1,s_1280x");
        // 构建异步处理指令。
        String bucketEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(properties.getBucketName().getBytes());
        String targetEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(fileName.getBytes());
        String process = String.format("%s|sys/saveas,b_%s,o_%s", style, bucketEncoded, targetEncoded);
        // 创建AsyncProcessObjectRequest对象。
        AsyncProcessObjectRequest request = new AsyncProcessObjectRequest(properties.getBucketName(), key, process);
        // 执行异步处理任务。
        AsyncProcessObjectResult response = oss.asyncProcessObject(request);
        return fileName;
    }

    /**
     * 上传 byte[] 数据到 Amazon S3，使用指定的后缀构造对象键。
     *
     * @param data   要上传的 byte[] 数据
     * @param suffix 对象键的后缀
     * @return UploadResult 包含上传后的文件信息
     * @throws OssException 如果上传失败，抛出自定义异常
     */
    public UploadResult uploadSuffix(byte[] data, String suffix, String contentType) {
        return upload(new ByteArrayInputStream(data), getPath(properties.getPrefix(), suffix), Long.valueOf(data.length), contentType);
    }

    /**
     * 上传 InputStream 到 Amazon S3，使用指定的后缀构造对象键。
     *
     * @param inputStream 要上传的输入流
     * @param suffix      对象键的后缀
     * @param length      输入流的长度
     * @return UploadResult 包含上传后的文件信息
     * @throws OssException 如果上传失败，抛出自定义异常
     */
    public UploadResult uploadSuffix(InputStream inputStream, String suffix, Long length, String contentType) {
        return upload(inputStream, getPath(properties.getPrefix(), suffix), length, contentType);
    }

    /**
     * 上传文件到 Amazon S3，使用指定的后缀构造对象键
     *
     * @param file   要上传的文件
     * @param suffix 对象键的后缀
     * @return UploadResult 包含上传后的文件信息
     * @throws OssException 如果上传失败，抛出自定义异常
     */
    public UploadResult uploadSuffix(File file, String suffix) {
        return upload(file.toPath(), getPath(properties.getPrefix(), suffix), null, FileUtils.getMimeType(suffix));
    }

    /**
     * 获取文件输入流
     *
     * @param path 完整文件路径
     * @return 输入流
     */
    public InputStream getObjectContent(String path) throws IOException {
        // 下载文件到临时目录
        Path tempFilePath = fileDownload(path);
        // 创建输入流
        InputStream inputStream = Files.newInputStream(tempFilePath);
        // 删除临时文件
        FileUtils.del(tempFilePath);
        // 返回对象内容的输入流
        return inputStream;
    }

    /**
     * 获取 S3 客户端的终端点 URL
     *
     * @return 终端点 URL
     */
    public String getEndpoint() {
        // 根据配置文件中的是否使用 HTTPS，设置协议头部
        String header = getIsHttps();
        // 拼接协议头部和终端点，得到完整的终端点 URL
        return header + properties.getEndpoint();
    }

    /**
     * 获取 S3 客户端的终端点 URL（自定义域名）
     *
     * @return 终端点 URL
     */
    public String getDomain() {
        // 从配置中获取域名、终端点、是否使用 HTTPS 等信息
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getIsHttps();

        // 如果是云服务商，直接返回域名或终端点
        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            return StringUtils.isNotEmpty(domain) ? header + domain : header + endpoint;
        }

        // 如果是 MinIO，处理域名并返回
        if (StringUtils.isNotEmpty(domain)) {
            return domain.startsWith(Constants.HTTPS) || domain.startsWith(Constants.HTTP) ? domain : header + domain;
        }

        // 返回终端点
        return header + endpoint;
    }

    /**
     * 根据传入的 region 参数返回相应的 AWS 区域
     * 如果 region 参数非空，使用 Region.of 方法创建并返回对应的 AWS 区域对象
     * 如果 region 参数为空，返回一个默认的 AWS 区域（例如，us-east-1），作为广泛支持的区域
     *
     * @return 对应的 AWS 区域对象，或者默认的广泛支持的区域（us-east-1）
     */
    public Region of() {
        //AWS 区域字符串
        String region = properties.getRegion();
        // 如果 region 参数非空，使用 Region.of 方法创建对应的 AWS 区域对象，否则返回默认区域
        return StringUtils.isNotEmpty(region) ? Region.of(region) : Region.US_EAST_1;
    }

    /**
     * 获取云存储服务的URL
     *
     * @return 文件路径
     */
    public String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getIsHttps();
        // 云服务商直接返回
        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            return header + (StringUtils.isNotEmpty(domain) ? domain : properties.getBucketName() + "." + endpoint);
        }
        // MinIO 单独处理
        if (StringUtils.isNotEmpty(domain)) {
            // 如果 domain 以 "https://" 或 "http://" 开头
            return (domain.startsWith(Constants.HTTPS) || domain.startsWith(Constants.HTTP)) ?
                    domain + SLASH + properties.getBucketName() : header + domain + SLASH + properties.getBucketName();
        }
        return header + endpoint + SLASH + properties.getBucketName();
    }

    /**
     * 生成一个符合特定规则的、唯一的文件路径。通过使用日期、UUID、前缀和后缀等元素的组合，确保了文件路径的独一无二性
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 文件路径
     */
    public String getPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 生成日期路径
        String datePath = DateUtils.datePath();
        // 拼接路径
        String path = StringUtils.isNotEmpty(prefix) ?
                prefix + SLASH + datePath + SLASH + uuid : datePath + SLASH + uuid;
        return path + suffix;
    }

    /**
     * 移除路径中的基础URL部分，得到相对路径
     *
     * @param path 完整的路径，包括基础URL和相对路径
     * @return 去除基础URL后的相对路径
     */
    public String removeBaseUrl(String path) {
        return path.replace(getUrl() + SLASH, "");
    }

    /**
     * 服务商
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * 获取是否使用 HTTPS 的配置，并返回相应的协议头部。
     *
     * @return 协议头部，根据是否使用 HTTPS 返回 "https://" 或 "http://"
     */
    public String getIsHttps() {
        return OssConstant.IS_HTTPS.equals(properties.getIsHttps()) ? Constants.HTTPS : Constants.HTTP;
    }

    /**
     * 检查配置是否相同
     */
    public boolean checkPropertiesSame(OssProperties properties) {
        return this.properties.equals(properties);
    }

    /**
     * 获取当前桶权限类型
     *
     * @return 当前桶权限类型code
     */
    public AccessPolicyType getAccessPolicy() {
        return AccessPolicyType.getByType(properties.getAccessPolicy());
    }

}
