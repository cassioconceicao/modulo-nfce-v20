/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.nfe.model;

import br.inf.portalfiscal.nfe.v400.autorizacao.TEndereco;
import br.inf.portalfiscal.nfe.v400.autorizacao.TUf;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class Endereco extends TEndereco {

    public void setCEP(Number cod) {
        if (cod != null) {
            setCEP(String.valueOf(cod));
        }
    }

    public void setCMun(Number cod) {
        if (cod != null) {
            setCMun(String.valueOf(cod));
        }
    }

    public void setNro(Number num) {
        if (num != null) {
            setNro(String.valueOf(num));
        }
    }

    @Override
    public void setXBairro(String str) {
        if (str != null) {
            super.setXBairro(str.trim());
        }
    }

    @Override
    public void setXCpl(String str) {
        if (str != null) {
            super.setXCpl(str.trim());
        }
    }

    @Override
    public void setXLgr(String str) {
        if (str != null) {
            super.setXLgr(str.trim());
        }
    }

    @Override
    public void setXMun(String str) {
        if (str != null) {
            super.setXMun(str.trim());
        }
    }

    /**
     *
     * @param uf RS
     */
    public void setUF(String uf) {
        if (uf != null) {
            setUF(TUf.fromValue(uf));
        }
    }
}
