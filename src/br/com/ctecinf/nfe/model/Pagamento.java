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
package br.com.ctecinf.nfe.model;

import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class Pagamento extends TNFe.InfNFe.Pag.DetPag {

    public static Forma[] FORMAS = {Forma.DINHEIRO, Forma.CHEQUE, Forma.CARTAO_CREDITO, Forma.CARTAO_DEBITO, Forma.CREDIARIO, Forma.OUTROS};

    public enum Forma {

        DINHEIRO("01", "DINHEIRO"),
        CHEQUE("02", "CHEQUE"),
        CARTAO_CREDITO("03", "CARTAO DE CREDITO"),
        CARTAO_DEBITO("04", "CARTAO DE DEBITO"),
        CREDIARIO("05", "CREDIARIO"),
        OUTROS("99", "OUTROS");

        private final String tPag;
        private final String descricao;

        private Forma(String tPag, String descricao) {
            this.tPag = tPag;
            this.descricao = descricao;
        }

        public String getTPag() {
            return tPag;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public void set(Forma formaPagamento, Double valorPago, Cartao cartao) {

        if (cartao != null) {
            setCard(cartao);
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);

        setTPag(formaPagamento.getTPag());
        setVPag(nf.format(valorPago).trim());
    }
}
