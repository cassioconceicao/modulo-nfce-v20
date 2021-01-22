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
import br.inf.portalfiscal.nfe.v400.autorizacao.ObjectFactory;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class NFCeSimplesNacional extends TNFe {

    public NFCeSimplesNacional() {
        infNFe = new InfNFe();
    }

    public void addCliente(Cliente cliente) {
        infNFe.setDest(cliente);
    }

    public void addProduto(Produto produto, InfNFe.Det.Imposto.ICMS icms, InfNFe.Det.Imposto.PIS pis, InfNFe.Det.Imposto.COFINS cofins) {

        TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
        imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoICMS(icms));
        imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoPIS(pis));
        imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoCOFINS(cofins));

        TNFe.InfNFe.Det det = new TNFe.InfNFe.Det();
        det.setNItem(String.valueOf(infNFe.getDet().size() + 1));
        det.setProd(produto);
        det.setImposto(imposto);

        infNFe.getDet().add(det);

        double vProd = Double.parseDouble(produto.getVProd().trim());
        double vTot = Double.parseDouble(infNFe.getTotal().getICMSTot().getVProd().trim());

        infNFe.getTotal().getICMSTot().setVProd(NFe.format2Digits(vProd + vTot));
        infNFe.getTotal().getICMSTot().setVNF(NFe.format2Digits(vProd + vTot));
    }
}
