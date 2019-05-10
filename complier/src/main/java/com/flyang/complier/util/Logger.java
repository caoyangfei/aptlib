package com.flyang.complier.util;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * {@link Messager} wrapper.
 * <p>
 * Created by Enyu Chen on 2017/6/13.
 */
public class Logger {
    private static Messager messager;
    private static Logger logger;

    public static void init(Messager messager) {
        if (logger == null) {
            logger = new Logger(messager);
        }
    }

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public static void info(CharSequence info) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, info);
        }
    }

    public static void info(Element element, CharSequence info) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.NOTE, info, element);
        }
    }

    public static void warn(CharSequence info) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.WARNING, info);
        }
    }

    public static void warn(Element element, CharSequence info) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.WARNING, info, element);
        }
    }

    public static void error(CharSequence info) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, info);
        }
    }

    public static void error(Element element, CharSequence info) {
        if (messager != null) {
            messager.printMessage(Diagnostic.Kind.ERROR, info, element);
        }
    }
}
