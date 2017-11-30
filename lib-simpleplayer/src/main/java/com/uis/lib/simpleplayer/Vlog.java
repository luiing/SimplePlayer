package com.uis.lib.simpleplayer;

import android.util.Log;

/**
 * Created by lhb on 2017/4/25.
 */

public class Vlog {

    private static boolean debug = true;//BuildConfig.DEBUG;

    public static void enableDebug(){
        debug = true;
    }

    public static void disableDebug(){
        debug = false;
    }

    public static void e(String tag,Object log){
        if(debug) {
            Log.e(tag, getMessage(log,false));
        }
    }

    public static void i(String tag,Object log){
        if(debug) {
            Log.i(tag, getMessage(log,false));
        }
    }

    public static void a(String tag,Object log){
        if(debug) {
            Log.w(tag, getMessage(log,true));
        }
    }

    static String getMessage(Object log,boolean isAll){
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        StackTraceElement stack = stacks[4];
        builder.append(stack.getFileName())
                .append("(")
                .append(stack.getLineNumber())
                .append(")")
                .append(stack.getMethodName())
                .append(":")
                .append(log)
                .append("\n");
        if(isAll) {
            for (StackTraceElement e : stacks) {
                builder.append(e.getFileName())
                        .append("(")
                        .append(e.getLineNumber())
                        .append(")")
                        .append(e.getMethodName())
                        .append("\n");
            }
        }
        return builder.toString();
    }

}
