/*
 * This code is based on the OpenJDK build process defined in `make/gensrc/GensrcCharsetMapping.gmk`, which contains the following
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

package cc.quarkus.qccrt.mojo;

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

import build.tools.charsetmapping.Main;
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
@Mojo(name = "generate-charset-mapping", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Tracking("make/gensrc/GensrcCharsetMapping.gmk")
public class GenerateCharsetMappingMojo extends AbstractMojo {
    static final String JAVA = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows") ? "java.exe" : "java";

    @Parameter(required = true)
    File dataDirectory;

    @Parameter(required = true)
    File extSrcDirectory;

    @Parameter(required = true)
    File outputDirectory;

    @Parameter(required = true)
    File copyrightHeader;

    @Parameter(required = true)
    List<File> javaTemplates;

    @Parameter(required = true)
    List<File> charsetTemplates;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Path java = Path.of(System.getProperty("java.home"), "bin", JAVA);
            if (! (Files.isRegularFile(java) && Files.isExecutable(java))) {
                throw new MojoFailureException("Cannot locate java executable");
            }
            Files.createDirectories(outputDirectory.toPath());
            ProcessBuilder pb = new ProcessBuilder();
            List<String> command = new ArrayList<>();
            command.add(java.toString());
            // this is a tricky maneuver to ensure the forked JVM has the same classpath that I do
            command.add("-classpath");
            ClassRealm realm = (ClassRealm) getClass().getClassLoader();
            URL[] urls = realm.getURLs();
            command.add(Arrays.stream(urls).map(Objects::toString).collect(Collectors.joining(File.pathSeparator)));
            // the tool class
            command.add(Main.class.getName());

            // args
            command.add(dataDirectory.toString());
            command.add(outputDirectory.toString());
            command.add("stdcs");
            command.add("charsets");
            command.add("stdcs-all");
            javaTemplates.stream().map(Objects::toString).forEach(command::add);
            command.add(extSrcDirectory.toString());
            command.add(copyrightHeader.toString());

            pb.command(command);
            MojoUtil.runAndWaitForProcessNoInput(pb);

        } catch (IOException e) {
            throw new MojoExecutionException("Mojo failed: " + e, e);
        }
    }
}
