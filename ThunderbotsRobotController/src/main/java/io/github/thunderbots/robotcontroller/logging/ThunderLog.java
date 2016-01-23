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

package io.github.thunderbots.robotcontroller.logging;

import android.util.Log;

/**
 * Contains delegates to Android's standard Log methods, but inserts necessary tag information.
 */
public class ThunderLog {

    public static final String THUNDERBOTS_TAG = "Thunderbots";

    public static void d(String msg) {
        Log.d(THUNDERBOTS_TAG, msg);
    }

    public static void w(String msg) {
        Log.w(THUNDERBOTS_TAG, msg);
    }

    public static void e(String msg) {
        Log.e(THUNDERBOTS_TAG, msg);
    }

    public static void i(String msg) {
        Log.i(THUNDERBOTS_TAG, msg);
    }

    public static void v(String msg) {
        Log.v(THUNDERBOTS_TAG, msg);
    }

    public static void wtf(String msg) {
        Log.wtf(THUNDERBOTS_TAG, msg);
    }

    public static void d(String msg, Throwable ex) {
        Log.d(THUNDERBOTS_TAG, msg, ex);
    }

    public static void w(String msg, Throwable ex) {
        Log.w(THUNDERBOTS_TAG, msg, ex);
    }

    public static void e(String msg, Throwable ex) {
        Log.e(THUNDERBOTS_TAG, msg, ex);
    }

    public static void i(String msg, Throwable ex) {
        Log.i(THUNDERBOTS_TAG, msg, ex);
    }

    public static void v(String msg, Throwable ex) {
        Log.v(THUNDERBOTS_TAG, msg, ex);
    }

    public static void wtf(String msg, Throwable ex) {
        Log.wtf(THUNDERBOTS_TAG, msg, ex);
    }

}
