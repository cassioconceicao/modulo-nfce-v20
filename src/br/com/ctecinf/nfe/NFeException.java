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
package br.com.ctecinf.nfe;

import javax.swing.JOptionPane;

/**
 *
 * @author Cássio Conceição
 * @since 2020
 * @version 2020
 * @see http://ctecinf.com.br/
 */
public class NFeException extends Exception {

    public NFeException(String message) {
        super(message);
    }

    public NFeException(Exception exception) {
        super(exception.fillInStackTrace());
    }

    /**
     * Exibe exceção
     */
    public void show() {
        JOptionPane.showMessageDialog(null, this, "Exception", JOptionPane.ERROR_MESSAGE);
        System.err.println(this);
    }
}
