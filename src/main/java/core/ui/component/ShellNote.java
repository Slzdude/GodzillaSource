package core.ui.component;

import core.Db;
import core.shell.ShellEntity;
import util.Log;
import util.functions;

import javax.swing.*;
import java.awt.*;

public class ShellNote extends JPanel {
    private final ShellEntity shellEntity;
    private final String shellId;
    private final String lastNoteMd5;
    private final RTextArea textArea;
    private final boolean state;
    private String noteData;

    public ShellNote(ShellEntity entity) {
        this.shellEntity = entity;
        this.shellId = this.shellEntity.getId();
        super.setLayout(new BorderLayout(1, 1));
        String noteData = Db.getShellNote(this.shellId);
        this.lastNoteMd5 = functions.md5(noteData);
        this.textArea = new RTextArea();
        this.textArea.setText(noteData);
        this.state = true;
        Thread thread = new Thread(() -> {
            while (ShellNote.this.state) {
                try {
                    Thread.sleep(10000L);
                    ShellNote.this.updateDbNote();
                } catch (InterruptedException var2) {
                    Log.error(var2);
                }
            }

        });
        thread.start();
        super.add(new JScrollPane(this.textArea));
    }

    public void updateDbNote() {
        String noteData = this.textArea.getText();
        String md5 = functions.md5(noteData);
        if (!this.lastNoteMd5.equals(md5)) {
            Db.updateShellNote(this.shellId, noteData);
        }

    }
}
