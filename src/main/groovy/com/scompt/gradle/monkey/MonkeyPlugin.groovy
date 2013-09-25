/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Edward Dale
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

package com.scompt.gradle.monkey

import org.gradle.api.Plugin
import org.gradle.api.Project

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant

import org.gradle.api.plugins.JavaBasePlugin

class MonkeyPlugin implements Plugin<Project> {

    void apply(Project project) {
        configureDependencies(project)
        applyExtensions(project)
        applyTasks(project)
    }

    void applyExtensions(final Project project) {
        project.extensions.create('monkey', MonkeyPluginExtension, project)
    }

    void applyTasks(final Project project) {
        if (!project.plugins.hasPlugin(AppPlugin)) {
            throw new IllegalStateException("gradle-android-plugin not found")
        }

        AppExtension android = project.android
        android.applicationVariants.all { ApplicationVariant variant ->
            MonkeyTestTask task = project.tasks.create("monkey${variant.name}", MonkeyTestTask)
            task.dependsOn(variant.install)
            task.group = JavaBasePlugin.VERIFICATION_GROUP
            task.description = "Run the ${variant.name} monkey tests on the first connected device"
            task.packageName = getPackageName(variant)
            task.outputs.upToDateWhen { false }
        }
    }

    private String getPackageName(ApplicationVariant variant) {
        // TODO: There's probably a better way to get the package name of the variant being tested.
        String packageName = variant.generateBuildConfig.packageName

        if (variant.processManifest.packageNameOverride != null) {
            packageName = variant.processManifest.packageNameOverride
        }
        packageName
    }


    void configureDependencies(final Project project) {
        project.repositories {
            mavenCentral()
        }
    }

}