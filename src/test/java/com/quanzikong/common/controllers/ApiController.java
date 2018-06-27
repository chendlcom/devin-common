package com.quanzikong.common.controllers;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import com.quanzikong.common.BizResult;
import com.quanzikong.common.to.TestPojo;
import com.quanzikong.common.utils.HessianUtil;
import com.quanzikong.common.utils.RestApiUtil;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiController {
    @RequestMapping(value = "/testget", method = {RequestMethod.GET})
    public BizResult testget(HttpServletRequest request, int one, String two) {
        BizResult rst = new BizResult();
        rst.xPut("testMsg", "Test RestfulAPI");

        TestPojo testPojo = new TestPojo();
        testPojo.setOne(one);
        testPojo.setTwo(two);
        rst.xPut("src", testPojo);

        return rst.toSuccess("OK");
    }

    @RequestMapping(value = "/testdefault", method = {RequestMethod.POST})
    public BizResult testDefault(int one, String two) {
        BizResult rst = new BizResult();
        rst.xPut("testMsg", "Test RestfulAPI");

        TestPojo testPojo = new TestPojo();
        testPojo.setOne(one);
        testPojo.setTwo(two);
        rst.xPut("src", testPojo);

        return rst.toSuccess("OK");
    }

    @RequestMapping(value = "/testdefaultb", method = {RequestMethod.POST})
    public BizResult testDefaultb(TestPojo testPojo) {
        BizResult rst = new BizResult();
        rst.xPut("testMsg", "Test RestfulAPI");
        rst.xPut("src", testPojo);

        return rst.toSuccess("OK");
    }

    @RequestMapping(value = "/testapplicationjson", method = {RequestMethod.POST})
    public BizResult testApplicationJson(@RequestBody TestPojo testPojo) {
        BizResult rst = new BizResult();
        rst.xPut("testMsg", "Test RestfulAPI");
        rst.xPut("src", testPojo);

        return rst.toSuccess("OK");
    }

    @RequestMapping(value = "/testapplicationjsonb", method = {RequestMethod.POST})
    public BizResult testApplicationJsonb(TestPojo testPojo) {
        BizResult rst = new BizResult();
        rst.xPut("testMsg", "Test RestfulAPI");
        rst.xPut("src", testPojo);

        return rst.toSuccess("OK");
    }

    @RequestMapping(
        value = "/testoctetstream",
        method = {RequestMethod.POST},
        consumes = {MediaType.APPLICATION_OCTET_STREAM_VALUE}
    )
    public BizResult testOctetStream(HttpServletRequest request) throws Exception {
        // header fields
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            System.out.println("headerName - " + s + " = " + request.getHeader(s));
        }

        // object
        byte[] bytes = RestApiUtil.getRequestBytes(request);
        TestPojo testPojo = HessianUtil.deserialize(bytes, TestPojo.class);

        return new BizResult().toSuccess("OK").xPut("src", testPojo);
    }

    @RequestMapping(
        value = "/testmultipart",
        method = {RequestMethod.POST},
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public BizResult testMultiPart(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        BizResult rst = new BizResult(true, "OK");

        // header fields
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            System.out.println("headerName - " + s + " = " + request.getHeader(s));
        }

        // parameters
        JSONObject o = new JSONObject();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String s = parameterNames.nextElement();
            String v = request.getParameter(s);
            o.put(s, v);
            System.out.println("parameterName - " + s + " = " + v);
        }

        // files
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        Iterator<Entry<String, List<MultipartFile>>> it = multiFileMap.entrySet().iterator();
        MultipartFile multipartFile;
        while (it.hasNext()) {
            Iterator<MultipartFile> iterator = it.next().getValue().iterator();
            while (iterator.hasNext()) {
                multipartFile = iterator.next();
                byte[] bytes = multipartFile.getBytes();
                InputStream inputStream = multipartFile.getInputStream();

                String originalFilename = multipartFile.getOriginalFilename();
                System.out.println("original filename = " + originalFilename);

                rst.put(originalFilename, originalFilename);
            }
        }

        return rst.xPut("src", o);
    }
}
