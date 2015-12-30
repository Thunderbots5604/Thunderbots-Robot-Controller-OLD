/* Copyright (C) 2015-2016 Thunderbots Robotics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.thunderbots.robotcontroller;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;

import java.io.File;
import java.util.List;

import io.github.thunderbots.robotcontroller.logging.ThunderLog;

public class OpModeRegister {

    public static void register(OpModeManager manager) {
        List<File> fileList = FileLoader.getJarList();
        fileList = DalvikConverter.convertJars(fileList);
        OpModeLoader loader = new OpModeLoader(fileList);
        List<Class<? extends OpMode>> opmodeList = loader.getOpModes();
        //ThunderLog.i("Now registering op modes");
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
