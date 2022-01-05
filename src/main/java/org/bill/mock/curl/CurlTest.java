package org.bill.mock.curl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

public class CurlTest {
    public static void main(String[] args) {

        String nacosMetaProt = "http://";
        String nacosMetaHttpProt = "put";
        String nacosMetaUrl = "/nacos/v1/ns/instance/metadata/batch";
        nacosMetaUrl = nacosMetaProt + "10.18.225.114:8848" + nacosMetaUrl;

        JSONObject variables = new JSONObject();
        variables.put("namespaceId", "ssc");
        variables.put("serviceName", "srm-store");

        JSONArray instantsList = new JSONArray();   //实例ip
        JSONObject instantsOne = new JSONObject();
        instantsOne.put("ip", "10.42.14.206");
        instantsOne.put("port", "25000");
        instantsOne.put("clusterName", "DEFAULT");
        instantsList.add(instantsOne);
        variables.put("instances", instantsList.toJSONString());

        JSONObject metaDataOne = new JSONObject();
        metaDataOne.put("preserved.heart.beat.interval", "1000");
        // 设置心跳超时时间，单位为秒，这里将心跳超时时间设为500毫秒
        // 即服务端6秒收不到客户端心跳，会将该客户端注册的实例设为不健康：
        metaDataOne.put("preserved.heart.beat.timeout", "500");
        // 设置实例删除的超时时间，单位为秒，这里将实例删除超时时间设为500毫秒，
        // 即服务端9秒收不到客户端心跳，会将该客户端注册的实例删除：
        metaDataOne.put("preserved.ip.delete.timeout", "500");
        variables.put("metadata", metaDataOne.toJSONString());

        System.out.println("nacosDown rest , nacosMetaUrl:{} , variables: {} == " +  nacosMetaUrl + "==" + asUrlVariables(variables));

        String result = RestUtil(nacosMetaUrl, variables, nacosMetaHttpProt);
        System.out.println("result : " + result);


    }


    /**
     * <pre>
     *
     * 模拟curl， Core的RestUtil跑不通，自己撸了个curl
     *
     * 报文demo
     * JSONObject variables = new JSONObject();
     *         variables.put("namespaceId", nameSpace);
     *         variables.put("serviceName", serviceName);
     *
     *         JSONArray instantsList = new JSONArray();   //实例ip
     *         JSONObject instantsOne = new JSONObject();
     *         instantsOne.put("ip", ip);
     *         instantsOne.put("port", port);
     *         instantsOne.put("clusterName", clusterName);
     *         instantsList.add(instantsOne);
     *         variables.put("instances", instantsList.toJSONString());
     *
     *
     * </pre>
     * @param url 链接
     * @param variables 参数
     * @param METHOD 如put,get,post等
     * @return
     */
    public static String RestUtil(String url, JSONObject variables, String METHOD) {

//        String parm = "\"[\\\"default\\\",\\\"ASDSFGSDGRRGSSSSS\\\"]\"";
        String[] cmds = {"curl", "-X", METHOD, url, "-d", " " + asUrlVariables(variables)};
//        log.info("nacosDown rest , rest : {}, ", cmds.toString());
        ProcessBuilder process = new ProcessBuilder(cmds);
        StringBuilder builder;
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static String asUrlVariables(JSONObject variables) {

        Map<String, Object> source = variables.getInnerMap();
        Iterator<String> it = source.keySet().iterator();

        StringBuilder urlVariables;
        String key;
        String value;
        for(urlVariables = new StringBuilder(); it.hasNext(); urlVariables.append("&").append(key).append("=").append(value)) {
            key = (String)it.next();
            value = "";
            Object object = source.get(key);
            if (object != null && object.toString().length() != 0) {
                value = object.toString();
            }
        }

        return urlVariables.substring(1);
    }


}
