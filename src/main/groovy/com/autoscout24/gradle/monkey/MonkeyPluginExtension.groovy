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

import org.gradle.api.Project

class MonkeyPluginExtension {
    boolean failOnFailure = false
    boolean teamCityLog = false
    int eventCount = 100
    int delay = 0
    int seed = 0
    int timeOut = 60
    boolean install = false
    int connectTimeoutMs = 5000
    Collection<String> excludedDevices = new ArrayList<String>()

    private final Project project

    MonkeyPluginExtension(Project project) {
        this.project = project
    }
}
