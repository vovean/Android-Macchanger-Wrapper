package com.vislo.ivr.learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    public String macchanger;

    public TextView output;
    Button get_cur_mac, set_new_mac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);
        set_new_mac = (Button) findViewById(R.id.new_mac_btn);
        get_cur_mac = (Button) findViewById(R.id.cur_mac_btn);

        output.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    protected void onResume() {
        super.onResume();
        update_macchanger_path();
    }

    void update_macchanger_path() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        Macchanger.set_mc_path(settings.getString("macchanger", getString(R.string.macchanger_default)));
        if (Macchanger.error != 0) {
            print_error();
        }
    }

    private void print_error() {
        output.append("\n");
        if (Macchanger.error == 1) {
            appendColoredText(output, "ERROR: ", getColor(R.color.colorAccent));
            output.append("Macchanger not found\n");
        } else if (Macchanger.error == 2) {
            appendColoredText(output, "ERROR: ", getColor(R.color.colorAccent));
            output.append(Shell.SU.run(Macchanger.get_mc_path()).getStderr() + "\n");
        } else{
            appendColoredText(output, "OK\n", getColor(R.color.darkGreen));
        }
    }

    public void show_current_mac(View view) {
        output.append("\n");
        if (Macchanger.error == 0) {
            CommandResult res = Shell.SU.run(Macchanger.get_mc_path() + " -s wlan0");
            if (res.isSuccessful()) {
                output.append(res.getStdout());
            } else {
                output.append(res.getStderr());
            }
            output.append("\n");
        } else {
            print_error();
        }
    }

    public void set_random_mac(View view) {
        if (Macchanger.error == 0) {
            WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {
                //Toast.makeText(this, "First, turn the wifi off", Toast.LENGTH_SHORT).show();
                Toasty.warning(this, "First, turn the wifi off", Toast.LENGTH_SHORT).show();
                return;
            }
            output.append("\n");

            CommandResult wifidown = Shell.SU.run("ifconfig wlan0 down");
            if (wifidown.isSuccessful()) {
                output.append("ifconfig wlan0 down ");
                appendColoredText(output, "SUCCESS\n", getColor(R.color.darkGreen));
            }

            CommandResult rand_mac = Shell.SU.run(Macchanger.get_mc_path() + " -r wlan0");
            if (rand_mac.isSuccessful()) {
                output.append("macchanger -r wlan0 ");
                appendColoredText(output, "SUCCESS\n", getColor(R.color.darkGreen));
            }

            CommandResult wifiup = Shell.SU.run("ifconfig wlan0 up");
            if (wifidown.isSuccessful()) {
                output.append("ifconfig wlan0 up ");
                appendColoredText(output, "SUCCESS\n", getColor(R.color.darkGreen));
            }

            Toasty.info(this, "Now you can use wifi", Toast.LENGTH_SHORT).show();
        } else {
            print_error();
        }
    }

    public void clear_output(View view) {
        output.setText("");
    }

    public static void appendColoredText(TextView tv, String text, int color) {
        int start = tv.getText().length();
        tv.append(text);
        int end = tv.getText().length();

        Spannable spannableText = (Spannable) tv.getText();
        spannableText.setSpan(new ForegroundColorSpan(color), start, end, 0);
        spannableText.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
    }

    public void to_settings(View view) {
        Intent to_settings = new Intent(this, SettingsActivity.class);
        startActivity(to_settings);
    }

    public void to_help(View view){
        Toasty.info(this, "Help", Toast.LENGTH_SHORT).show();
    }
}
