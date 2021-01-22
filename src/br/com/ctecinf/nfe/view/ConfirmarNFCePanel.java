/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.nfe.view;

import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

/**
 *
 * @author Cássio Conceição
 * @since 29/05/2019
 * @version 201905
 * @see http://ctecinf.com.br/
 */
public class ConfirmarNFCePanel extends JPanel {

    private final TNFe nfe;
    private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) DecimalFormat.getNumberInstance(new Locale("pt", "BR"));

    static {
        DECIMAL_FORMAT.setMinimumFractionDigits(2);
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }

    public ConfirmarNFCePanel(TNFe nfe) {
        super(new BorderLayout());
        this.nfe = nfe;
        try {
            initUI();
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, ex, "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() throws ParseException {

        add(new JLabel("Confere as informações descritas acima?"), BorderLayout.SOUTH);

        JEditorPane editor = new JEditorPane("text/html", danfeHTML());
        editor.setEditable(false);
        editor.setBackground(new Color(255, 250, 198));

        add(editor, BorderLayout.CENTER);
    }

    private String head() throws ParseException {

        String cnpj = nfe.getInfNFe().getEmit().getCNPJ();
        String razaoSocial = nfe.getInfNFe().getEmit().getXNome();
        String endereco = nfe.getInfNFe().getEmit().getEnderEmit().getXLgr();
        endereco += nfe.getInfNFe().getEmit().getEnderEmit().getNro() != null ? ", " + nfe.getInfNFe().getEmit().getEnderEmit().getNro() : "";
        endereco += nfe.getInfNFe().getEmit().getEnderEmit().getXCpl() != null ? " - " + nfe.getInfNFe().getEmit().getEnderEmit().getXCpl() : "";
        endereco += nfe.getInfNFe().getEmit().getEnderEmit().getXBairro() != null ? ", " + nfe.getInfNFe().getEmit().getEnderEmit().getXBairro() : "";
        endereco += nfe.getInfNFe().getEmit().getEnderEmit().getXMun() != null ? ", " + nfe.getInfNFe().getEmit().getEnderEmit().getXMun() : "";
        endereco += " - " + nfe.getInfNFe().getEmit().getEnderEmit().getUF().value();

        return "                <p align=\"center\">CNPJ " + new MaskFormatter("##.###.###/####-##").valueToString(cnpj) + " <b>" + razaoSocial + "</b><br />\n"
                + "                " + endereco + "<br />\n"
                + "                Documento Auxiliar da Nota Fiscal de Consumidor Eletrônica</p><br />\n";
    }

    private String produtos() {

        String cod;
        String desc;
        String qtde;
        String und;
        String vlUnit;
        String total;

        String str = "";

        for (TNFe.InfNFe.Det det : nfe.getInfNFe().getDet()) {

            TNFe.InfNFe.Det.Prod prod = det.getProd();

            cod = prod.getCProd();
            desc = prod.getXProd();
            qtde = prod.getQCom();
            und = prod.getUCom();
            vlUnit = DECIMAL_FORMAT.format(Double.parseDouble(prod.getVUnCom()));
            total = DECIMAL_FORMAT.format(Double.parseDouble(prod.getVProd()));

            str += "                <tr>\n"
                    + "                    <td>" + cod + "</td><td>" + desc + "</td><td>" + qtde + "</td><td>" + und + "</td><td align=\"right\">" + vlUnit + "</td><td align=\"right\">" + total + "</td>\n"
                    + "                </tr>\n";
        }

        return str;
    }

    private String pagamentos() {

        String desc;
        String valor;

        String str = "";

        for (TNFe.InfNFe.Pag.DetPag detPag : nfe.getInfNFe().getPag().getDetPag()) {

            desc = getDescPagto(detPag.getTPag());

            valor = DECIMAL_FORMAT.format(Double.parseDouble(detPag.getVPag()));

            str += "                <tr>\n"
                    + "                    <td align=\"left\">" + desc + "</td><td align=\"right\">" + valor + "</td>\n"
                    + "                </tr>\n";
        }

        return str;
    }

    private String getDescPagto(String tPag) {

        switch (tPag) {

            case "01":
                return "Dinheiro";

            case "02":
                return "Cheque";

            case "03":
                return "Catao de Credito";

            case "04":
                return "Cartao de Debito";

            case "05":
                return "Credito Loja";

            default:
                return "Outros";
        }
    }

    private String danfeHTML() throws ParseException {

        String str = "<div style=\"width:320px;font-size:9px\">\n"
                + "            \n"
                + head()
                + "            \n"
                + "            <table style=\"width:100%;font-size:8px\">\n"
                + "                <tr>\n"
                + "                    <th width=\"10%\" align=\"left\">Cód.</th><th width=\"40%\" align=\"left\">Descrição</th><th width=\"10%\" align=\"left\">Qtde.</th><th width=\"10%\" align=\"left\">UN</th><th width=\"15%\" align=\"right\">Vl. Unit.</th><th width=\"15%\" align=\"right\">Vl. Total</th>\n"
                + "                </tr>\n"
                + produtos()
                + "            </table>\n"
                + "            \n"
                + "            <table style=\"width:100%;font-size:9px\">\n"
                + "                \n"
                + "                <tr>\n"
                + "                    <td align=\"left\">Qtde. total de itens</td><td align=\"right\">" + nfe.getInfNFe().getDet().size() + "</td>\n"
                + "                </tr>\n"
                + "                \n"
                + "                <tr>\n"
                + "                    <td align=\"left\">Valor total R$</td><td align=\"right\">" + DECIMAL_FORMAT.format(Double.parseDouble(nfe.getInfNFe().getTotal().getICMSTot().getVProd())) + "</td>\n"
                + "                </tr>\n"
                + "                \n"
                + "                <tr>\n"
                + "                    <td align=\"left\">Desconto R$</td><td align=\"right\">" + DECIMAL_FORMAT.format(Double.parseDouble(nfe.getInfNFe().getTotal().getICMSTot().getVDesc())) + "</td>\n"
                + "                </tr>\n"
                + "                \n"
                + "                <tr>\n"
                + "                    <th align=\"left\">Valor à pagar R$</th><th align=\"right\">" + DECIMAL_FORMAT.format(Double.parseDouble(nfe.getInfNFe().getTotal().getICMSTot().getVNF())) + "</th>\n"
                + "                </tr>\n"
                + "                \n"
                + "            </table>\n"
                + "            \n"
                + "            <table style=\"width:100%;font-size:9px\">\n"
                + "                \n"
                + "                <tr>\n"
                + "                    <td align=\"left\">FORMA PAGAMENTO</td><td align=\"right\">VALOR PAGO R$</td>\n"
                + "                </tr>\n"
                + "                \n"
                + pagamentos()
                + "                \n"
                + "            </table>\n"
                + "            \n"
                + "            <table style=\"width:100%;font-size:9px\">\n"
                + "                \n"
                + "                <tr>\n"
                + "                    <td align=\"left\">Troco R$</td><td align=\"right\">" + DECIMAL_FORMAT.format(nfe.getInfNFe().getPag().getVTroco() == null ? "0" : Double.parseDouble(nfe.getInfNFe().getPag().getVTroco())) + "</td>\n"
                + "                </tr>\n"
                + "                \n"
                + "            </table>\n"
                + "            \n";

        if (nfe.getInfNFe().getDest() != null) {

            String nome = nfe.getInfNFe().getDest().getXNome();

            if (nfe.getInfNFe().getDest().getCPF() != null) {

                String cpf = nfe.getInfNFe().getDest().getCPF();

                str += "<p align=\"center\"><b>CONSUMIDOR - CPF " + new MaskFormatter("###.###.###-##").valueToString(cpf) + "</b>";

            } else if (nfe.getInfNFe().getDest().getIdEstrangeiro() != null) {

                String idEstrangeiro = nfe.getInfNFe().getDest().getCPF();

                str += "<p align=\"center\"><b>CONSUMIDOR - ID ESTRANGEIRO " + idEstrangeiro + "</b>";
            }

            if (nome != null && !nome.isEmpty()) {
                str += "<br />" + nome;
            }

            str += "</p>";

        } else {
            str += "<p align=\"center\"><b>CONSUMIDOR NÃO IDENTIFICADO</b></p>";
        }

        return str;
    }
}
