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

import br.inf.portalfiscal.nfe.v400.autorizacao.TEndereco;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;
import br.inf.portalfiscal.nfe.v400.autorizacao.TUf;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class Cliente extends TNFe.InfNFe.Dest {

    public Cliente() {
        super.setIndIEDest("9");
    }

    public void setFone(String fone) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        getEnderDest().setFone(fone.trim());
    }

    public void setCEP(Number cod) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (cod != null) {
            getEnderDest().setCEP(String.valueOf(cod).trim());
        }
    }

    public void setCMun(Number cod) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (cod != null) {
            getEnderDest().setCMun(String.valueOf(cod).trim());
        }
    }

    public void setNro(Number num) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (num != null) {
            getEnderDest().setNro(String.valueOf(num).trim());
        }
    }

    public void setXBairro(String str) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (str != null) {
            getEnderDest().setXBairro(str.trim());
        }
    }

    public void setXCpl(String str) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (str != null) {
            getEnderDest().setXCpl(str.trim());
        }
    }

    public void setXLgr(String str) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (str != null) {
            getEnderDest().setXLgr(str.trim());
        }
    }

    public void setXMun(String str) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (str != null) {
            getEnderDest().setXMun(str.trim());
        }
    }

    public void setUF(String uf) {

        if (getEnderDest() == null) {
            setEnderDest(new TEndereco());
        }

        if (uf != null) {
            getEnderDest().setUF(TUf.fromValue(uf.trim()));
        }
    }

    public String getFone() {
        return getEnderDest().getFone();
    }

    public String getCEP() {
        return getEnderDest().getCEP();
    }

    public String getCMun() {
        return getEnderDest().getCMun();
    }

    public String getNro() {
        return getEnderDest().getNro();
    }

    public String getXBairro() {
        return getEnderDest().getXBairro();
    }

    public String getXCpl() {
        return getEnderDest().getXCpl();
    }

    public String getXLgr() {
        return getEnderDest().getXLgr();
    }

    public String getXMun() {
        return getEnderDest().getXMun();
    }

    public TUf getUF() {
        return getEnderDest().getUF();
    }

    public void setEndereco(Endereco endereco) {
        super.setEnderDest(endereco);
    }

    @Override
    public String toString() {
        return getXNome();
    }

}
