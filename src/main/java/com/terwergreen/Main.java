package com.terwergreen;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Main {
    private final Object promiseLock = new Object();
    private volatile boolean promiseResolved = false;
    private volatile boolean promiseRejected = false;
    private Object renderedObject = null;

    private Consumer<Object> fnResolve = object -> {
        synchronized (promiseLock) {
            renderedObject = object;
            promiseResolved = true;
            System.out.println("fnResolve=>promiseResolved");
        }
    };

    private Consumer<Object> fnRejected = object -> {
        synchronized (promiseLock) {
            renderedObject = object;
            promiseRejected = true;
            System.out.println("fnRejected=>promiseRejected");
        }
    };

    private void execute(Map<String, Object> httpContext) {
        try (Context context = JSContext.getInstance().getContext()) {
            // 编译entry-server
            String entryServer = readResource("entry-server.js");
            context.eval("js", entryServer);

            // renderServer
            String source = "(async function() { " +
                    "const context = " + JSON.toJSONString(httpContext) + ";" +
                    "return global.renderServer(context);" +
                    " });";
            Value eval = context.eval("js", source);
            eval.execute().invokeMember("then", fnResolve, fnRejected);

            int i = 0;
            int jsWaitTimeout = 1000 * 2;
            int interval = 200; // 等待时间间隔
            int totalWaitTime = 0; // 实际等待时间

            if (!promiseResolved) {
                while (!promiseResolved && totalWaitTime < jsWaitTimeout) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        System.out.println("Thread error:" + e.getMessage());
                    }
                    totalWaitTime = totalWaitTime + interval;
                    if (interval < 500) interval = interval * 2;
                    i = i + 1;
                }

                if (!promiseResolved) {
                    System.out.println("time is out");
                } else {
                    System.out.println("cost time to resolve:" + totalWaitTime);
                }
            } else {
                System.out.println("promise already resolved");
            }

            System.out.println("renderedObject = " + renderedObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();

        // 设置路由上下文
        Map<String, Object> httpContext = new HashMap<>();
        httpContext.put("url", "/");

        main.execute(httpContext);
    }

    /**
     * 读取脚本
     *
     * @param resourceName 脚本名称
     * @return 脚本字符
     */
    public static String readResource(String resourceName) {
        String result = null;
        try {
            result = Resources.toString(Main.class.getClassLoader().getResource(resourceName), Charsets.UTF_8);
        } catch (Exception e) {
            System.out.println("文件读取失败:" + resourceName + "," + e.getMessage());
        }
        return result;
    }
}
