/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 AutoScout24 GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.autoscout24.gradle.monkey

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.core.BuilderConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin

class MonkeyPlugin implements Plugin<Project> {

    void apply(Project project) {
        applyExtensions(project)
        applyTasks(project)
    }

    static void applyExtensions(final Project project) {
        project.extensions.create('monkey', MonkeyPluginExtension, project)
    }

    static void applyTasks(final Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalStateException("gradle-android-plugin not found")
        }

        AppExtension android =  project.extensions.getByType(AppExtension)
        android.applicationVariants.all { ApplicationVariant variant ->

            MonkeyTestTask task = project.tasks.create("monkey${variant.name.capitalize()}", MonkeyTestTask)
            task.group = JavaBasePlugin.VERIFICATION_GROUP
            task.description = "Run the ${variant.name.capitalize()} monkey tests on the first connected device"
            task.variantName = variant.name
            task.reportFileDirectory = new File(project.buildDir, BuilderConstants.FD_REPORTS)
            task.outputs.upToDateWhen { false }

            if (project.extensions.getByType(MonkeyPluginExtension).install) {
                task.dependsOn(variant.assemble)
                variant.outputs.each { output ->
                    task.apkFile = output.outputFile
                }
            }
        }
    }

}