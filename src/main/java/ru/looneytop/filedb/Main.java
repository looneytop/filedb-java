package ru.looneytop.filedb;

import ru.looneytop.filedb.ui.MainWindow;

import javax.swing.SwingUtilities;

/** Точка входа настольного приложения. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

