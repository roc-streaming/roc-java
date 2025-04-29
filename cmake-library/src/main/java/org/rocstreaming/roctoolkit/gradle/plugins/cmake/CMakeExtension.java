package org.rocstreaming.roctoolkit.gradle.plugins.cmake;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.Action;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class CMakeExtension {
    public static final class GENERATOR {
        public static final String MAKE = "Unix Makefiles";
        public static final String NINJA = "Ninja";
    };

    private final Target target;
    private final DirectoryProperty projectDirectory;
    private final ListProperty<String> srcDirs;
    private final ListProperty<String> arguments;
    private final Property<String> generator;

    @Inject
    public CMakeExtension(ProjectLayout projectLayout, ObjectFactory objectFactory) {
        target = new Target(projectLayout, objectFactory);
        projectDirectory = objectFactory.directoryProperty().convention(projectLayout.getProjectDirectory());
        srcDirs = objectFactory.listProperty(String.class);
        arguments = objectFactory.listProperty(String.class);
        generator = objectFactory.property(String.class).convention(GENERATOR.MAKE);
    }

    public void target(Action<? super Target> action) {
        action.execute(target);
    }

    public final Target getTarget() {
        return target;
    }

    public final DirectoryProperty getProjectDirectory() {
        return projectDirectory;
    }

    public final ListProperty<String> getSrcDirs() {
        return srcDirs;
    }

    public void srcDirs(File... dirs) {
        srcDirs.addAll(Arrays.asList(dirs).stream().map(dir -> dir.getAbsolutePath()).collect(Collectors.toList()));
    }

    public void srcDirs(String... dirs) {
        srcDirs.addAll(dirs);
    }

    public final ListProperty<String> getArguments() {
        return arguments;
    }

    public void setArguments(String... args) {
        arguments.addAll(Arrays.asList(args).stream().filter(a -> {
            String[] splitted = a.split("=");
            return (splitted.length == 1) || (splitted.length == 2 && !splitted[1].equals("null"));
        }).collect(Collectors.toList()));
    }

    public final Property<String> getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        if (generator != null) {
            if (generator.equals("make"))
                this.generator.set(GENERATOR.MAKE);
            else if (generator.equals("ninja"))
                this.generator.set(GENERATOR.NINJA);
            else
                throw new IllegalArgumentException("generator '" + generator + "' is not supported. Choose one from 'make' or 'ninja'.");
        }
    }
}
