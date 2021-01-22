/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.nfe.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author Cássio Conceição
 * @param <T>
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br
 */
public abstract class WaitDialog<T> extends JDialog {

    private JProgressBar progressBar;

    /**
     * Construtor
     */
    public WaitDialog() {
        this(null);
    }

    /**
     * Construtor
     *
     * @param owner
     */
    public WaitDialog(Frame owner) {
        super(owner);
        init();
    }

    /**
     * Inicia interface
     */
    private void init() {

        if (getOwner() != null && !getOwner().isVisible()) {
            getOwner().setVisible(true);
        }

        try {
            progressBar = new JProgressBar();
            progressBar.setPreferredSize(new Dimension(210, 20));
        } catch (Exception ex) {
        }

        setTitle("Aguarde...");

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.add(new JPanel(), BorderLayout.NORTH);
        panel.add(new JPanel(), BorderLayout.SOUTH);
        panel.add(new JPanel(), BorderLayout.EAST);
        panel.add(new JPanel(), BorderLayout.WEST);

        panel.add(progressBar, BorderLayout.CENTER);

        add(panel);

        pack();
        setAlwaysOnTop(true);
        setAutoRequestFocus(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Inicia o processo longo
     */
    public void start() {

        new Thread(() -> {
            try {
                progressBar.setIndeterminate(true);
                T obj = process();
                WaitDialog.this.dispose();
                WaitDialog.this.endProcess(obj);
            } catch (Exception ex) {
                WaitDialog.this.dispose();
                JOptionPane.showMessageDialog(getOwner(), ex, "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    /**
     * Implementar processo longo background
     *
     * @return
     * @throws java.lang.Exception
     */
    public abstract T process() throws Exception;

    /**
     * Implementar finalização do processo longo
     *
     * @param result Objeto de retorno do método exec();
     * @throws java.lang.Exception
     */
    public abstract void endProcess(T result) throws Exception;
}
