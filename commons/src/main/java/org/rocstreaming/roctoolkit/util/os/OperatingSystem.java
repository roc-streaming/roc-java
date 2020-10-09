package org.rocstreaming.roctoolkit.util.os;

import java.util.Locale;

public abstract class OperatingSystem {
    public static final Windows WINDOWS = new Windows();
    public static final MacOs MAC_OS = new MacOs();
    public static final Solaris SOLARIS = new Solaris();
    public static final Linux LINUX = new Linux();
    public static final FreeBSD FREE_BSD = new FreeBSD();
    public static final Unix UNIX = new Unix();
    private static OperatingSystem currentOs = forName(System.getProperty("os.name"));
    private final String osName;
    protected String osArch;
    
    public OperatingSystem() {
        osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        osArch = System.getProperty("os.arch");
    }

    public static OperatingSystem current() {
        return currentOs;
    }

    public static OperatingSystem forName(String os) {
        String osName = os.toLowerCase(Locale.ENGLISH);
        if (osName.contains("windows")) {
            return WINDOWS;
        } else if (osName.contains("mac os x") || osName.contains("darwin") || osName.contains("osx")) {
            return MAC_OS;
        } else if (osName.contains("sunos") || osName.contains("solaris")) {
            return SOLARIS;
        } else if (osName.contains("linux")) {
            return LINUX;
        } else if (osName.contains("freebsd")) {
            return FREE_BSD;
        } else {
            return null;
        }
    }

    public String getName() {
        return osName;
    }

    public String getArch() {
        return osArch;
    }

    public boolean isWindows() {
        return false;
    }

    public boolean isUnix() {
        return false;
    }

    public boolean isMacOsX() {
        return false;
    }

    public boolean isLinux() {
        return false;
    }

    public boolean isSolaris() {
        return false;
    }

    public boolean isFreeBSD() {
        return false;
    }

    public abstract String getFamilyName();

    static class Windows extends OperatingSystem {
        @Override
        public boolean isWindows() {
            return true;
        }

        @Override
        public String getFamilyName() {
            return "windows";
        }

        @Override
        public String getName() {
            return "win32";
        }
    }

    static class Unix extends OperatingSystem {
        Unix() {
            String arch = System.getProperty("os.arch");
            if ("x86".equals(arch)) {
                this.osArch = "i386";
            }
            if ("x86_64".equals(arch)) {
                this.osArch = "amd64";
            }
            if ("powerpc".equals(arch)) {
                this.osArch = "ppc";
            }
        }

        @Override
        public boolean isUnix() {
            return true;
        }

        @Override
        public String getFamilyName() {
            return "unknown";
        }
    }

    static class MacOs extends Unix {
        @Override
        public boolean isMacOsX() {
            return true;
        }

        @Override
        public String getFamilyName() {
            return "os x";
        }

        @Override
        public String getName() {
            return "darwin";
        }
    }

    static class Linux extends Unix {
        @Override
        public boolean isLinux() {
            return true;
        }

        @Override
        public String getFamilyName() {
            return "linux";
        }

        @Override
        public String getName() {
            return "linux";
        }
    }

    static class FreeBSD extends Unix {
        @Override
        public boolean isFreeBSD() {
            return true;
        }

        @Override
        public String getFamilyName() {
            return "unix";
        }

        @Override
        public String getName() {
            return "freebsd";
        }
    }

    static class Solaris extends Unix {
        Solaris() {
            super();
            String arch = System.getProperty("os.arch");
            if (arch.equals("i386") || arch.equals("x86")) {
                this.osArch = "x86";
            }
        }

        @Override
        public boolean isSolaris() {
            return true;
        }

        @Override
        public String getFamilyName() {
            return "solaris";
        }

        @Override
        public String getName() {
            return "solaris";
        }
    }
}
