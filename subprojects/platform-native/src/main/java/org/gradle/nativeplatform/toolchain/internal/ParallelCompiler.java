/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativeplatform.toolchain.internal;

import org.gradle.api.Action;
import org.gradle.api.internal.tasks.SimpleWorkResult;
import org.gradle.api.tasks.WorkResult;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.operations.BuildOperationQueue;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.nativeplatform.internal.BinaryToolSpec;

/**
 * A compiler that runs one or more command-line tools in parallel and returns the result.
 */
public class ParallelCompiler<T extends BinaryToolSpec> implements Compiler<T> {
    private final CommandLineToolBackedCompiler<T> compiler;
    private final CommandLineToolInvocationWorker commandLineToolInvocationWorker;
    private final BuildOperationExecutor buildOperationExecutor;

    public ParallelCompiler(BuildOperationExecutor buildOperationExecutor, CommandLineToolInvocationWorker commandLineToolInvocationWorker, CommandLineToolBackedCompiler<T> compiler) {
        this.compiler = compiler;
        this.commandLineToolInvocationWorker = commandLineToolInvocationWorker;
        this.buildOperationExecutor = buildOperationExecutor;
    }

    @Override
    public WorkResult execute(final T spec) {
        buildOperationExecutor.runAll(commandLineToolInvocationWorker, new Action<BuildOperationQueue<CommandLineToolInvocation>>() {
            @Override
            public void execute(BuildOperationQueue<CommandLineToolInvocation> queue) {
                queue.setLogLocation(spec.getOperationLogger().getLogLocation());
                compiler.start(spec, queue);
            }
        });
        return new SimpleWorkResult(true);
    }
}
