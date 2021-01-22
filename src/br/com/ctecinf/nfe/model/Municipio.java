/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.nfe.model;

/**
 *
 * @author Cássio Conceição
 * @since 21/08/2019 17:06:02
 * @version 201908
 * @see http://ctecinf.com.br/
 */
public class Municipio {

    private Integer codIbge;
    private String nome;
    private Integer ufCodIbge;
    private String ufNome;
    private String ufSigla;

    public Integer getCodIbge() {
        return this.codIbge;
    }

    public void setCodIbge(Integer codIbge) {
        this.codIbge = codIbge;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getUfCodIbge() {
        return this.ufCodIbge;
    }

    public void setUfCodIbge(Integer ufCodIbge) {
        this.ufCodIbge = ufCodIbge;
    }

    public String getUfNome() {
        return this.ufNome;
    }

    public void setUfNome(String ufNome) {
        this.ufNome = ufNome;
    }

    public String getUfSigla() {
        return this.ufSigla;
    }

    public void setUfSigla(String ufSigla) {
        this.ufSigla = ufSigla;
    }

    @Override
    public String toString() {
        return (nome + " - " + ufSigla).trim();
    }
}
