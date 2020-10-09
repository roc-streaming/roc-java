package org.rocstreaming.roctoolkit.gradle.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeBasePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("lifecycle-base");
    }
}
