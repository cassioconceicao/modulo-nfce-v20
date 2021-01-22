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

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class Cartao extends TNFe.InfNFe.Pag.DetPag.Card {

    public static Bandeira[] BANDEIRAS = {Bandeira.VISA, Bandeira.MASTERCARD, Bandeira.BANRICOMPRAS, Bandeira.ELO, Bandeira.HIPERCARD, Bandeira.AMERICA_EXPRESS, Bandeira.AURA, Bandeira.CABAL, Bandeira.DINERS, Bandeira.SOROCRED, Bandeira.VERDECARD};

    public Cartao() {
        super.setTpIntegra("2");
    }

    public enum Bandeira {

        AMERICA_EXPRESS("03", "60419645000195", "AMERICA EXPRESS"),
        AURA("08", "03722919000187", "AURA"),
        BANRICOMPRAS("99", "92934215000106", "BANRICOMPRAS"),
        CABAL("09", "03766873000106", "CABEL"),
        DINERS("05", "01425787000104", "DINERS"),
        ELO("06", "01425787000104", "ELO"),
        HIPERCARD("07", "01425787000104", "HIPERCARD"),
        MASTERCARD("02", "01425787000104", "MASTERCARD"),
        SOROCRED("04", "60114865000100", "SOROCRED"),
        VERDECARD("99", "01722480000167", "VERDECARD"),
        VISA("03", "01425787000104", "VISA");

        private final String tBand;
        private final String cnpj;
        private final String descricao;

        Bandeira(String tBand, String cnpj, String descricao) {
            this.tBand = tBand;
            this.cnpj = cnpj;
            this.descricao = descricao;
        }

        public String getTBand() {
            return tBand;
        }

        public String getCNPJ() {
            return cnpj;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

    }

    public void set(Bandeira bandeira, String autorizacao) {
        setCAut(autorizacao.trim());
        setCNPJ(bandeira.getCNPJ());
        setTBand(bandeira.getTBand());
    }
}
