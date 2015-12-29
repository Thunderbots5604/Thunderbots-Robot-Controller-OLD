package io.github.thunderbots.robotcontroller;

import com.qualcomm.ftcrobotcontroller.FtcRobotControllerActivity;
import java.io.File;
import java.util.List;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.command.Main;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class DalvikConverter {

    private static final String OUTPUT_TO = "/thunderbots/compiled";

    public static void convertJars(List<File> jarList) {
        String[] args = {"--dex", null, null};
        for (int i = 0; i < jarList.size(); i++) {
            File f = jarList.get(i);
            //System.out.println("in: " + f);
            //System.out.println("out: " + outputFile(f));
            args[1] = "--output=" + outputFile(f).getAbsolutePath() + "";
            args[2] = "" + f.getAbsolutePath() + "";
            try {
                Main.main(args);
                jarList.set(i, outputFile(f));
            } catch (ParseException ex) {
                ThunderLog.e(f.getName() + " was probably generated using the wrong compiler!");
                ThunderLog.e("Make sure to use Java 1.6");
            }
        }
    }

    private static File outputFile(File inputFile) {
        String inputName = inputFile.getName();
        inputName = inputName.replace(" ", "-");
        File output = new File(FtcRobotControllerActivity.getPrivateFilesDirectory(), OUTPUT_TO);
        if (output.exists()) {
            output.delete();
        }
        output.mkdirs();
        return new File(output, "compiled-" + inputFile.getName());
    }

}
