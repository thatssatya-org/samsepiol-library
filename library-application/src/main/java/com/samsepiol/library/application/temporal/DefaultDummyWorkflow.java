package com.samsepiol.library.application.temporal;

public class DefaultDummyWorkflow implements DummyWorkflow {
    private final DummyActivity dummyActivity = DummyActivity.getInstance();

    @Override
    public void process() {
        dummyActivity.process();
    }
}
