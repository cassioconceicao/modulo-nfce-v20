/*
 * Copyright (C) 2020 ctecinf.com.br
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ctecinf.nfe.view;

import br.com.ctecinf.nfe.NFeException;
import br.com.ctecinf.nfe.SOAP;
import br.com.ctecinf.nfe.model.Cartao;
import br.com.ctecinf.nfe.model.Cliente;
import br.com.ctecinf.nfe.model.Endereco;
import br.com.ctecinf.nfe.model.NFCeSimplesNacional;
import br.com.ctecinf.nfe.model.Pagamento;
import br.com.ctecinf.nfe.model.Produto;
import br.inf.portalfiscal.nfe.v400.autorizacao.TEndereco;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNfeProc;
import br.inf.portalfiscal.nfe.v400.autorizacao.TRetEnviNFe;
import br.inf.portalfiscal.nfe.v400.autorizacao.TUf;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 *
 * @author Cássio Conceição
 * @since 2019
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public abstract class EmissorNFCe extends javax.swing.JFrame {

    private AutoCompleteField<Produto> search = null;
    private double quantidade = 1.00;

    private AutoCompleteField<Cliente> cliente;
    private TableModel modelTotal;
    private JTable tableProd;
    private TableModel modelProd;

    /**
     * Creates new form EmissorNFCe
     */
    public EmissorNFCe() {
        initComponents();
        configGUI();
    }

    private void configGUI() {

        Produto produto = new Produto();
        search = new AutoCompleteField(produto.getAutoCompleteModel());
        search.addSelectListener((SelectEvent ev) -> {
            incluirProduto((Produto) ev.getValue());
        });
        search.setFont(search.getFont().deriveFont(25f));
        search.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Q) {
                    setQuantidade();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
                    try {
                        selecionaCliente();
                    } catch (NFeException ex) {
                        JOptionPane.showMessageDialog(EmissorNFCe.this, ex, "Exception", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
                    finalizarNFCe();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_R) {
                    removerItemNFCe();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                    novaNFCe();
                }
            }
        });

        pSearch.setLayout(new BorderLayout());
        pSearch.add(search, BorderLayout.CENTER);

        Cliente c = new Cliente();
        cliente = new AutoCompleteField<>(c.getAutoCompleteModel());

        pCliente.setLayout(new BorderLayout());
        pCliente.add(cliente, BorderLayout.CENTER);

        modelTotal = new TableModel();
        modelTotal.addColumn("Decrição", String.class);
        modelTotal.addColumn("Valor", Double.class);
        modelTotal.addRow("Sub-Total", 0.00);
        modelTotal.addRow("Desconto", 0.00);
        modelTotal.addRow("Total", 0.00);

        JTable table = new JTable(modelTotal) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setPreferredScrollableViewportSize(new Dimension(180, 85));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.getColumn(table.getColumnName(0)).setMaxWidth(80);

        pTotal.setLayout(new BorderLayout());
        pTotal.add(new JScrollPane(table));

        modelProd = new TableModel();
        modelProd.addColumn("Item", Integer.class);
        modelProd.addColumn("Cód.", Integer.class);
        modelProd.addColumn("Descrição", Produto.class);
        modelProd.addColumn("NCM", Long.class);
        modelProd.addColumn("Vlr. Unit.", BigDecimal.class);
        modelProd.addColumn("Und.", String.class);
        modelProd.addColumn("Qtde.", Double.class);
        modelProd.addColumn("Total", Double.class);
        modelProd.addColumn("Desconto", Double.class);

        tableProd = new JTable(modelProd);

        pProd.setLayout(new BorderLayout());
        pProd.add(new JScrollPane(tableProd));

        pLogo.setLayout(new BorderLayout());
        //pLogo.add(new JLabel(Image.parse(Image.NFCE, 150)), BorderLayout.CENTER);

        setLocationRelativeTo(null);

        new Thread(() -> {
            while (isFocusableWindow()) {
                search.requestFocus();
            }
        }).start();
    }

    private void setQuantidade() {

        search.setValue(null);

        Number number = AbstractDialog.showInput(this, "Quantidade", 1.00, SwingUtils.decimalFormatter());

        if (number == null) {
            return;
        }

        quantidade = number.doubleValue();
    }

    private void incluirProduto(Produto p) {

        Number number = AbstractDialog.showInput(this, "Valor Unitário", p.getVUnCom() == null ? 0.00 : Double.parseDouble(p.getVUnCom()), SwingUtils.decimalFormatter());

        if (number == null) {
            return;
        }

        if (number.doubleValue() == 0) {
            JOptionPane.showMessageDialog(this, "Valor do produto não pode ser zero.", "Exception", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Number desconto = AbstractDialog.showInput(this, "Desconto", 0.00, SwingUtils.decimalFormatter());

        p.setValor(number.doubleValue(), quantidade, desconto);

        double vl = (double) modelTotal.getValueAt(0, 1);
        modelTotal.setValueAt(Double.parseDouble(p.getVProd()) + vl, 0, 1);

        if (desconto != null) {
            vl = (double) modelTotal.getValueAt(1, 1);
            modelTotal.setValueAt(desconto.doubleValue() + vl, 1, 1);
        } else {
            desconto = 0.00;
        }

        double sub = (double) modelTotal.getValueAt(0, 1);
        double desc = (double) modelTotal.getValueAt(1, 1);
        double total = sub - desc;

        modelTotal.setValueAt(total, 2, 1);

        Object[] row = {modelProd.getRowCount() + 1, Integer.parseInt(p.getCProd()), p, p.getNCM(), Double.parseDouble(p.getVUnCom()), p.getUCom(), quantidade, Double.parseDouble(p.getVProd()), desconto.doubleValue()};
        modelProd.addRow(row);

        SwingUtilities.invokeLater(() -> {
            quantidade = 1.00;
            search.setValue(null);
            search.requestFocus();
        });
    }

    private void finalizarNFCe() {

        if (modelProd.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Adicionar produto(s) a NFC-e.");
            return;
        }

        if (cliente.getValue() == null && JOptionPane.showConfirmDialog(this, "Deseja emitir NFC-e sem adicionar cliente?", "NFC-e", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Selecione cliente e volte a finalizar.");
            return;
        }

        if (cliente.getValue() != null && cliente.getValue().getCpf() == null) {
            JOptionPane.showMessageDialog(this, "É preciso cadastrar um CPF para o cliente.");
            return;
        }

        try {

            NFCeSimplesNacional nfce = new NFCeSimplesNacional();

            // Total
            double total = (double) modelTotal.getValueAt(2, 1);
            double totalPago = 0;
            modelTotal.setValueAt(total, 2, 1);

            // Cliente
            if (cliente.getValue() != null) {

                TEndereco endereco = null;

                if (total > 8000) {

                    EnderecoPanel panel = new EnderecoPanel();

                    if (JOptionPane.showConfirmDialog(this, panel, "Confirmar endereço", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

                        try {

                            endereco = new Endereco();
                            endereco.setCEP(String.valueOf(panel.getCep()).trim());
                            endereco.setCMun(String.valueOf(panel.getMunicipio().getCodIbge()).trim());
                            endereco.setNro(String.valueOf(panel.getNumero()).trim());
                            endereco.setUF(TUf.valueOf(panel.getMunicipio().getUfSigla().trim()));
                            endereco.setXBairro(panel.getBairro().trim());
                            endereco.setXCpl(panel.getComplemento().trim());
                            endereco.setXLgr(panel.getLogradouro().trim());
                            endereco.setXMun(panel.getMunicipio().getNome().trim());

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, ex, "Exception", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "É obrigatório preenchimento do endereço do cliente com valor acima de R$ 8.000,00", "Exception", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                nfce.addDest(cliente.getValue().getCpf(), cliente.getValue().getNome(), cliente.getValue().getEmail(), endereco);
            }

            // Produtos
            for (int i = 0; i < modelProd.getRowCount(); i++) {
                Produto p = (Produto) modelProd.getValueAt(i, 2);
                nfce.addProduto(p, ((Number) modelProd.getValueAt(i, 6)).doubleValue(), ((Number) modelProd.getValueAt(i, 8)).doubleValue());
            }

            // Pagamento
            do {

                Pagamento.Forma tPag = (Pagamento.Forma) AbstractDialog.showSelect(this, "Forma de pagamento", Pagamento.FORMAS);

                if (tPag == null) {
                    return;
                }

                Cartao cartao = null;

                if (tPag == Pagamento.Forma.CARTAO_CREDITO || tPag == Pagamento.Forma.CARTAO_DEBITO) {
                    cartao = (Cartao) AbstractDialog.showSelect(this, "Bandeira", Cartao.BANDEIRAS);
                    cartao.setCAut((String) AbstractDialog.showInput(this, "Número da Autorização", null, null));
                }

                Number vPag = (Number) AbstractDialog.showInput(this, "Valor", (total - totalPago), SwingUtils.decimalFormatter());

                if (vPag == null) {
                    return;
                }

                switch (tPag) {

                    case Constants.FORMA_PGTO_DINHEIRO:
                        nfce.addPagamentoDinheiro(vPag.doubleValue());
                        break;
                    case Constants.FORMA_PGTO_CHEQUE:
                        nfce.addPagamentoCheque(vPag.doubleValue());
                        break;
                    case Constants.FORMA_PGTO_CARTAO_CRED:
                        nfce.addPagamentoCartaoCredito(cartao, vPag.doubleValue());
                        break;
                    case Constants.FORMA_PGTO_CARTAO_DEB:
                        nfce.addPagamentoCartaoDebito(cartao, vPag.doubleValue());
                        break;
                    case Constants.FORMA_PGTO_CREDIARIO:
                        nfce.addPagamentoCrediario(vPag.doubleValue());
                        break;
                    default:
                        nfce.addPagamentoOutros(vPag.doubleValue());
                        break;
                }

                totalPago += vPag.doubleValue();

            } while (totalPago < total);

            // Calcula troco
            double troco = 0.00;
            if (totalPago > total) {
                troco = totalPago - total;
            }

            // Finalizar NFC-e
            nfce.finalizar(troco);

            if (JOptionPane.showConfirmDialog(this, new ConfirmarNFCePanel(nfce), "NFC-e", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                new WaitDialog<TRetEnviNFe>(this) {

                    @Override
                    public TRetEnviNFe process() throws Exception {
                        return SOAP.enviarNFCe(nfce);
                    }

                    @Override
                    public void endProcess(TRetEnviNFe result) throws Exception {

                        novaNFCe();

                        if (result != null) {

                            
                        }

                    }

                }.start();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex, "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novaNFCe() {
        modelProd.removeAllRows();
        modelTotal.setValueAt(0.00, 0, 1);
        modelTotal.setValueAt(0.00, 1, 1);
        modelTotal.setValueAt(0.00, 2, 1);
        cliente.setValue(null);
        search.requestFocus();
    }

    private void removerItemNFCe() {

        Number number = AbstractDialog.showInput(this, "Número do item a remover", null, SwingUtils.integerFormatter());

        if (number == null) {
            JOptionPane.showMessageDialog(this, "Item inválido.");
            return;
        }

        int row = number.intValue() - 1;

        if (JOptionPane.showConfirmDialog(this, "Deseja realmente excluír item [" + modelProd.getValueAt(row, 2) + "]?", "NFC-e", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {

            try {

                double desconto = ((Number) modelProd.getValueAt(row, 8)).doubleValue();
                double total = (double) modelProd.getValueAt(row, 7);

                modelTotal.setValueAt((double) modelTotal.getValueAt(0, 1) - total, 0, 1);
                modelTotal.setValueAt((double) modelTotal.getValueAt(1, 1) - desconto, 1, 1);
                modelTotal.setValueAt((double) modelTotal.getValueAt(2, 1) - (total - desconto), 2, 1);

                int index = tableProd.getRowSorter().convertRowIndexToModel(tableProd.getSelectedRow());
                modelProd.removeRow(index);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex, "Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected abstract List<Produto> buscaProduto(String filter) throws NFeException;

    protected abstract Cliente selecionaCliente() throws NFeException;

    protected abstract void saveNFCe(TNfeProc nfce) throws NFeException;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pSearch = new javax.swing.JPanel();
        pLogo = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pCliente = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        pTotal = new javax.swing.JPanel();
        pProd = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Produto ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 24))); // NOI18N
        jPanel1.setOpaque(false);

        javax.swing.GroupLayout pSearchLayout = new javax.swing.GroupLayout(pSearch);
        pSearch.setLayout(pSearchLayout);
        pSearchLayout.setHorizontalGroup(
            pSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pSearchLayout.setVerticalGroup(
            pSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 38, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pLogo.setOpaque(false);

        javax.swing.GroupLayout pLogoLayout = new javax.swing.GroupLayout(pLogo);
        pLogo.setLayout(pLogoLayout);
        pLogoLayout.setHorizontalGroup(
            pLogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );
        pLogoLayout.setVerticalGroup(
            pLogoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " Cliente ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 24))); // NOI18N
        jPanel2.setOpaque(false);

        javax.swing.GroupLayout pClienteLayout = new javax.swing.GroupLayout(pCliente);
        pCliente.setLayout(pClienteLayout);
        pClienteLayout.setHorizontalGroup(
            pClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 302, Short.MAX_VALUE)
        );
        pClienteLayout.setVerticalGroup(
            pClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 38, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(" Comandos "));

        jLabel1.setText("Ctrl + Q = Quantidade");

        jLabel2.setText("Ctrl + C = Cliente");

        jLabel3.setText("Ctrl + F = Finalizar");

        jLabel4.setText("Ctrl + R = Remover Item");

        jLabel5.setText("Ctrl + N = Nova NFC-e");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(" Total NFC-e "));
        pTotal.setOpaque(false);

        javax.swing.GroupLayout pTotalLayout = new javax.swing.GroupLayout(pTotal);
        pTotal.setLayout(pTotalLayout);
        pTotalLayout.setHorizontalGroup(
            pTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
        );
        pTotalLayout.setVerticalGroup(
            pTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 118, Short.MAX_VALUE)
        );

        pProd.setBorder(javax.swing.BorderFactory.createTitledBorder(" Itens NFC-e "));
        pProd.setOpaque(false);

        javax.swing.GroupLayout pProdLayout = new javax.swing.GroupLayout(pProd);
        pProd.setLayout(pProdLayout);
        pProdLayout.setHorizontalGroup(
            pProdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pProdLayout.setVerticalGroup(
            pProdLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 108, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pProd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 31, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pLogo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pProd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(98, 98, 98))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel pCliente;
    private javax.swing.JPanel pLogo;
    private javax.swing.JPanel pProd;
    private javax.swing.JPanel pSearch;
    private javax.swing.JPanel pTotal;
    // End of variables declaration//GEN-END:variables
}
