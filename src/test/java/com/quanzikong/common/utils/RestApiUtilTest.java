package com.quanzikong.common.utils;

import java.io.File;

import com.alibaba.fastjson.JSONObject;

import com.quanzikong.common.Application;
import com.quanzikong.common.BizResult;
import com.quanzikong.common.enums.HttpContentType;
import com.quanzikong.common.to.TestPojo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * RestApiUtilTest
 *
 * @since 2018-06-26 19:03
 */
public class RestApiUtilTest {
    private ConfigurableApplicationContext context;

    @Before
    public void setUp() throws Exception {
        context = SpringApplication.run(Application.class, new String[] {});
    }

    @After
    public void after() {
        SpringApplication.exit(context);
    }

    @Test
    public void testget() throws Exception {
        String url = "http://localhost:8090/api/testget";

        String rsp = RestApiUtil.newInstance(url).asGet()
            .addParam("one", 1)
            .addParam("two", "2")
            .getResponseString();

        showLog(rsp);
        BizResult rst = JSONObject.parseObject(rsp, BizResult.class);

        assertTrue(rst.isSuccess());
        assertEquals(Integer.valueOf(1), rst.getJSONObject("src").getInteger("one"));
    }

    public BizResult postDefault(String url) throws Exception {
        String rsp = RestApiUtil.newInstance(url).asPost()
            .addParam("one", 1)
            .addParam("two", "2")
            .getResponseString();
        showLog(rsp);
        BizResult rst = JSONObject.parseObject(rsp, BizResult.class);

        assertTrue(rst.isSuccess());
        assertEquals(Integer.valueOf(1), rst.getJSONObject("src").getInteger("one"));

        return rst;
    }

    @Test
    public void testdefault() throws Exception {
        String url = "http://localhost:8090/api/testdefault";
        postDefault(url);
    }

    @Test
    public void testdefaultb() throws Exception {
        String url = "http://localhost:8090/api/testdefaultb";
        postDefault(url);
    }

    public BizResult postApplicationJson(String url) throws Exception {
        String rsp = RestApiUtil.newInstance(url).asPost()
            .setContentType(HttpContentType.APPLICATION_JSON)
            .addParam("one", 1)
            .addParam("two", "2")
            .getResponseString();
        showLog(rsp);
        BizResult rst = JSONObject.parseObject(rsp, BizResult.class);

        assertTrue(rst.isSuccess());
        return rst;
    }

    @Test
    public void testapplicationjson() throws Exception {
        String url = "http://localhost:8090/api/testapplicationjson";
        BizResult rst = postApplicationJson(url);
        assertEquals(Integer.valueOf(1), rst.getJSONObject("src").getInteger("one"));
    }

    @Test
    public void testapplicationjsonb() throws Exception {
        String url = "http://localhost:8090/api/testapplicationjsonb";
        BizResult rst = postApplicationJson(url);
        assertEquals(Integer.valueOf(0), rst.getJSONObject("src").getInteger("one"));
    }

    @Test
    public void testapplicationjsonc() throws Exception {
        String url = "http://localhost:8090/api/testapplicationjsonc";
        String rsp = RestApiUtil.newInstance(url).asPost()
            .setContentType(HttpContentType.APPLICATION_JSON)
            .setPostPojo(ArrayUtil.asStrList("aa", "bb"))
            .getResponseString();
        showLog(rsp);
        BizResult rst = JSONObject.parseObject(rsp, BizResult.class);

        assertTrue(rst.isSuccess());
        assertEquals("aa", rst.getJSONArray("src").getString(0));
    }

    @Test
    public void testoctetstream() throws Exception {
        String url = "http://localhost:8090/api/testoctetstream";
        TestPojo testPojo = new TestPojo();
        testPojo.setOne(1);
        testPojo.setTwo("2");

        String rsp = RestApiUtil.newInstance(url)
            .addHeaderField("x-api-key", "testKey")
            .asPost().setContentType(HttpContentType.APPLICATION_OCTET_STREAM)
            .setPostPojo(HessianUtil.serialize(testPojo))
            .getResponseString();
        showLog(rsp);
        BizResult rst = JSONObject.parseObject(rsp, BizResult.class);

        assertTrue(rst.isSuccess());
        assertEquals(Integer.valueOf(1), rst.getJSONObject("src").getInteger("one"));
    }

    @Test
    public void testmultipart() throws Exception {
        String path = ClassUtil.getClasspath();
        String fpath1 = path + "/za.txt";
        String fpath2 = path + "/zb.txt";

        String url = "http://localhost:8090/api/testmultipart";
        String rsp = RestApiUtil.newInstance(url)
            .addHeaderField("x-api-key", "%^&*HJKdsf")
            .asPost().setContentType(HttpContentType.MULTIPART_FORM_DATA)
            .addParam("one", 1)
            .addParam("two", "2")
            .addFilePart("filea", new File(fpath1))
            .addFilePart("fileb", new File(fpath2))
            .getResponseString();

        showLog(rsp);
        BizResult rst = JSONObject.parseObject(rsp, BizResult.class);

        assertTrue(rst.isSuccess());
        assertEquals(Integer.valueOf(1), rst.getJSONObject("src").getInteger("one"));
        assertEquals("2", rst.getJSONObject("src").getString("two"));
        assertEquals("filea", rst.getString("filea"));
        assertEquals("fileb", rst.getString("fileb"));
    }

    //@Test // test succeed 2018-06-27 14:45:01
    public void testRemouteHttps() throws Exception {
        String url = "https://remoteIp:port/";

        String rsp = RestApiUtil.newInstance(url).asGet()
            .addParam("one", 1)
            .addParam("two", "2")
            .getResponseString();

        showLog(rsp);
    }

    private void showLog(String logs) {
        System.out.println(String.format(""
            + "\n-------------------------------------------------------------------------------------\n"
            + "%s"
            + "\n-------------------------------------------------------------------------------------\n", logs));
    }
}
