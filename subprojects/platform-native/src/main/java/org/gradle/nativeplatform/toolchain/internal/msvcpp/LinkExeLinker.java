/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.nativeplatform.toolchain.internal.msvcpp;

import org.gradle.api.Transformer;
import org.gradle.internal.operations.BuildOperationQueue;
import org.gradle.nativeplatform.internal.LinkerSpec;
import org.gradle.nativeplatform.internal.SharedLibraryLinkerSpec;
import org.gradle.nativeplatform.toolchain.internal.AbstractCompiler;
import org.gradle.nativeplatform.toolchain.internal.ArgsTransformer;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolContext;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolInvocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.gradle.nativeplatform.toolchain.internal.msvcpp.EscapeUserArgs.escapeUserArgs;

class LinkExeLinker extends AbstractCompiler<LinkerSpec> {

    private final Transformer<LinkerSpec, LinkerSpec> specTransformer;
    private final CommandLineToolContext invocationContext;

    LinkExeLinker(CommandLineToolContext invocationContext, Transformer<LinkerSpec, LinkerSpec> specTransformer) {
        super(invocationContext, new LinkerArgsTransformer(), true);
        this.invocationContext = invocationContext;
        this.specTransformer = specTransformer;
    }

    @Override
    public void start(LinkerSpec spec, BuildOperationQueue<CommandLineToolInvocation> queue) {
        spec = specTransformer.transform(spec);
        List<String> args = getArguments(spec);
        CommandLineToolInvocation invocation = invocationContext.createInvocation(
            "linking " + spec.getOutputFile().getName(), spec.getOutputFile().getParentFile(), args, spec.getOperationLogger());
        queue.add(invocation);
    }

    protected void addOptionsFileArgs(List<String> args, File tempDir) {
        new VisualCppOptionsFileArgsWriter(tempDir).execute(args);
    }

    private static class LinkerArgsTransformer implements ArgsTransformer<LinkerSpec> {
        @Override
        public List<String> transform(LinkerSpec spec) {
            List<String> args = new ArrayList<String>();
            args.addAll(escapeUserArgs(spec.getAllArgs()));
            args.add("/OUT:" + spec.getOutputFile().getAbsolutePath());
            args.add("/NOLOGO");
            if (spec instanceof SharedLibraryLinkerSpec) {
                args.add("/DLL");
            }
            for (File pathEntry : spec.getLibraryPath()) {
                args.add("/LIBPATH:" + pathEntry.getAbsolutePath());
            }
            for (File file : spec.getObjectFiles()) {
                args.add(file.getAbsolutePath());
            }
            for (File file : spec.getLibraries()) {
                args.add(file.getAbsolutePath());
            }
            return args;
        }
    }
}
