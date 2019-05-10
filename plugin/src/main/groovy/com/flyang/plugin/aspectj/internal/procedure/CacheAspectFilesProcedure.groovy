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
package com.flyang.plugin.aspectj.internal.procedure

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.flyang.plugin.aspectj.internal.cache.VariantCache
import com.flyang.plugin.aspectj.internal.concurrent.BatchTaskScheduler
import com.flyang.plugin.aspectj.internal.concurrent.ITask
import com.google.common.io.ByteStreams
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import com.flyang.plugin.aspectj.internal.AspectjUtils

/**
 * class description here
 * @author simon
 * @version 1.0.0
 * @since 2018-04-23
 */
class CacheAspectFilesProcedure extends AbsProcedure {
    CacheAspectFilesProcedure(Project project, VariantCache variantCache, TransformInvocation transformInvocation) {
        super(project, variantCache, transformInvocation)
    }

    @Override
    boolean doWorkContinuously() {
        project.logger.debug("~~~~~~~~~~~~~~~~~~~~cache aspect files")
        //缓存aspect文件
        BatchTaskScheduler batchTaskScheduler = new BatchTaskScheduler()

        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->
//                    collect aspect file
                batchTaskScheduler.addTask(new ITask() {
                    @Override
                    Object call() throws Exception {
                        dirInput.file.eachFileRecurse { File item ->
                            if (AspectjUtils.isAspectClass(item)) {
                                project.logger.debug("~~~~~~~~~~~~collect aspect file:${item.absolutePath}")
                                String path = item.absolutePath
                                String subPath = path.substring(dirInput.file.absolutePath.length())
                                File cacheFile = new File(variantCache.aspectPath + subPath)
                                variantCache.add(item, cacheFile)
                            }
                        }

                        return null
                    }
                })
            }

            input.jarInputs.each { JarInput jarInput ->
//                    collect aspect file
                batchTaskScheduler.addTask(new ITask() {
                    @Override
                    Object call() throws Exception {
                        JarFile jarFile = new JarFile(jarInput.file)
                        Enumeration<JarEntry> entries = jarFile.entries()
                        while (entries.hasMoreElements()) {
                            JarEntry jarEntry = entries.nextElement()
                            String entryName = jarEntry.getName()
                            if (!jarEntry.isDirectory() && AspectjUtils.isClassFile(entryName)) {
                                byte[] bytes = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                                File cacheFile = new File(variantCache.aspectPath + File.separator + entryName)
                                if (AspectjUtils.isAspectClass(bytes)) {
                                    project.logger.debug("~~~~~~~~~~~collect aspect file:${entryName}")
                                    variantCache.add(bytes, cacheFile)
                                }
                            }
                        }

                        jarFile.close()

                        return null
                    }
                })
            }
        }

        batchTaskScheduler.execute()

        if (AspectjUtils.countOfFiles(variantCache.aspectDir) == 0) {
            AspectjUtils.doWorkWithNoAspectj(transformInvocation)
            return false
        }

        return true
    }
}
