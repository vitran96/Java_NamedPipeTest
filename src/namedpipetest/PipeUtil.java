package namedpipetest;

public final class PipeUtil {

    public static String MakePipeName(String name) {
        if (OsUtil.GetGeneralOsName().equals(OsUtil.WINDOWS))
            return "\\\\.\\pipe\\" + name;

        return name;
    }
}
