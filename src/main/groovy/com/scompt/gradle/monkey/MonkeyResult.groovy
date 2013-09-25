package com.scompt.gradle.monkey

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
