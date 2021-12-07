/*
 * This code is based on the OpenJDK build process defined in `make/gensrc/GensrcBuffer.gmk`, which contains the following
 * copyright notice:
 *
 * #
 * # Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
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

package org.qbicc.mojo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import build.tools.spp.AbstractSppMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.qbicc.rt.annotation.Tracking;

@Mojo(name = "generate-emoji-data", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Tracking("make/modules/java.base/gensrc/GensrcEmojiData.gmk")
public class GenerateEmojiDataMojo extends AbstractSppMojo {
    static final String JAVA = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows") ? "java.exe" : "java";

    @Parameter(defaultValue = "${project.baseDir}/../../openjdk/src/java.base/share/classes")
    File inputDirectory;

    @Parameter(defaultValue = "${project.baseDir}/../../openjdk/make/data/unicodedata")
    File unicodeData;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/spp")
    File outputDirectory;

    public void execute() throws MojoFailureException, MojoExecutionException {
        try {
            Path java = Path.of(System.getProperty("java.home"), "bin", JAVA);
            if (! (Files.isRegularFile(java) && Files.isExecutable(java))) {
                throw new MojoFailureException("Cannot locate java executable");
            }
            Path template = inputDirectory.toPath().resolve("java").resolve("util").resolve("regex").resolve("EmojiData.java.template");
            Path parent = outputDirectory.toPath().resolve("java").resolve("util").resolve("regex");
            Path outputFile = parent.resolve("EmojiData.java");
            Files.createDirectories(parent);
            ProcessBuilder pb = new ProcessBuilder();
            List<String> command = new ArrayList<>();
            command.add(java.toString());
            // this is a tricky maneuver to ensure the forked JVM has the same classpath that I do
            command.add("-classpath");
            ClassRealm realm = (ClassRealm) getClass().getClassLoader();
            URL[] urls = realm.getURLs();
            command.add(Arrays.stream(urls).map(Objects::toString).collect(Collectors.joining(File.pathSeparator)));
            // the tool class
            command.add(build.tools.generateemojidata.GenerateEmojiData.class.getName());

            // args
            command.add(template.toString());
            command.add(unicodeData.toString());
            command.add(outputFile.toString());

            pb.command(command);
            MojoUtil.runAndWaitForProcessNoInput(pb);

        } catch (IOException e) {
            throw new MojoExecutionException("Mojo failed: " + e, e);
        }
    }
}
