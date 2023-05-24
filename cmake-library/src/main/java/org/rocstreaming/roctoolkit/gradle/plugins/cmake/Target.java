package org.rocstreaming.roctoolkit.gradle.plugins.cmake;

import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import org.rocstreaming.roctoolkit.util.os.OperatingSystem;

public class Target {
    final Property<String> host;
    final Property<String> platform;
    final Property<Boolean> isCrossCompiling;

    public Target(ProjectLayout projectLayout, ObjectFactory objectFactory) {
        final OperatingSystem currentOs = OperatingSystem.current();
        host = objectFactory.property(String.class).convention(currentOs.getName());
        platform = objectFactory.property(String.class).convention(currentOs.getArch());
        isCrossCompiling = objectFactory.property(Boolean.class).convention(false);
    }

    @Input
    public final Property<String> getHost() {
        return host;
    }

    public void setHost(String host) {
        if (host != null && !host.equals("") && !this.host.get().equals(host)) {
            if (OperatingSystem.forName(host) == null) {
                throw new IllegalArgumentException("os \"" + host + "\" is invalid");
            }
            this.host.set(host);
            if (!isCrossCompiling.get()) {
                isCrossCompiling.set(true);
                isCrossCompiling.disallowChanges();
            }
        }
    }

    public void host(String host) {
        setHost(host);
    }

    @Input
    public final Property<String> getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        if (platform != null && !platform.equals("") && !platform.equals("null") && !this.platform.get().equals(platform)) {
            this.platform.set(platform);
            if (!isCrossCompiling.get()) {
                isCrossCompiling.set(true);
                isCrossCompiling.disallowChanges();
            }
        }
    }

    public void platform(String platform) {
        setPlatform(platform);
    }

    @Internal
    public final Property<Boolean> getIsCrossCompiling() {
        return isCrossCompiling;
    }
}
