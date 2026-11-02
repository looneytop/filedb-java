package ru.looneytop.filedb.ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Начальное окно. Формы и действия добавим после реализации файлового хранения. */
public final class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    public MainWindow() {
        super("Файловая база данных");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(760, 480));
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTable recordsTable = new JTable();
        recordsTable.setFillsViewportHeight(true);
        add(new JScrollPane(recordsTable), BorderLayout.CENTER);

        JLabel status = new JLabel("База не открыта");
        status.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        add(status, BorderLayout.SOUTH);
    }
}

