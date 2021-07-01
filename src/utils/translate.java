package utils;

import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.util.ResourceBundle;

public class translate {
    /**
     * Takes a Label or Button object and translates its text using the given resource bundle.
     * @param lbl The text label to translate.
     * @param lang The resource bundle to use.
     */
    public static void translateText(Label lbl, ResourceBundle lang){ lbl.setText(lang.getString(lbl.getText())); }
    public static void translateText(Button btn, ResourceBundle lang) { btn.setText(lang.getString(btn.getText())); }
}
