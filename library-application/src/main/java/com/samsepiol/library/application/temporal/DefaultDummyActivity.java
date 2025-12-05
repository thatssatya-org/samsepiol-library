package com.samsepiol.library.application.temporal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultDummyActivity implements DummyActivity {

    @Override
    public void process() {

    }
}
