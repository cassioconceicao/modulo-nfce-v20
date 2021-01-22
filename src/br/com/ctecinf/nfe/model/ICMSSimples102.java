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
public class ICMSSimples102 extends TNFe.InfNFe.Det.Imposto.ICMS {

    /**
     * Default ICMS Origem '0'
     */
    public ICMSSimples102() {
        this(0);
    }

    public ICMSSimples102(Integer icmsOrig) {
        
        icmssn102 = new ICMSSN102();
        icmssn102.setCSOSN("102");
        
        setOrig(icmsOrig);
    }

    public final void setOrig(Integer icmsOrig) {
        icmssn102.setOrig(icmsOrig == null ? "0" : String.valueOf(icmsOrig));
    }

}
