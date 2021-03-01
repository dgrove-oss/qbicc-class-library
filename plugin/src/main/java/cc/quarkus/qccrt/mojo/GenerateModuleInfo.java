/*
 * This code is based on the OpenJDK build process defined in `make/gensrc/GensrcModuleInfo.gmk`, which contains the following
 * copyright notice:
 *
 * #
 * # Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.
 * # DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * #
 * # This code is free software; you can redistribute it and/or modify it
 * # under the terms of the GNU General Public License version 2 only, as
 * # published by the Free Software Foundation.  Oracle designates this
 * # particular file as subject to the "Classpath" exception as provided
 * # by Oracle in the LICENSE file that accompanied this code.
 * #
 * # This code is distributed in the hope that it will be useful, but WITHOUT
 * # ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * # FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * # version 2 for more details (a copy is included in the LICENSE file that
 * # accompanied this code).
 * #
 * # You should have received a copy of the GNU General Public License version
 * # 2 along with this work; if not, write to the Free Software Foundation,
 * # Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * #
 * # Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * # or visit www.oracle.com if you need additional information or have any
 * # questions.
 * #
 *
 * This file may contain additional modifications which are Copyright (c) Red Hat and other
 * contributors.
 */

package cc.quarkus.qccrt.mojo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import build.tools.module.GenModuleInfoSource;
import cc.quarkus.qccrt.annotation.Tracking;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

/**
 *
 */
@Mojo(name = "generate-module-info", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Tracking("make/gensrc/GensrcModuleInfo.gmk")
public class GenerateModuleInfo extends AbstractMojo {
    static final String JAVA = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows") ? "java.exe" : "java";

    @Parameter(required = true)
    File output;

    @Parameter(required = true)
    File sourceFile;

    @Parameter
    List<String> allModules;

    @Parameter
    File openJdkSrc;

    @Parameter(required = true)
    List<File> moduleFiles;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Path java = Path.of(System.getProperty("java.home"), "bin", JAVA);
            if (! (Files.isRegularFile(java) && Files.isExecutable(java))) {
                throw new MojoFailureException("Cannot locate java executable");
            }
            Path outputPath = output.toPath();
            Files.createDirectories(outputPath.getParent());
            ProcessBuilder pb = new ProcessBuilder();
            List<String> command = new ArrayList<>();
            command.add(java.toString());
            // this is a tricky maneuver to ensure the forked JVM has the same classpath that I do
            command.add("-classpath");
            ClassRealm realm = (ClassRealm) getClass().getClassLoader();
            URL[] urls = realm.getURLs();
            command.add(Arrays.stream(urls).map(Objects::toString).collect(Collectors.joining(File.pathSeparator)));
            // the tool class
            command.add(GenModuleInfoSource.class.getName());

            // args
            command.add("-o");
            command.add(output.toString());
            command.add("--source-file");
            command.add(sourceFile.toString());
            command.add("--modules");
            List<String> modules;
            if (allModules == null) {
                // this is a temporary sort of hack, I guess...
                if (openJdkSrc == null) {
                    throw new MojoExecutionException("One of either allModules or openJdkSrc must be given");
                }
                modules = new ArrayList<>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(openJdkSrc.toPath())) {
                    for (Path path : stream) {
                        if (Files.isDirectory(path)) {
                            String simpleName = path.getFileName().toString();
                            if (simpleName.startsWith("java.") || simpleName.startsWith("jdk.")) {
                                modules.add(simpleName);
                            }
                        }
                    }
                }
            } else {
                modules = allModules;
            }
            command.add(String.join(",", modules));
            moduleFiles.stream().map(File::toString).forEach(command::add);

            pb.command(command);
            MojoUtil.runAndWaitForProcessNoInput(pb);

        } catch (IOException e) {
            throw new MojoExecutionException("Mojo failed: " + e, e);
        }
    }
}
