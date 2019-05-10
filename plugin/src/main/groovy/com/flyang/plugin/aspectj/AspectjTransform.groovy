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
package com.flyang.plugin.aspectj

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.flyang.plugin.aspectj.internal.cache.VariantCache
import com.flyang.plugin.aspectj.internal.procedure.*
import com.google.common.collect.ImmutableSet
import org.gradle.api.Project

/**
 * class description here
 * @author simon
 * @version 1.0.0
 * @since 2018-03-12
 */
class AspectjTransform extends Transform {

    AspectjProcedure aspectJProcedure

    AspectjTransform(Project proj) {
        aspectJProcedure = new AspectjProcedure(proj)
    }

    @Override
    String getName() {
        return "aspectJ"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.<QualifiedContent.ContentType>of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //是否支持增量编译
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        Project project = aspectJProcedure.project

        TransformTask transformTask = (TransformTask)transformInvocation.context
        VariantCache variantCache = new VariantCache(aspectJProcedure.project, aspectJProcedure.aspectJCache, transformTask.variantName)

        aspectJProcedure.with(new CheckAspectJXEnableProcedure(project, variantCache, transformInvocation))

        if (transformInvocation.incremental) {
            //incremental build
            aspectJProcedure.with(new UpdateAspectFilesProcedure(project, variantCache, transformInvocation))
            aspectJProcedure.with(new UpdateInputFilesProcedure(project, variantCache, transformInvocation))
            aspectJProcedure.with(new UpdateAspectOutputProcedure(project, variantCache, transformInvocation))
        } else {
            //delete output and cache before full build
            transformInvocation.outputProvider.deleteAll()
            variantCache.reset()

            aspectJProcedure.with(new CacheAspectFilesProcedure(project, variantCache, transformInvocation))
            aspectJProcedure.with(new CacheInputFilesProcedure(project, variantCache, transformInvocation))
            aspectJProcedure.with(new DoAspectWorkProcedure(project, variantCache, transformInvocation))
        }

        aspectJProcedure.with(new OnFinishedProcedure(project, variantCache, transformInvocation))

        aspectJProcedure.doWorkContinuously()
    }
}
