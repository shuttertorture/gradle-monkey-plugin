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

import com.android.builder.testing.ConnectedDeviceProvider
import com.android.builder.testing.ConnectedDevice
import org.gradle.api.GradleException

import java.util.concurrent.TimeUnit
import com.android.ddmlib.CollectingOutputReceiver        

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.regex.Pattern

class MonkeyTestTask extends DefaultTask {

    MonkeyTestTask() {
        super()
        this.description = "Run monkey test on all first connected device."
    }

    @TaskAction
    def runMonkeyTest() throws IOException {
        CollectingOutputReceiver receiver
        ConnectedDeviceProvider cdp = new ConnectedDeviceProvider(project.android.plugin.sdkParser)
        cdp.init()
        ConnectedDevice device = cdp.devices[0]
        
        receiver = new CollectingOutputReceiver()
        device.executeShellCommand("dumpsys power", receiver, 30, TimeUnit.SECONDS)
        Collection powerStateLines = receiver.output.split("\\r?\\n").findAll {l -> l.matches("^[ ]*mPowerState.*")}
        
        if (!powerStateLines) {
            throw new GradleException("Couldn't determine if device is locked")
        }
        
        String powerStatus = powerStateLines[0].replaceFirst("^[ ]*mPowerState=", '')
        if (powerStatus.equals("0") || powerStatus.equals("")) {
            device.executeShellCommand("input keyevent KEYCODE_POWER", new CollectingOutputReceiver(), 30, TimeUnit.SECONDS)
        }
        
        receiver = new CollectingOutputReceiver()
        device.executeShellCommand("monkey -p com.autoscout24.debug -vv 5", receiver, 30, TimeUnit.SECONDS)
        
        println 'asdf' + receiver.output + 'asdf'
    }

    def getFile(String regex) {
        def pattern = Pattern.compile(regex)

        if (!project.spoon.apkDirectory.exists()) {
            throw new IllegalStateException("OutputDirectory not found")
        }

        def fileList = project.spoon.apkDirectory.list(
                [accept: { d, f -> f ==~ pattern }] as FilenameFilter
        ).toList()

        if (fileList == null || fileList.size() == 0) {
            return null
        }
        return new File(project.spoon.apkDirectory, fileList[0])
    }

    private static File cleanFile(String path) {
        if (path == null) {
            return null;
        }
        return new File(path);
    }


}
