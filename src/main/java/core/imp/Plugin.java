package core.imp;

import core.shell.ShellEntity;

import javax.swing.*;

public interface Plugin {
    void init(ShellEntity var1);

    JPanel getView();
}
