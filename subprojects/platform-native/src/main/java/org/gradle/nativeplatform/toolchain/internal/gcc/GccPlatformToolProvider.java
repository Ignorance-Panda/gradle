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
package org.gradle.nativeplatform.toolchain.internal.gcc;

import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory;
import org.gradle.nativeplatform.internal.LinkerSpec;
import org.gradle.nativeplatform.internal.StaticLibraryArchiverSpec;
import org.gradle.nativeplatform.platform.internal.OperatingSystemInternal;
import org.gradle.nativeplatform.toolchain.internal.AbstractPlatformToolProvider;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolContext;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolInvocationWorker;
import org.gradle.nativeplatform.toolchain.internal.DefaultCommandLineToolInvocationWorker;
import org.gradle.nativeplatform.toolchain.internal.DefaultMutableCommandLineToolContext;
import org.gradle.nativeplatform.toolchain.internal.MutableCommandLineToolContext;
import org.gradle.nativeplatform.toolchain.internal.NativeCompilerFactory;
import org.gradle.nativeplatform.toolchain.internal.ToolType;
import org.gradle.nativeplatform.toolchain.internal.compilespec.AssembleSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.CCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.CPCHCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.CppCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.CppPCHCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.ObjectiveCCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.ObjectiveCPCHCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.ObjectiveCppCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.compilespec.ObjectiveCppPCHCompileSpec;
import org.gradle.nativeplatform.toolchain.internal.tools.GccCommandLineToolConfigurationInternal;
import org.gradle.nativeplatform.toolchain.internal.tools.ToolRegistry;
import org.gradle.nativeplatform.toolchain.internal.tools.ToolSearchPath;
import org.gradle.process.internal.ExecActionFactory;

class GccPlatformToolProvider extends AbstractPlatformToolProvider {
    private final ToolSearchPath toolSearchPath;
    private final NativeCompilerFactory compilerFactory;
    private final ToolRegistry toolRegistry;
    private final ExecActionFactory execActionFactory;
    private final CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory;
    private final boolean useCommandFile;

    GccPlatformToolProvider(NativeCompilerFactory compilerFactory, OperatingSystemInternal targetOperatingSystem, ToolSearchPath toolSearchPath, ToolRegistry toolRegistry, ExecActionFactory execActionFactory, CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory, boolean useCommandFile) {
        super(targetOperatingSystem);
        this.compilerFactory = compilerFactory;
        this.toolRegistry = toolRegistry;
        this.toolSearchPath = toolSearchPath;
        this.compilerOutputFileNamingSchemeFactory = compilerOutputFileNamingSchemeFactory;
        this.useCommandFile = useCommandFile;
        this.execActionFactory = execActionFactory;
    }

    @Override
    protected Compiler<CppCompileSpec> createCppCompiler() {
        GccCommandLineToolConfigurationInternal cppCompilerTool = toolRegistry.getTool(ToolType.CPP_COMPILER);
        CppCompiler compiler = new CppCompiler(compilerOutputFileNamingSchemeFactory, context(cppCompilerTool), getObjectFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(cppCompilerTool), compilerOutputFileNamingSchemeFactory, getObjectFileExtension());
    }

    @Override
    protected Compiler<CppPCHCompileSpec> createCppPCHCompiler() {
        GccCommandLineToolConfigurationInternal cppCompilerTool = toolRegistry.getTool(ToolType.CPP_COMPILER);
        CppPCHCompiler compiler = new CppPCHCompiler(compilerOutputFileNamingSchemeFactory, context(cppCompilerTool), getPCHFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(cppCompilerTool), compilerOutputFileNamingSchemeFactory, getPCHFileExtension());
    }

    @Override
    protected Compiler<CCompileSpec> createCCompiler() {
        GccCommandLineToolConfigurationInternal cCompilerTool = toolRegistry.getTool(ToolType.C_COMPILER);
        CCompiler compiler = new CCompiler(compilerOutputFileNamingSchemeFactory, context(cCompilerTool), getObjectFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(cCompilerTool), compilerOutputFileNamingSchemeFactory, getObjectFileExtension());
    }

    @Override
    protected Compiler<CPCHCompileSpec> createCPCHCompiler() {
        GccCommandLineToolConfigurationInternal cCompilerTool = toolRegistry.getTool(ToolType.C_COMPILER);
        CPCHCompiler compiler = new CPCHCompiler(compilerOutputFileNamingSchemeFactory, context(cCompilerTool), getPCHFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(cCompilerTool), compilerOutputFileNamingSchemeFactory, getPCHFileExtension());
    }

    @Override
    protected Compiler<ObjectiveCppCompileSpec> createObjectiveCppCompiler() {
        GccCommandLineToolConfigurationInternal objectiveCppCompilerTool = toolRegistry.getTool(ToolType.OBJECTIVECPP_COMPILER);
        ObjectiveCppCompiler compiler = new ObjectiveCppCompiler(compilerOutputFileNamingSchemeFactory, context(objectiveCppCompilerTool), getObjectFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(objectiveCppCompilerTool), compilerOutputFileNamingSchemeFactory, getObjectFileExtension());
    }

    @Override
    protected Compiler<ObjectiveCppPCHCompileSpec> createObjectiveCppPCHCompiler() {
        GccCommandLineToolConfigurationInternal objectiveCppCompilerTool = toolRegistry.getTool(ToolType.OBJECTIVECPP_COMPILER);
        ObjectiveCppPCHCompiler compiler = new ObjectiveCppPCHCompiler(compilerOutputFileNamingSchemeFactory, context(objectiveCppCompilerTool), getPCHFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(objectiveCppCompilerTool), compilerOutputFileNamingSchemeFactory, getPCHFileExtension());
    }

    @Override
    protected Compiler<ObjectiveCCompileSpec> createObjectiveCCompiler() {
        GccCommandLineToolConfigurationInternal objectiveCCompilerTool = toolRegistry.getTool(ToolType.OBJECTIVEC_COMPILER);
        ObjectiveCCompiler compiler = new ObjectiveCCompiler(compilerOutputFileNamingSchemeFactory, context(objectiveCCompilerTool), getObjectFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(objectiveCCompilerTool), compilerOutputFileNamingSchemeFactory, getObjectFileExtension());
    }

    @Override
    protected Compiler<ObjectiveCPCHCompileSpec> createObjectiveCPCHCompiler() {
        GccCommandLineToolConfigurationInternal objectiveCCompilerTool = toolRegistry.getTool(ToolType.OBJECTIVEC_COMPILER);
        ObjectiveCPCHCompiler compiler = new ObjectiveCPCHCompiler(compilerOutputFileNamingSchemeFactory, context(objectiveCCompilerTool), getPCHFileExtension(), useCommandFile);
        return compilerFactory.incrementalAndParallelCompiler(compiler, commandLineTool(objectiveCCompilerTool), compilerOutputFileNamingSchemeFactory, getPCHFileExtension());
    }

    @Override
    protected Compiler<AssembleSpec> createAssembler() {
        GccCommandLineToolConfigurationInternal assemblerTool = toolRegistry.getTool(ToolType.ASSEMBLER);
        // Disable command line file for now because some custom assemblers don't understand the same arguments as GCC.
        Assembler assembler = new Assembler(compilerOutputFileNamingSchemeFactory, context(assemblerTool), getObjectFileExtension(), false);
        return compilerFactory.compiler(assembler, commandLineTool(assemblerTool));
    }

    @Override
    protected Compiler<LinkerSpec> createLinker() {
        GccCommandLineToolConfigurationInternal linkerTool = toolRegistry.getTool(ToolType.LINKER);
        GccLinker linker = new GccLinker(context(linkerTool), useCommandFile);
        return compilerFactory.compiler(linker, commandLineTool(linkerTool));
    }

    @Override
    protected Compiler<StaticLibraryArchiverSpec> createStaticLibraryArchiver() {
        GccCommandLineToolConfigurationInternal staticLibArchiverTool = toolRegistry.getTool(ToolType.STATIC_LIB_ARCHIVER);
        ArStaticLibraryArchiver archiver = new ArStaticLibraryArchiver(context(staticLibArchiverTool));
        return compilerFactory.compiler(archiver, commandLineTool(staticLibArchiverTool));
    }

    private CommandLineToolInvocationWorker commandLineTool(GccCommandLineToolConfigurationInternal tool) {
        ToolType key = tool.getToolType();
        String exeName = tool.getExecutable();
        return new DefaultCommandLineToolInvocationWorker(key.getToolName(), toolSearchPath.locate(key, exeName).getTool(), execActionFactory);
    }

    private CommandLineToolContext context(GccCommandLineToolConfigurationInternal toolConfiguration) {
        MutableCommandLineToolContext baseInvocation = new DefaultMutableCommandLineToolContext();
        // MinGW requires the path to be set
        baseInvocation.addPath(toolSearchPath.getPath());
        baseInvocation.addEnvironmentVar("CYGWIN", "nodosfilewarning");
        baseInvocation.setArgAction(toolConfiguration.getArgAction());
        return baseInvocation;
    }

    public String getPCHFileExtension() {
        return ".h.gch";
    }
}
