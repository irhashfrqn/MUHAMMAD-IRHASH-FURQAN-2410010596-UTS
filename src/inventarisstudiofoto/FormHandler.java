/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventarisstudiofoto;

/**
 *
 * @author irhas
 */
import javax.swing.*;
import javax.swing.text.*;

public class FormHandler {

    public static void batasiForm(JTextField textField, int batasan) {
        // Membatasi jumlah karakter yang bisa dimasukkan ke dalam JTextField
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= batasan) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length() - length) <= batasan) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
