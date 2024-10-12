package org.rocstreaming.roctoolkit.gradle.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFiles;
import org.gradle.api.tasks.TaskAction;
import org.rocstreaming.roctoolkit.gradle.plugins.cmake.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Task types to execute CMake
 */
public class CMake extends DefaultTask {
    private String buildType;
    private final Property<Target> target = getProject().getObjects().property(Target.class);
    private final ListProperty<String> arguments = getProject().getObjects().listProperty(String.class);
    private final DirectoryProperty variantDirectory = getProject().getObjects().directoryProperty();
    private final DirectoryProperty projectDirectory = getProject().getObjects().directoryProperty();
    private final Property<String> generator = getProject().getObjects().property(String.class);

    @TaskAction
    public void generateCmakeFiles() {
        if (target.get().getIsCrossCompiling().get() && System.getenv("CMAKE_TOOLCHAIN_FILE") == null) {
            throw new IllegalArgumentException("CMAKE_TOOLCHAIN_FILE env variable is not set.");
        }

        String cmakeExecutable = System.getenv().getOrDefault("CMAKE_EXECUTABLE", "cmake");
        Provider<Directory> targetDirectory = variantDirectory.dir(
            String.format("%s/%s", getTarget().get().getHost().get(),
                          getTarget().get().getPlatform().get()));
        targetDirectory.get().getAsFile().mkdirs();

        ArrayList<String> cliArgs = new ArrayList<>();
        cliArgs.add(cmakeExecutable);
        cliArgs.add("-DCMAKE_BUILD_TYPE=" + capitalize(getBuildType()));
        if (!getArguments().get().isEmpty()) {
            cliArgs.add(String.join(" ", getArguments().get()));
        }
        cliArgs.add("-G" + generator.get());
        cliArgs.add("-DCMAKE_TOOLCHAIN_FILE=" +
                    System.getenv().getOrDefault("CMAKE_TOOLCHAIN_FILE", ""));
        cliArgs.add("--no-warn-unused-cli");
        cliArgs.add(getProjectDirectory().get().getAsFile().getAbsolutePath());

        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(targetDirectory);
            execSpec.commandLine(cliArgs);
        });
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Nested
    public final Property<Target> getTarget() {
        return target;
    }

    @Input
    public Property<String> getGenerator() {
        return generator;
    }

    @InputFiles
    public FileCollection getCMakeLists() {
        return getProject().fileTree(projectDirectory, it -> it.include("**/CMakeLists.txt"));
    }

    @OutputFiles
    public FileCollection getCmakeFiles() {
        return getProject().fileTree(variantDirectory, it -> it.include("**/CMakeFiles/**/*")
                                                                .include("**/Makefile")
                                                                .include("**/*.ninja")
                                                                .include("**/*.cmake"));
    }

    @Input
    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    @Input
    public ListProperty<String> getArguments() {
        return arguments;
    }

    @Internal
    public DirectoryProperty getVariantDirectory() {
        return variantDirectory;
    }

    @Internal
    public DirectoryProperty getProjectDirectory() {
        return projectDirectory;
    }
}
