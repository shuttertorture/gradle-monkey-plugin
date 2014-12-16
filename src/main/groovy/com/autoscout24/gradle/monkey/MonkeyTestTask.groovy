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
import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.core.VariantConfiguration
import com.android.builder.testing.ConnectedDevice
import com.android.builder.testing.ConnectedDeviceProvider
import com.android.ddmlib.CollectingOutputReceiver
import com.android.utils.StdLogger
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

class MonkeyTestTask extends DefaultTask {

    @OutputFile
    File reportFile

    @InputFile
    @Optional
    File apkFile

    String variantName;

    AppExtension android
    MonkeyPluginExtension monkey

    @TaskAction
    def runMonkeyTest() throws IOException {

        android = project.android
        monkey = project.monkey

        String packageName = getPackageName()
        logger.info("Running tests for package: " + packageName)

        ConnectedDeviceProvider cdp = new ConnectedDeviceProvider(android.getAdbExe())
        cdp.init()
        ConnectedDevice device = cdp.devices[0]
        logger.info("Found device " + device.name)

        if (apkFile != null) {
            def logger = new StdLogger(StdLogger.Level.VERBOSE)
            device.uninstallPackage(packageName, 30000, logger)
            device.installPackage(apkFile, 30000, logger)
        }

        CollectingOutputReceiver receiver = new CollectingOutputReceiver()
        String monkeyCommand = String.format("monkey -p %s -s %d -vv %d ", packageName, monkey.seed, monkey.eventCount)
        device.executeShellCommand(monkeyCommand, receiver, 30, TimeUnit.SECONDS)

        String monkeyOutput = receiver.output
        MonkeyResult result = parseMonkeyOutput(monkeyOutput)

        if (logger.isDebugEnabled()) {
            println monkeyOutput
        }

        if (monkey.teamCityLog) {
            println String.format("##teamcity[buildStatus status='%s' text='{build.status.text}, %d/%d events']",
                    result.status.isSuccess ? "SUCCESS" : "FAILURE", result.eventsCompleted, result.totalEventCount)
        }

        if (reportFile != null) {
            def reportsDir = reportFile.getParentFile()
            if (!reportsDir.exists() && !reportsDir.mkdirs()) {
                throw new GradleException("Could not create reports directory: " + reportsDir.getAbsolutePath())
            }

            reportFile.write(monkeyOutput, "UTF-8")
        }

        if (result.status != MonkeyResult.ResultStatus.Success && monkey.failOnFailure) {
            System.exit(1)
        }
    }

    // Adapted from https://github.com/jenkinsci/android-emulator-plugin/blob/master/src/main/java/hudson/plugins/android_emulator/monkey/MonkeyBuilder.java
    def parseMonkeyOutput(String monkeyOutput) {
        // No input, no output
        if (monkeyOutput == null) {
            return new MonkeyResult(MonkeyResult.ResultStatus.NothingToParse, 0, 0);
        }

        // If we don't recognise any outcomes, then say so
        MonkeyResult.ResultStatus status = MonkeyResult.ResultStatus.UnrecognisedFormat;

        // Extract common data
        int totalEventCount = 0;
        Matcher matcher = Pattern.compile(":Monkey: seed=-?\\d+ count=(\\d+)").matcher(monkeyOutput);
        if (matcher.find()) {
            totalEventCount = Integer.parseInt(matcher.group(1));
        }

        // Determine outcome
        int eventsCompleted = 0;
        if (monkeyOutput.contains("// Monkey finished")) {
            status = MonkeyResult.ResultStatus.Success;
            eventsCompleted = totalEventCount;
        } else {
            // If it didn't finish, assume failure
            matcher = Pattern.compile("Events injected: (\\d+)").matcher(monkeyOutput);
            if (matcher.find()) {
                eventsCompleted = Integer.parseInt(matcher.group(1));
            }

            // Determine failure type
            matcher = Pattern.compile("// (CRASH|NOT RESPONDING)").matcher(monkeyOutput);
            if (matcher.find()) {
                String reason = matcher.group(1);
                if ("CRASH".equals(reason)) {
                    status = MonkeyResult.ResultStatus.Crash;
                } else if ("NOT RESPONDING".equals(reason)) {
                    status = MonkeyResult.ResultStatus.AppNotResponding;
                }
            }
        }

        return new MonkeyResult(status, totalEventCount, eventsCompleted)
    }

    private String getPackageName() {
        def matchingVariants = android.applicationVariants.matching { var -> var.name == variantName }

        if (matchingVariants.isEmpty()) {
            throw new GradleException("Could not find the '" + variantName + "' variant")
        }

        ApplicationVariant variant = matchingVariants.iterator().next()

        VariantConfiguration.getManifestPackage(variant.getOutputs()[0].getProcessManifest().manifestOutputFile)
    }
}
