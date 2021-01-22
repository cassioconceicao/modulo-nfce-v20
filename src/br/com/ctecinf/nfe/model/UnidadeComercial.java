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

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public enum UnidadeComercial {

    AMPOLA("AMPOLA"),
    BALDE("BALDE"),
    BANDEJA("BANDEJ"),
    BARRA("BARRA"),
    BISNAGA("BISNAG"),
    BLOCO("BLOCO"),
    BOBINA("BOBINA"),
    BOMBONA("BOMBEAR"),
    CAPSULA("CAPSULAS"),
    CARTELA("CARRINHO"),
    CENTO("CENTO"),
    CONJUNTO("CJ"),
    CENTIMETRO("CM"),
    CENTIMETRO_QUADRADO("CM2"),
    CAIXA("CX"),
    CAIXA_COM_2_UNIDADES("CX2"),
    CAIXA_COM_3_UNIDADES("CX3"),
    CAIXA_COM_5_UNIDADES("CX5"),
    CAIXA_COM_10_UNIDADES("CX10"),
    CAIXA_COM_15_UNIDADES("CX15"),
    CAIXA_COM_20_UNIDADES("CX20"),
    CAIXA_COM_25_UNIDADES("CX25"),
    CAIXA_COM_50_UNIDADES("CX50"),
    CAIXA_COM_100_UNIDADES("CX100"),
    EXIBICAO("DISP"),
    DUZIA("DUZIA"),
    EMBALAGEM("EMBAL"),
    FARDO("FARDO"),
    FOLHA("FOLHA"),
    FRASCO("FRASCO"),
    GALAO("GALAO"),
    GARRAFA("GF"),
    GRAMAS("GRAMAS"),
    JOGO("JOGO"),
    QUILOGRAMA("KG"),
    KIT("KIT"),
    LATA("LATA"),
    LITRO("LITRO"),
    METRO("M"),
    METRO_QUADRADO("M2"),
    METRO_CUBICO("M3"),
    MILHEIRO("MILHEI"),
    MILILITRO("ML"),
    MEGAWATT_HORA("MWH"),
    PACOTE("PACOTE"),
    PALETE("PALETE"),
    PAR("PAR"),
    PECA("PC"),
    AMIGO("AMIGO"),
    QUILATE("K"),
    RESMA("RESMA"),
    ROLO("ROLO"),
    SACO("SACO"),
    SACOLA("SACOLA"),
    TAMBOR("TAMBOR"),
    TANQUE("TANQUE"),
    TONELADA("TON"),
    TUBO("TUBO"),
    UNIDADE("UNID"),
    VASILHAME("VASIL"),
    VIDRO("VIDRO");

    private final String value;

    UnidadeComercial(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
