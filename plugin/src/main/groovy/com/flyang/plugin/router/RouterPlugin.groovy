package com.flyang.plugin.router

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.TestPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.plugins.ExtraPropertiesExtension

/**
 * @author yangfei.cao
 * @ClassName RouterPlugin
 * @date 2019/4/26
 * ------------- Description -------------
 * 插件入口
 */
class RouterPlugin implements Plugin<Project> {
    static final String APT_OPTION_NAME = "moduleName"

    String apiVersion = "1.1.1.2019_04"
    String annotationVersion = "1.1.1.2019_04"
    String complierVersion = "1.1.1.2019_04"
    boolean isNexus = false   //是否是本地nexus库，默认不是

    String androidBuildGradleVersion

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)                                // AppPlugin
                && !project.plugins.hasPlugin(LibraryPlugin)                     // LibraryPlugin
                && !project.plugins.hasPlugin(TestPlugin)                        // TestPlugin
                && !project.plugins.hasPlugin("com.android.instantapp")       // InstantAppPlugin, added in 3.0
                && !project.plugins.hasPlugin("com.android.feature")          // FeaturePlugin, added in 3.0
                && !project.plugins.hasPlugin("com.android.dynamic-feature")) // DynamicFeaturePlugin, added in 3.2
        {
            throw new GradleException("android plugin required.")
        }

        project.rootProject.buildscript.configurations.each {
            if (it.name == ScriptHandler.CLASSPATH_CONFIGURATION) { // classpath
                it.resolvedConfiguration.firstLevelModuleDependencies.each {
                    // println("${it.moduleGroup}:${it.moduleName}:${it.moduleVersion}")
                    if (it.moduleGroup == "com.android.tools.build" && it.moduleName == "gradle") {
                        androidBuildGradleVersion = it.moduleVersion
                    }
                }
            }
        }
        if (!androidBuildGradleVersion) {
            throw new IllegalArgumentException("Unknown android build gradle plugin version.")
        }

        // kotlin project ?
        def isKotlinProject = project.plugins.hasPlugin('kotlin-android')
        if (isKotlinProject) {
            if (!project.plugins.hasPlugin('kotlin-kapt')) {
                project.plugins.apply('kotlin-kapt')
            }
        }

        String compileConf = 'compile'
        if (is3_xVersion()) {
            compileConf = 'implementation'
        }
        String aptConf = 'annotationProcessor'
        if (isKotlinProject) {
            aptConf = 'kapt'
        }

        // 添加依赖库
        Project apiProject = project.rootProject.findProject("api")
        Project compilerProject = project.rootProject.findProject("complier")
        Project annotationProject = project.rootProject.findProject("annotation")
        Project aopProject = project.rootProject.findProject("aop")
        if (apiProject && compilerProject && annotationProject && aopProject) {
            //本地
            project.dependencies.add(compileConf, apiProject)
            project.dependencies.add(compileConf, annotationProject)
            project.dependencies.add(compileConf, aopProject)
            project.dependencies.add(aptConf, compilerProject)
        } else {
            ExtraPropertiesExtension ext = project.rootProject.ext

            if (ext.has("isNexus")) {
                isNexus = ext.get("isNexus")
            }
            if (ext.has("apiVersion")) {
                apiVersion = ext.get("apiVersion")
            }
            if (ext.has("annotationVersion")) {
                annotationVersion = ext.get("annotationVersion")
            }
            if (ext.has("complierVersion")) {
                complierVersion = ext.get("complierVersion")
            }

            if (isNexus) {
                //本地
                project.dependencies.add(compileConf,
                        "com.flyang.common:api:${apiVersion}")
                project.dependencies.add(compileConf,
                        "com.flyang.common:annotation:${annotationVersion}")
                project.dependencies.add(aptConf,
                        "com.flyang.common:complier:${complierVersion}@jar")
            } else {
                //GitHub
                project.dependencies.add(compileConf,
                        "com.github.caoyangfei.aptlib:api:${apiVersion}")
                project.dependencies.add(compileConf,
                        "com.github.caoyangfei.aptlib:annotation:${annotationVersion}")
                project.dependencies.add(aptConf,
                        "com.github.caoyangfei.aptlib:complier:${complierVersion}@jar")
            }
        }

        def android = project.extensions.findByName("android")
        if (android) {
            android.defaultConfig.javaCompileOptions.annotationProcessorOptions.argument(APT_OPTION_NAME, project.name)
            android.productFlavors.all {
                it.javaCompileOptions.annotationProcessorOptions.argument(APT_OPTION_NAME, project.name)
            }
        }

        //AppPlugin   判断动态注入
        if (project.plugins.hasPlugin(AppPlugin)) {
            def transform = new RouterTransform(project)
            android.registerTransform(transform)
        }
    }

    //gradle版本是不是大于3.0
    boolean is3_xVersion() {
        return androidBuildGradleVersion.split("\\.")[0].toInteger() >= 3
    }
}
