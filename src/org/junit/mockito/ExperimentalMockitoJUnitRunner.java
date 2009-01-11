/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.junit.mockito;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.internal.debugging.DebuggingInfo;
import org.mockito.internal.progress.MockingProgress;
import org.mockito.internal.progress.ThreadSafeMockingProgress;
import org.mockito.internal.util.MockitoLogger;
import org.mockito.internal.util.MockitoLoggerImpl;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Uses <b>JUnit 4.5</b> runner {@link BlockJUnit4ClassRunner}.
 * <p>
 * This runner does exactly what {@link MockitoJUnitRunner} does but also  
 * prints useful warnings that can enhance testing experience. 
 * The point is that Mockito should help the tdd developer to quickly figure out if the test fails for the right reason. 
 * Then the developer can implement the functionality. 
 * Also when the test fails it should be easy to figure out why the test fails. 
 * <p>
 * Sometimes when the test fails, the underlying reason is that stubbed method was called with wrong arguments. 
 * Sometimes it fails because one forgets to stub a method or forgets to call a stubbed method. 
 * All above problems are not immediately obvious.
 * <p>
 * One way of approaching this problem is full-blown 'expect' API. 
 * However it means the 'expectations upfront' business which is not in line with core Mockito concepts.
 * After all, one of the key points of Mockito are <b>explicit assertions</b> that are always placed at the <b>bottom of the test</b> method.
 * <p>
 * Let's look at different ways of addressing the issue.
 * Here's the experiment: a warning is printed to the standard output if the test fails.
 * Also, you get a clickabe link to the line of code. You can immediately jump to the place in code where the potential problem is.
 * <p> 
 * Let's say your test fails on assertion. 
 * Let's say the underlying reason is a stubbed method that was called with different arguments:
 * <pre>
 * //test:
 * when(translator.translate("Mockito")).thenReturn("cool framework");
 * String translated = dictionary.search("Mockito");
 * assertEquals("cool framework", translated);
 * 
 * //code:
 * public String search(String word) {
 *     ...
 *     return translator.translate("oups");
 *
 * </pre>
 * On standard output you'll see something like that:
 * <pre>
 * [Mockito] Warning - stubbed method called with different arguments.
 * Stubbed this way:
 * translator.translate("Mockito");
 * org.dictionary.SmartDictionaryTest.shouldFindTranslation(SmartDictionaryTest.java:27)
 *  
 * But called with different arguments:
 * translator.translate("oups");
 * org.dictionary.SmartDictionary.search(SmartDictionary.java:15)
 * </pre>
 * <p>
 * Note that it is just a warning, not an assertion. 
 * The test fails on assertion because it's the assertion's task to document what the test stands for and what behavior it proves. 
 * Warnings just helps debugging tests.
 * <p>
 * Note that code links printed to the console are clickable in any decent IDE (e.g. Eclipse).
 * <p>
 * So far I identified 3 cases when warnings are printed:
 * <li>unstubbed method</li>
 * <li>unsued stub</li>
 * <li>stubbed method but called with different arguments</li> 
 * <p>
 * <br/>
 * Do you think it is useful or not? Drop us an email at mockito@googlegroups.com 
 */
public class ExperimentalMockitoJUnitRunner extends MockitoJUnitRunner {

    private final MockitoLogger logger;
    
    public ExperimentalMockitoJUnitRunner(Class<?> klass) throws InitializationError {
        this(klass, new MockitoLoggerImpl());
    }
    
    public ExperimentalMockitoJUnitRunner(Class<?> klass, MockitoLogger logger) throws InitializationError {
        super(klass);
        this.logger = logger;
    }
    
    //this is what is really executed when the test runs
    static interface JunitTestBody {
        void run(RunNotifier notifier);
    }
    
    @Override
    public void run(RunNotifier notifier) {
        this.run(notifier, new JunitTestBody() {
            public void run(RunNotifier notifier) {
                ExperimentalMockitoJUnitRunner.super.run(notifier);
            }
        });
    }
    
    public void run(RunNotifier notifier, JunitTestBody junitTestBody) {
        MockingProgress progress = new ThreadSafeMockingProgress();
        DebuggingInfo debuggingInfo = progress.getDebuggingInfo();
        
        beforeRun(notifier, debuggingInfo);
        
        junitTestBody.run(notifier);
        
        afterRun(debuggingInfo);
    }

    private void afterRun(final DebuggingInfo debuggingInfo) {
        debuggingInfo.clearData();
    }

    private void beforeRun(RunNotifier notifier, final DebuggingInfo debuggingInfo) {
        debuggingInfo.collectData();

        RunListener listener = new RunListener() {
            @Override public void testFailure(Failure failure) throws Exception {
                debuggingInfo.printWarnings(logger);
            }
        };
        
        notifier.addListener(listener);
    }
}