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
    public DirectoryProperty getVariantDirectory() {
        return variantDirectory;
    }

    @InputFiles
    public final ConfigurableFileCollection getBuildFiles() {
        return buildFiles;
    }

    @InputFiles
    public final ConfigurableFileCollection getSrcDirs() {
        return srcDirs;
    }

    @Nested
    public final Property<Target> getTarget() {
        return target;
    }

    @Input
    public final Property<String> getGenerator() {
        return generator;
    }

    @Internal
    public DirectoryProperty getProjectDirectory() {
        return projectDirectory;
    }

    @OutputDirectory
    public DirectoryProperty getOutputDirectory() {
        return outputDirectory;
    }

    public void generatedBy(final TaskProvider<? extends Task> task) {
        getVariantDirectory().set(task.flatMap(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).getVariantDirectory();
            } else {
                throw new IllegalArgumentException(getClass().getName() + " task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
        getOutputDirectory().set(task.flatMap(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).getVariantDirectory();
            } else {
                throw new IllegalArgumentException(getClass().getName() + " task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));

        dependsOn(task);

        getBuildFiles().setFrom(task.map(it -> {
            if (it instanceof CMake) {
                return ((CMake) it).getCmakeFiles();
            } else {
                throw new IllegalArgumentException(getClass().getName() + " task cannot extract build information from \'" + it.getClass().getName() + "\' task");
            }
        }));
    }

    @TaskAction
    public void executeBuild() {
        final String executable = getExecutable();
        getProject().exec(execSpec -> {
            execSpec.setWorkingDir(getVariantDirectory().dir(String.format("%s/%s", getTarget().get().getHost().get(), getTarget().get().getPlatform().get())));
            execSpec.setExecutable(executable);
        });
    }

    @Internal
    public String getExecutable() {
        switch (getGenerator().get()) {
            case GENERATOR.MAKE:
                return System.getenv().getOrDefault("MAKE_EXECUTABLE", "make");
            case GENERATOR.NINJA:
                return System.getenv().getOrDefault("NINJA_EXECUTABLE", "ninja");
            default:
                return null;
        }
    }
}
