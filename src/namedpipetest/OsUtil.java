package namedpipetest;

public class OsUtil {

    public static final String WINDOWS = "windows";
    public static final String MAC = "mac";
    public static final String UNIX = "unix";
    public static final String SOLARIS = "solaris";
    public static final String UNKNOWN = "unknown";

    public static String GetGeneralOsName() {
        final String OS = System.getProperty("os.name").toLowerCase();

        if (OS.contains("win"))
            return WINDOWS;
        else if (OS.contains("mac"))
            return MAC;
        else if (OS.contains("nix") ||
            OS.contains("nux") ||
            OS.contains("aix")
        )
            return UNIX;
        else if (OS.contains("sunos"))
            return SOLARIS;

        return UNKNOWN;
    }
}
