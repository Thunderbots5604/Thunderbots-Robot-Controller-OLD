package io.github.thunderbots.robotcontroller.fileloader;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import java.io.File;
import java.util.List;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class ThunderOpModeRegister {

    public static void register(OpModeManager manager) {
        ThunderLog.v("About to register op modes...");
        try {
            List<File> fileList = OpModeClassLoader.getFileSet();
            DalvikConverter.getJarList(fileList);
            DalvikConverter.convertJars(fileList);
            List<Class<? extends OpMode>> opmodeList = OpModeClassLoader.loadJars(fileList);
            ThunderLog.d("Final op mode list: " + opmodeList);
            ThunderLog.v("Now registering OpModes...");
            for (Class<? extends OpMode> opmode : opmodeList) {
                if (AnnotationReader.isActive(opmode)) {
                    try {
                        manager.register(AnnotationReader.getOpModeName(opmode), opmode);
                        ThunderLog.v("Registered " + opmode.getSimpleName());
                    } catch (Throwable ex) {
                        ThunderLog.e("Error registering op mode: " + opmode.getSimpleName());
                        ThunderLog.e(ex.getMessage());
                    }
                }
            }
        } catch (Throwable ex) {
            ThunderLog.e("Error reading external files:");
            if (ex instanceof Exception) {
                ex.printStackTrace();
            }
        }
    }

}
