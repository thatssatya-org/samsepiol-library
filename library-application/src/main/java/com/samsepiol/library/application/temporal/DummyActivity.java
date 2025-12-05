package com.samsepiol.library.application.temporal;

import com.samsepiol.library.temporal.activity.TemporalActivity;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

@ActivityInterface
public interface DummyActivity extends TemporalActivity {

    @ActivityMethod
    void process();

    static DummyActivity getInstance() {
        return Workflow.newActivityStub(DummyActivity.class, ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(5))
                .build());
    }
}
