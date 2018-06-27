package com.quanzikong.common.enums;

/**
 * HttpContentType
 */
public enum HttpContentType {
    // name code Description
    /**
     * DEFAULT
     *
     * Client
     * String url = "http{s}://ip:port/xxx/yyy";
     * String rsp = RestApiUtil.newInstance(url).asPost()
     * .addParam("one", 1)
     * .addParam("two", "2")
     * .getResponseString();
     *
     * Server
     * 对于SpringBoot的微服务，不设置Content-Type，后端可以以下两种方式接受参数：
     *
     * @RequestMapping(value = "/testapplicationjson", method = {RequestMethod.POST})
     * public String testApplicationJson(int one, String two){
     * // you business
     * return "OK";
     * }
     * @RequestMapping(value = "/testapplicationjson", method = {RequestMethod.POST})
     * public String testApplicationJson(TestPojo testPojo){
     * // you business
     * return "OK";
     * }
     * 以上方式参数对象前切记不能加@RequestBody
     */
    DEFAULT("", "default"),
    /**
     * APPLICATION_JSON
     *
     * Client
     * String url = "http{s}://ip:port/xxx/yyy";
     * String rsp = RestApiUtil.newInstance(url).asPost()
     * .setContentType(HttpContentType.APPLICATION_JSON)
     * .addParam("one", 1)
     * .addParam("two", "2")
     * .getResponseString();
     *
     * Server
     * 对于SpringBoot的微服务，如果设置了Content-Type为application/json，后端必须以对象接受参数，且必须加@RequestBody，如：
     *
     * @RequestMapping(value = "/testapplicationjson", method = {RequestMethod.POST})
     * public String testApplicationJson(@RequestBody TestPojo testPojo){
     * // you business
     * return "OK";
     * }
     * 否则testPojo的属性都为创建对象的默认值，无法接收到客服端发送的参数
     */
    APPLICATION_JSON("application/json", "json"),
    /**
     * APPLICATION_OCTET_STREAM
     *
     * 以对象二进制流的形式，通过http发送经过Hessian序列化后的对象（通过<code>RestApiUtil.setPostPojo</code>设置）
     * 服务端要获得客户端传递的对象，需通过给定工具进行反序列化（工具中会默认使用Hessian进行反序列化）
     * Usage:
     * 1 Client
     * String url = "http{s}://ip:port/xxx/yyy";
     * TestPojo testPojo = new TestPojo();
     * testPojo.setOne(1);
     * testPojo.setTwo("2");
     *
     * String rsp = RestApiUtil.newInstance(url)
     * .addHeaderField("x-api-key", "testKey")
     * .asPost().setContentType(HttpContentType.APPLICATION_OCTET_STREAM)
     * .setPostPojo(testPojo, TestPojo.class)
     * .getResponseString();
     *
     * 2 Server
     * public String testOctetStream(HttpServletRequest request) throws Exception
     * {
     * // header fields
     * Enumeration<String> headerNames = request.getHeaderNames();
     * while (headerNames.hasMoreElements())
     * {
     * String s = headerNames.nextElement();
     * System.out.println("headerName - " + s + " = " + request.getHeader(s));
     * }
     *
     * // object
     * TestPojo testPojo = RestApiUtil.getResponseObject(request, TestPojo.class);
     *
     * return "OK";
     * }
     */
    APPLICATION_OCTET_STREAM("application/octet-stream", "binary stream"),
    /**
     * MULTIPART_FORM_DATA
     *
     * 可同时发送多个参数、多个文件
     * Usage:
     * 1 Client
     * String url = "http{s}://ip:port/xxx/yyy";
     * String fpath1 = "/filePath/file1.jpg";
     * String fpath2 = "/filePath/file2.txt";
     * String rsp = RestApiUtil.newInstance(url)
     * .addHeaderField("x-api-key", "testKey")
     * .asPost().setContentType(HttpContentType.MULTIPART_FORM_DATA)
     * .addParam("one", 1)
     * .addParam("two", "2")
     * .addFilePart("filea", new File(fpath1))
     * .addFilePart("fileb", new File(fpath2))
     * .getResponseString();
     *
     * 2 Server
     * public String testMultiPart(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception
     * {
     * // header fields
     * Enumeration<String> headerNames = request.getHeaderNames();
     * while (headerNames.hasMoreElements())
     * {
     * String s = headerNames.nextElement();
     * System.out.println("headerName - " + s + " = " + request.getHeader(s));
     * }
     *
     * // parameters
     * Enumeration<String> parameterNames = request.getParameterNames();
     * while (parameterNames.hasMoreElements())
     * {
     * String s = parameterNames.nextElement();
     * System.out.println("parameterName - " + s + " = " + request.getParameter(s));
     * }
     *
     * // files
     * MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
     * Iterator<Entry<String, List<MultipartFile>>> it = multiFileMap.entrySet().iterator();
     * MultipartFile multipartFile;
     * while (it.hasNext())
     * {
     * Iterator<MultipartFile> iterator = it.next().getValue().iterator();
     * while (iterator.hasNext())
     * {
     * multipartFile = iterator.next();
     *
     * String originalFilename = multipartFile.getOriginalFilename();
     * System.out.println("original filename = " + originalFilename);
     *
     * byte[] bytes = multipartFile.getBytes();
     * InputStream inputStream = multipartFile.getInputStream();
     * }
     * }
     *
     * return "OK";
     * }
     */
    MULTIPART_FORM_DATA("multipart/form-data", "text,file"),
    // end
    ;

    /**
     * 枚举编码
     */
    private final String code;

    /**
     * 描述说明
     */
    private final String description;

    /**
     * 默认构造器
     *
     * @param code        枚举编码
     * @param description 描述说明
     */
    private HttpContentType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code 枚举编码
     *
     * @return 状态枚举
     */
    public static HttpContentType getByCode(String code) {
        for (HttpContentType item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 通过枚举<code>description</code>获得枚举
     *
     * @param description 枚举编码
     *
     * @return 状态枚举
     */
    public static HttpContentType getByDescription(String description) {
        for (HttpContentType item : values()) {
            if (item.getDescription().equals(description)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
