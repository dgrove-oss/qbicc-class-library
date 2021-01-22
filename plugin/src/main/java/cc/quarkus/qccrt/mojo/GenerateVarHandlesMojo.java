package cc.quarkus.qccrt.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import build.tools.spp.AbstractSppMojo;
import cc.quarkus.qccrt.annotation.Tracking;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-var-handles", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@Tracking("make/gensrc/GensrcVarHandles.gmk")
public class GenerateVarHandlesMojo extends AbstractSppMojo {
    @Parameter(required = true)
    File varHandleTemplateFile;

    @Parameter(required = true)
    File varHandleByteArrayViewTemplateFile;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/spp")
    File outputDirectory;

    public void execute() throws MojoFailureException {
        try {
            Path outputPath = outputDirectory.toPath().resolve("java").resolve("lang").resolve("invoke");
            Files.createDirectories(outputPath);
            generateVarHandle("Boolean", outputPath);
            generateVarHandle("Byte", outputPath);
            generateVarHandle("Short", outputPath);
            generateVarHandle("Char", outputPath);
            generateVarHandle("Int", outputPath);
            generateVarHandle("Long", outputPath);
            generateVarHandle("Float", outputPath);
            generateVarHandle("Double", outputPath);
            generateVarHandle("Object", outputPath);

            generateVarHandleByteArray("Short", outputPath);
            generateVarHandleByteArray("Char", outputPath);
            generateVarHandleByteArray("Int", outputPath);
            generateVarHandleByteArray("Long", outputPath);
            generateVarHandleByteArray("Float", outputPath);
            generateVarHandleByteArray("Double", outputPath);
        } catch (IOException e) {
            throw new MojoFailureException("Failed to process one or more file(s)", e);
        }
    }

    static final Set<String> atomicAddTypes = Set.of("Byte", "Short", "Char", "Int", "Long", "Float", "Double");
    static final Set<String> bitwiseTypes = Set.of("Boolean", "Byte", "Short", "Char", "Int", "Long");
    static final Set<String> shorterThanIntTypes = Set.of("Byte", "Short", "Char");

    void generateVarHandle(String type, Path outputDirectory) throws IOException {
        Set<String> keys = new HashSet<>();
        keys.add("CAS");
        if (atomicAddTypes.contains(type)) {
            keys.add("AtomicAdd");
        }
        if (bitwiseTypes.contains(type)) {
            keys.add("Bitwise");
        }
        if (shorterThanIntTypes.contains(type)) {
            keys.add("ShorterThanInt");
        }
        String littleType = type.equals("Object") ? type : type.toLowerCase(Locale.ROOT);
        keys.add(littleType);
        doSpp(varHandleTemplateFile.toPath(), outputDirectory.resolve("VarHandle" + type + "s.java"), keys, Map.of(
            "type", littleType,
            "Type", type
        ), false, false);
    }

    static final Map<String, String> rawTypes = Map.of(
        "Short",  "Short",
        "Char",   "Char",
        "Int",    "Int",
        "Long",   "Long",
        "Float",  "Int",
        "Double", "Long"
    );

    static final Map<String, String> boxTypes = Map.of(
        "Short", "Short",
        "Char", "Character",
        "Int", "Integer",
        "Long", "Long",
        "Float", "Float",
        "Double", "Double"
    );

    static final Set<String> arrayCasTypes = Set.of(
        "Int",
        "Long",
        "Float",
        "Double"
    );

    static final Set<String> arrayAtomicAddTypes = Set.of(
        "Int",
        "Long"
    );

    static final Set<String> arrayBitwiseTypes = Set.of(
        "Int",
        "Long"
    );

    static final Set<String> fpTypes = Set.of("Float", "Double");

    void generateVarHandleByteArray(String type, Path outputDirectory) throws IOException {
        String rawType = rawTypes.get(type);
        String boxType = boxTypes.get(type);
        String rawBoxType = boxTypes.get(rawType);
        String littleType = type.toLowerCase(Locale.ROOT);
        String littleRawType = rawType.toLowerCase(Locale.ROOT);
        Set<String> keys = new HashSet<>();
        if (fpTypes.contains(type)) {
            keys.add("floatingPoint");
        }
        if (arrayCasTypes.contains(type)) {
            keys.add("CAS");
        }
        if (arrayAtomicAddTypes.contains(type)) {
            keys.add("AtomicAdd");
        }
        if (arrayBitwiseTypes.contains(type)) {
            keys.add("Bitwise");
        }
        keys.add(type);
        doSpp(varHandleByteArrayViewTemplateFile.toPath(), outputDirectory.resolve("VarHandleByteArrayAs" + type + "s.java"), keys, Map.of(
            "type", littleType,
            "Type", type,
            "BoxType", boxType,
            "rawType", littleRawType,
            "RawType", rawType,
            "RawBoxType", rawBoxType
        ), false, false);
    }
}