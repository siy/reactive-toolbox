/*
 * Copyright (c) 2017 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.injector.internal.examples.annotated.impl;

import org.reactivetoolbox.injector.annotations.Singleton;
import org.reactivetoolbox.injector.internal.examples.annotated.Application;
import org.reactivetoolbox.injector.internal.examples.annotated.ApplicationConfig;
import org.reactivetoolbox.injector.internal.examples.annotated.Service;
import org.reactivetoolbox.injector.internal.examples.annotated.ServiceFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ApplicationImpl implements Application {
    private static final long ONE_DOT_DURATION = 200;
    private static final int DOT_COUNT = 5;

    private final ApplicationConfig config;
    private final ServiceFactory factory;

    public ApplicationImpl(ApplicationConfig config, ServiceFactory factory) {
        this.config = config;
        this.factory = factory;
    }

    @Override
    public void run() {
        List<Service> running = startServices();

        waitForEvent();

        stopServices(running);
    }

    private void stopServices(List<Service> running) {
        running.stream()
               .peek(Service::stop)
               .forEach(service -> log("Stopped " + service.name()));
    }

    private List<Service> startServices() {
        log("Application is starting...");
        List<Service> running = config.services().stream()
                                      .map(factory::create)
                                      .peek(Service::start)
                                      .peek(service -> log("Started " + service.name()))
                                      .collect(Collectors.toList());
        Collections.reverse(running);
        return running;
    }

    private void waitForEvent() {
        //In real application here can be waiting for some event, but in this example
        //we just wait for few seconds and then stop

        log("Application is running");
        for (int i = 0; i < DOT_COUNT; i++) {
            sleep(ONE_DOT_DURATION);
            log(".");
        }
        log("Application is stopping");
    }

    private void log(String message) {
        System.out.println(message);
    }

    private static void sleep(long durationInMs) {
        try {
            Thread.sleep(durationInMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
