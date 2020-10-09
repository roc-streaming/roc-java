package org.rocstreaming.roctoolkit.gradle.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(NativeBasePlugin.class);
    }
}
