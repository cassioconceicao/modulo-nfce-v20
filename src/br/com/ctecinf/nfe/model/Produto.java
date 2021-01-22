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
public class Produto extends TNFe.InfNFe.Det.Prod {

    public Produto() {
        cean = "";
        ceanTrib = "";
        indTot = "1";
    }

    public void setCodigo(Long cod) {
        setCProd(String.valueOf(cod).trim());
    }

    public void setDescricao(String desc) {
        setXProd(desc.trim());
    }

    public void setCFOP(Integer cfop) {
        setCFOP(String.valueOf(cfop).trim());
    }

    public void setNCM(Integer cfop) {
        setNCM(String.valueOf(cfop).trim());
    }

    public void setValor(Double valorUnitario, Double qtde, Number desconto) {

        setQCom(NFe.format4Digits(qtde).trim());
        setQTrib(NFe.format4Digits(qtde).trim());

        setVUnCom(NFe.format4Digits(valorUnitario).trim());
        setVUnTrib(NFe.format4Digits(valorUnitario).trim());

        setVProd(NFe.format2Digits(valorUnitario * qtde).trim());

        if (desconto != null && desconto.doubleValue() > 0) {
            setVDesc(NFe.format2Digits(desconto).trim());
        }
    }

    public void setUnidadeComercial(UnidadeComercial unidadeComercial) {
        setUCom(unidadeComercial.getValue());
        setUTrib(unidadeComercial.getValue());
    }

}
