package com.senpure.tail.fx;

import com.senpure.javafx.Controller;
import com.senpure.javafx.Javafx;
import com.senpure.tail.Command;
import com.senpure.tail.Tail;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * MainController
 *
 * @author senpure
 * @time 2020-08-18 17:48:04
 */
@Controller
public class MainController implements Initializable, ApplicationRunner {

    @FXML
    private TextArea textArea;


    private String[] args;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Tail tail = new Tail((line, newLine) -> Platform.runLater(() -> {

            textArea.appendText(line);
            if (newLine) {
                textArea.appendText("\n");
            }
        }));
        Command command = new Command();

        Platform.runLater(() -> {
            Stage stage = Javafx.getPrimaryStage();
            StringBuilder sb = new StringBuilder();

            sb.append("[");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(args[i]);
            }
            sb.append("]");
            if (sb.length() > 20) {
              //  sb.delete(1, 19);
              //  sb.insert(1, "...");
            }
            String title = stage.getTitle() + " " + sb.toString();

           // stage.setTitle(title);
        });
        command.execute(tail, args);
    }


    @Override
    public void run(ApplicationArguments args) {
        this.args = args.getSourceArgs();

    }
}
