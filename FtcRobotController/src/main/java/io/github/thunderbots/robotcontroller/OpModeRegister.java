package io.github.thunderbots.robotcontroller;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import java.io.File;
import java.util.List;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class OpModeRegister {

    public static void register(OpModeManager manager) {
        List<File> fileList = FileLoader.getJarList();
        DalvikConverter.convertJars(fileList);
        List<Class<? extends OpMode>> opmodeList = OpModeClassLoader.loadJars(fileList);
        ThunderLog.i("Now registering op modes");
        for (Class<? extends OpMode> opmode : opmodeList) {
            if (AnnotationReader.isActive(opmode)) {
                try {
                    manager.register(AnnotationReader.getOpModeName(opmode), opmode);
                    ThunderLog.i("Registered " + opmode.getSimpleName());
                } catch (Throwable ex) {
                    ThunderLog.e("Error registering op mode: " + opmode.getSimpleName());
                    ThunderLog.e(ex.getMessage());
                }
            }
        }
    }

}
