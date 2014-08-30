/*
 * Copyright 2012 the original author or authors.
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

import org.gradle.api.internal.file.FileResolver;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.internal.reflect.Instantiator;
import org.gradle.nativeplatform.toolchain.Gcc;
import org.gradle.nativeplatform.toolchain.internal.ToolChainAvailability;
import org.gradle.nativeplatform.toolchain.internal.ToolType;
import org.gradle.nativeplatform.toolchain.internal.gcc.version.CompilerMetaDataProvider;
import org.gradle.nativeplatform.toolchain.internal.gcc.version.CompilerMetaDataProviderFactory;
import org.gradle.nativeplatform.toolchain.internal.gcc.version.GccVersionResult;
import org.gradle.nativeplatform.toolchain.internal.tools.CommandLineToolSearchResult;
import org.gradle.process.internal.ExecActionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Compiler adapter for GCC.
 */
public class GccToolChain extends AbstractGccCompatibleToolChain implements Gcc {

    private static final Logger LOGGER = LoggerFactory.getLogger(GccToolChain.class);

    public static final String DEFAULT_NAME = "gcc";

    private final CompilerMetaDataProvider versionDeterminer;

    public GccToolChain(Instantiator instantiator, String name, OperatingSystem operatingSystem, FileResolver fileResolver, ExecActionFactory execActionFactory, CompilerMetaDataProviderFactory metaDataProviderFactory) {
        super(name, operatingSystem, fileResolver, execActionFactory, instantiator);
        this.versionDeterminer = metaDataProviderFactory.gcc();
    }

    @Override
    protected String getTypeName() {
        return "GNU GCC";
    }

    @Override
    protected void initTools(DefaultGccPlatformToolChain platformToolChain, ToolChainAvailability availability) {
        CommandLineToolSearchResult compiler = locate(platformToolChain.getcCompiler());
        if (!compiler.isAvailable()) {
            compiler = locate(platformToolChain.getCppCompiler());
        }
        if (!compiler.isAvailable()) {
            return;
        }

        GccVersionResult versionResult = versionDeterminer.getGccMetaData(compiler.getTool());
        availability.mustBeAvailable(versionResult);
        if (versionResult.isAvailable()) {
            LOGGER.debug("Found {} with version {}", ToolType.C_COMPILER.getToolName(), versionResult);
            platformToolChain.setCanUseCommandFile(versionResult.getVersion().getMajor() >= 4);
        }

        super.initTools(platformToolChain, availability);
    }
}