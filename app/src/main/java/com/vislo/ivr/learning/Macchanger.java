package com.vislo.ivr.learning;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;

public class Macchanger {

    public static String mc_path;

    /**
     * 0 - no error
     * 1 - not found
     * 2 - other error
     */
    public static int error;


    private static void check_mc_path() {
        CommandResult res = Shell.SU.run(mc_path);
        if (res.isSuccessful()) {
            error = 0;
        } else {
            if (res.getStderr().contains("not found")) {
                error = 1;
            } else {
                error = 2;
            }
        }
    }

    public static void set_mc_path(String path) {
        mc_path = path;
        check_mc_path();
    }

    public static String get_mc_path() {
        return mc_path;
    }
}
