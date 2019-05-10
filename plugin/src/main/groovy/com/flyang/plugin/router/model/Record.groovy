package com.flyang.plugin.router.model

import com.google.common.collect.ImmutableList

/**
 * Created by chenenyu on 2018/7/26.
 */
class Record {

    static final String TEMPLATE_ROUTE_TABLE = "com/flyang/api/router/template/RouteTable"
    static final String TEMPLATE_INTERCEPTOR_TABLE = "com/flyang/api/router/template/InterceptorTable"
    static final String TEMPLATE_TARGET_INTERCEPTORS_TABLE = "com/flyang/api/router/template/TargetInterceptorsTable"

    static final List<Record> records = ImmutableList.of(
            new Record(TEMPLATE_ROUTE_TABLE),
            new Record(TEMPLATE_INTERCEPTOR_TABLE),
            new Record(TEMPLATE_TARGET_INTERCEPTORS_TABLE))

    static final String REGISTER_CLASS_NAME = "com/flyang/api/router/AptHub.class"
    static final String APT_CLASS_PACKAGE_NAME = "com/flyang/router"

    static final Set<String> excludeJar = ["com.android.support", "android.arch.", "androidx."]


    String templateName

    Set<String> aptClasses = []

    Record(String templateName) {
        this.templateName = templateName
    }
}
