package org.rocstreaming.roctoolkit.gradle.plugins.cmake;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.attributes.Attribute;
import org.rocstreaming.roctoolkit.gradle.tasks.CMake;
import org.rocstreaming.roctoolkit.gradle.tasks.Builder;
import org.rocstreaming.roctoolkit.gradle.plugins.NativeLibraryPlugin;

import java.util.Arrays;

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of
 * dependency management.
 */
public class CMakeLibraryPlugin implements Plugin<Project> {
    public void apply(final Project project) {
        project.getPlugins().apply(NativeLibraryPlugin.class);

        // Add a CMake extension to the Gradle model
        final CMakeExtension extension = project.getExtensions().create("cmake", CMakeExtension.class,
                project.getLayout(), project.getObjects());
        extension.getSrcDirs().convention(Arrays.asList("src/main/cpp", "src/main/public", "src/main/headers"));

        /*
         * Create some tasks to drive the CMake build
         */
        TaskContainer tasks = project.getTasks();

        final TaskProvider<CMake> cmakeDebug = tasks.register("cmakeDebug", CMake.class, task -> {
            task.setBuildType("Debug");
            task.getTarget().set(extension.getTarget());
            task.getArguments().set(extension.getArguements());
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("debug"));
            task.getProjectDirectory().set(extension.getProjectDirectory());
            task.getGenerator().set(extension.getGenerator());
        });

        final TaskProvider<CMake> cmakeRelease = tasks.register("cmakeRelease", CMake.class, task -> {
            task.setBuildType("RelWithDebInfo");
            task.getTarget().set(extension.getTarget());
            task.getArguments().set(extension.getArguements());
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("release"));
            task.getProjectDirectory().set(extension.getProjectDirectory());
            task.getGenerator().set(extension.getGenerator());
        });

        final TaskProvider<Builder> assembleDebug = tasks.register("assembleDebug", Builder.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the debug binaries");
            task.generatedBy(cmakeDebug);
            task.getTarget().set(extension.getTarget());
            task.getProjectDirectory().set(extension.getProjectDirectory());
            task.getSrcDirs().setFrom(extension.getSrcDirs());
            task.getGenerator().set(extension.getGenerator());
        });

        final TaskProvider<Builder> assembleRelease = tasks.register("assembleRelease", Builder.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the release binaries");
            task.generatedBy(cmakeRelease);
            task.getTarget().set(extension.getTarget());
            task.getProjectDirectory().set(extension.getProjectDirectory());
            task.getSrcDirs().setFrom(extension.getSrcDirs());
            task.getGenerator().set(extension.getGenerator());
        });

        tasks.named("assemble", task -> task.dependsOn(assembleDebug));
    }
}
