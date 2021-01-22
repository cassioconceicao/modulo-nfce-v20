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

import br.com.ctecinf.nfe.NFe;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class COFINSSimples102 extends TNFe.InfNFe.Det.Imposto.COFINS {

    public COFINSSimples102() {

        cofinsOutr = new COFINSOutr();
        cofinsOutr.setCST("99");

        setQBCProd(0.00);
        setVAliqProd(0.00);
        setVCOFINS(0.00);
    }

    public final void setQBCProd(Number value) {
        cofinsOutr.setQBCProd(NFe.format4Digits(value));
    }

    public final void setVAliqProd(Number value) {
        cofinsOutr.setVAliqProd(NFe.format4Digits(value));
    }

    public final void setVCOFINS(Number value) {
        cofinsOutr.setVCOFINS(NFe.format2Digits(value));
    }

}
