package org.rocstreaming.roctoolkit.gradle.tasks;

import org.gradle.api.Task;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;

import static org.rocstreaming.roctoolkit.gradle.plugins.cmake.CMakeExtension.GENERATOR;
import org.rocstreaming.roctoolkit.gradle.plugins.cmake.Target;

public abstract class Builder extends DefaultTask {
    private final DirectoryProperty variantDirectory = getProject().getObjects().directoryProperty();
    private final DirectoryProperty outputDirectory = getProject().getObjects().directoryProperty();
    private final DirectoryProperty projectDirectory = getProject().getObjects().directoryProperty();
    private final ConfigurableFileCollection srcDirs = getProject().files();
    private final ConfigurableFileCollection buildFiles = getProject().files();
    private final Property<Target> target = getProject().getObjects().property(Target.class);
    private final Property<String> generator = getProject().getObjects().property(String.class);

    @Internal
    DirectoryProperty variantDirectory() {
        return variantDirectory;
    }

    @InputFiles
    public final ConfigurableFileCollection buildFiles() {
        return buildFiles;
    }

    @InputFiles
    public final ConfigurableFileCollection srcDirs() {
        return srcDirs;
    }

    @Nested
    public final Property<Target> target() {
        return target;
    }

    @Input
    public final Property<String> generator() {
        return generator;
    }

    @Internal
    DirectoryProperty projectDirectory() {
        return projectDirectory;
    }

    @OutputDirectory
    DirectoryProperty outputDirectory() {
        return outputDirectory;
    }

    public void generatedBy(final TaskProvider<? extends Task> task) {
        variantDirectory().set(task.flatMap(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).variantDirectory();
            } else {
                throw new IllegalArgumentException(getClass().getName() + " task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
        outputDirectory().set(task.flatMap(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).variantDirectory();
            } else {
                throw new IllegalArgumentException(getClass().getName() + " task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));

        dependsOn(task);

        buildFiles().setFrom(task.map(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).cmakeFiles();
            } else {
                throw new IllegalArgumentException(getClass().getName() + " task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
    }

    @TaskAction
    public void executeBuild() {
        final String executable = getExecutable();
        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(variantDirectory().dir(String.format("%s/%s", target().get().getHost().get(), target().get().getPlatform().get())));
            execSpec.setExecutable(executable);
        });
    }

    @Internal
    public String getExecutable() {
        switch (generator().get()) {
            case GENERATOR.MAKE:
                return System.getenv().getOrDefault("MAKE_EXECUTABLE", "make");
            case GENERATOR.NINJA:
                return System.getenv().getOrDefault("NINJA_EXECUTABLE", "ninja");
            default:
                return null;
        }
    }
}
