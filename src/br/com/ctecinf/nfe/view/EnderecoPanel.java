/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.nfe.view;

import br.com.ctecinf.nfe.model.Municipio;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author cassio
 */
public class EnderecoPanel extends JPanel {

    private AutoCompleteField<Municipio> municipio;
    private JFormattedTextField logradouro;
    private JFormattedTextField numero;
    private JFormattedTextField complemento;
    private JFormattedTextField bairro;
    private JFormattedTextField cep;

    public EnderecoPanel() {
        init();
    }

    private void init() {

        setLayout(new GridLayout(6, 1));

        municipio = new AutoCompleteField(new Municipio().getAutoCompleteModel());
        setLabel(municipio, "Município", true);
        add(municipio);

        logradouro = new JFormattedTextField(new UpperCaseFormatter());
        setLabel(logradouro, "Logradouro", true);
        add(logradouro);

        numero = new JFormattedTextField(new NumberFormatter(0));
        setLabel(numero, "Número", true);
        add(numero);

        complemento = new JFormattedTextField(new UpperCaseFormatter());
        setLabel(complemento, "Complemento", false);
        add(complemento);

        bairro = new JFormattedTextField(new UpperCaseFormatter());
        setLabel(bairro, "Bairro", true);
        add(bairro);

        cep = new JFormattedTextField(new NumberFormatter(0));
        setLabel(cep, "CEP", false);
        add(cep);
    }

    public void setLabel(JComponent component, String label, boolean notNull) {
        component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(null, " " + (notNull ? "* " : "") + label + " ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, component.getFont().deriveFont(Font.BOLD), notNull ? Color.RED : Color.BLACK), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    }

    public Municipio getMunicipio() {
        return municipio.getValue();
    }

    public String getLogradouro() {
        return logradouro.getValue() == null ? null : (String) logradouro.getValue();
    }

    public Number getNumero() {
        return numero.getValue() == null ? null : (Number) numero.getValue();
    }

    public String getComplemento() {
        return complemento.getValue() == null ? null : (String) complemento.getValue();
    }

    public String getBairro() {
        return bairro.getValue() == null ? null : (String) bairro.getValue();
    }

    public Number getCep() {
        return cep.getValue() == null ? null : (Number) cep.getValue();
    }

}
