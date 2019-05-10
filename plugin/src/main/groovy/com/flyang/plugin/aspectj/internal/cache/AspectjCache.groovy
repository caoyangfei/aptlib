/*
 * Copyright 2018 firefly1126, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.gradle_plugin_android_aspectjx
 */
package com.flyang.plugin.aspectj.internal.cache

import com.android.builder.model.AndroidProject
import com.flyang.plugin.aspectj.AspectjExtension
import com.flyang.plugin.aspectj.internal.model.AspectjExtensionConfig
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import com.flyang.plugin.aspectj.internal.AspectjUtils

/**
 * class description here
 * @author simon
 * @version 1.0.0
 * @since 2018-04-03
 */
class AspectjCache {

    Project project
    String cachePath
    Map<String, VariantCache> variantCacheMap = new HashMap<>()

    String extensionConfigPath
    AspectjExtensionConfig aspectJExtensionConfig = new AspectjExtensionConfig()

    //for aspectj
    String encoding
    String bootClassPath
    String sourceCompatibility
    String targetCompatibility
    List<String> ajcArgs = new ArrayList<>()

    AspectjCache(Project proj) {
        this.project = proj

        init()
    }

    private void init() {
        cachePath = project.buildDir.absolutePath + File.separator + AndroidProject.FD_INTERMEDIATES + "/aspectJ"
        extensionConfigPath = cachePath + File.separator + "extensionconfig.json"

        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        //extension config
        File extensionConfig = new File(extensionConfigPath)
        if (extensionConfig.exists()) {
            aspectJExtensionConfig = AspectjUtils.optFromJsonString(FileUtils.readFileToString(extensionConfig), AspectjExtensionConfig.class)
        }

        if (aspectJExtensionConfig == null) {
            aspectJExtensionConfig = new AspectjExtensionConfig()
        }
    }

    File getCacheDir() {
        return new File(cachePath)
    }

    File getExtensionConfigFile() {
        return new File(extensionConfigPath)
    }

    void reset() {
        FileUtils.deleteDirectory(cacheDir)

        init()
    }

    void commit() {
        project.logger.debug("putExtensionConfig:${extensionConfigFile}")

        FileUtils.deleteQuietly(extensionConfigFile)

        File parent = extensionConfigFile.parentFile

        if (parent != null && !parent.exists()) {
            parent.mkdirs()
        }

        if (!extensionConfigFile.exists()) {
            extensionConfigFile.createNewFile()
        }

        String jsonString = AspectjUtils.optToJsonString(aspectJExtensionConfig)
        project.logger.debug("${jsonString}")
        FileUtils.write(extensionConfigFile, jsonString, "UTF-8")
    }

    void put(String variantName, VariantCache cache) {
        if (variantName != null && cache != null) {
            variantCacheMap.put(variantName, cache)
        }
    }

    boolean contains(String variantName) {
        if (variantName == null) {
            return false
        }

        return variantCacheMap.containsKey(variantName)
    }

    void putExtensionConfig(AspectjExtension extension) {
        if (extension == null) {
            return
        }

        aspectJExtensionConfig.enabled = extension.enabled
        aspectJExtensionConfig.ajcArgs = extension.ajcArgs
        aspectJExtensionConfig.includes = extension.includes
        aspectJExtensionConfig.excludes = extension.excludes
    }

    boolean isExtensionChanged(AspectjExtension extension) {
        if (extension == null) {
            return true
        }

        boolean isSourceIncludesExists = aspectJExtensionConfig.includes != null && !aspectJExtensionConfig.includes.isEmpty()
        boolean isTargetIncludeExists = extension.includes != null && !extension.includes.isEmpty()
        boolean isSourceExcludeExists = aspectJExtensionConfig.excludes != null && !aspectJExtensionConfig.excludes.isEmpty()
        boolean isTargetExcludeExists = extension.excludes != null && !extension.excludes.isEmpty()

        if ((!isSourceIncludesExists && isTargetIncludeExists)
            || (isSourceIncludesExists && !isTargetIncludeExists)
            || (!isSourceExcludeExists && isTargetExcludeExists)
            || (isSourceExcludeExists && !isTargetExcludeExists)) {
            return true
        }

        if ((!isSourceIncludesExists && !isTargetIncludeExists)
            && (!isSourceExcludeExists && !isTargetExcludeExists)) {
            return false
        }

        if (aspectJExtensionConfig.includes.size() != extension.includes.size()
            || aspectJExtensionConfig.excludes.size() != extension.excludes.size()) {
            return true
        }

        boolean isChanged = false
        aspectJExtensionConfig.includes.each {String source ->
            boolean targetMatched = false
            for (String target : extension.includes) {
                if (source == target) {
                    targetMatched = true
                    break
                }
            }

            if (!targetMatched) {
                isChanged = true
            }
        }

        aspectJExtensionConfig.excludes.each {String source ->
            boolean targetMatched = false
            for (String target : extension.excludes) {
                if (source == target) {
                    targetMatched = true
                    break
                }
            }

            if (!targetMatched) {
                isChanged = true
            }
        }

        return isChanged
    }
}
