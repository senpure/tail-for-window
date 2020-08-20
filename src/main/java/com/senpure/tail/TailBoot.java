package com.senpure.tail;

import com.senpure.javafx.JavafxBoot;
import com.senpure.javafx.JavafxSpringBootApplication;
import com.senpure.javafx.SpringBefore;
import com.senpure.tail.fx.MainView;

/**
 * TailBoot
 *
 * @author senpure
 * @time 2020-08-18 17:47:06
 */
@JavafxBoot
public class TailBoot extends JavafxSpringBootApplication  {


    public static void main(String[] args) {

        launch(TailBoot.class, MainView.class, new SpringBefore() {

            @Override
            public boolean isSplash() {
                return false;
            }

            @Override
            public boolean isInstallAnsiConsole() {
                return false;
            }
        }, args);
    }

    @Override
    protected void showPrimaryStage() {
        super.showPrimaryStage();

    }
}
