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

package com.autoscout24.gradle.monkey

// Adapted from https://github.com/jenkinsci/android-emulator-plugin/blob/master/src/main/java/hudson/plugins/android_emulator/monkey/MonkeyResult.java
public class MonkeyResult {
    final ResultStatus status;
    final int totalEventCount
    final int eventsCompleted

    MonkeyResult(ResultStatus status, int totalEventCount, int eventsCompleted) {
        this.eventsCompleted = eventsCompleted
        this.totalEventCount = totalEventCount
        this.status = status
    }

    public enum ResultStatus {
        /** Monkey test completed successfully */
        Success(true),
        /** Application crashed while under test */
        Crash(false),
        /** ANR occurred while under test */
        AppNotResponding(false),
        /** No monkey output was found to parse */
        NothingToParse(false),
        /** Monkey output was given, but outcome couldn't be determined */
        UnrecognisedFormat(false);

        public final isSuccess

        ResultStatus(boolean isSuccess) {
            this.isSuccess = isSuccess
        }
    }
}
