package com.vislo.ivr.learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    EditText macchanger_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        macchanger_path = (EditText) findViewById(R.id.macchanger_full_path);

        macchanger_path.setText(Macchanger.get_mc_path());
    }

    public void back_to_main(View view) {
//        Intent to_main = new Intent(this, MainActivity.class);
//        startActivity(to_main);
        this.onBackPressed();
    }

    public void save_settings(View view) {
        String path = macchanger_path.getText().toString();
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("macchanger", path);
        editor.commit();

        Macchanger.set_mc_path(path);
        Toasty.success(this, "Changes saved", Toast.LENGTH_SHORT).show();
    }

    public void reset_macchanger_path(View view) {
        macchanger_path.setText(getString(R.string.macchanger_default));
        macchanger_path.setSelection(macchanger_path.getText().length());
    }

    public void reset_mac(View view) {
        if (Macchanger.error == 0) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {
                Toasty.warning(this, "First, turn the wifi off", Toast.LENGTH_SHORT).show();
                return;
            }

            Shell.SU.run("ifconfig wlan0 down");

            CommandResult rand_mac = Shell.SU.run(Macchanger.get_mc_path() + " -p wlan0");
            if (rand_mac.isSuccessful()) {
                Toasty.success(this, "MAC reset successfully", Toast.LENGTH_LONG).show();
            } else {
                Toasty.error(this, "Something went wrong...", Toast.LENGTH_LONG).show();
            }

            Shell.SU.run("ifconfig wlan0 up");
        } else {
            Toasty.error(this, "ERROR: check log on the main page", Toast.LENGTH_LONG).show();
        }
    }
}
