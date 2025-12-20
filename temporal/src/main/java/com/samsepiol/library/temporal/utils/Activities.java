package com.samsepiol.library.temporal.utils;

import com.samsepiol.library.temporal.constants.Queues;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Duration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Activities {

    public static <T> @NonNull T newActivity(@NonNull Class<T> activityClass) {
        return Workflow.newActivityStub(activityClass, Options.DEFAULT_MAX_ATTEMPT_ACTIVITY_OPTIONS);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public final class Options {

        public static final ActivityOptions DEFAULT_ONE_ATTEMPT_ACTIVITY_OPTIONS =
                ActivityOptions.newBuilder()
                        .setTaskQueue(Queues.WORKFLOWS)
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumAttempts(1)
                                .build())
                        .setStartToCloseTimeout(Duration.ofMinutes(1))
                        .build();

        public static final ActivityOptions DEFAULT_MAX_ATTEMPT_ACTIVITY_OPTIONS =
                ActivityOptions.newBuilder()
                        .setRetryOptions(RetryOptions.getDefaultInstance())
                        .setStartToCloseTimeout(Duration.ofMinutes(1))
                        .build();

        public static final ActivityOptions DEFAULT_TWENTY_ATTEMPT_ACTIVITY_OPTIONS =
                ActivityOptions.newBuilder()
                        .setTaskQueue(Queues.WORKFLOWS)
                        .setRetryOptions(RetryOptions.newBuilder()
                                .setMaximumAttempts(20)
                                .setInitialInterval(Duration.ofSeconds(10))
                                .setMaximumInterval(Duration.ofMinutes(5))
                                .setBackoffCoefficient(2)
                                .build())
                        .setStartToCloseTimeout(Duration.ofMinutes(1))
                        .build();

    }
}
