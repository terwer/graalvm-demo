package com.terwergreen;

import org.graalvm.polyglot.Context;

public class JSContext {
    private static JSContext jsContext;
    private static Context context;

    public static synchronized JSContext getInstance() {
        if (jsContext == null) {
            long start = System.currentTimeMillis();
            jsContext = new JSContext();
            context = Context.newBuilder("js").allowAllAccess(true).build();
            long end = System.currentTimeMillis();
            System.out.printf("Init JSContext cost time {%s} ms\n", (end - start));
        }
        return jsContext;
    }

    private JSContext() {
    }

    public Context getContext() {
        return context;
    }
}
