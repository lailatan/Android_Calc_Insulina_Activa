package com.lailatan.calc_insulina_activa;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import java.text.DecimalFormatSymbols;

public abstract class UtilMaskEdit {

    public static final String FORMAT_CPF = "###.###.###-##";
    public static final String FORMAT_FONE = "(###)####-#####";
    public static final String FORMAT_CEP = "#####-###";
    public static final String FORMAT_DATE = "##/##/####";
    public static final String FORMAT_HOUR = "##:##";
    public static final String FORMAT_DECIMAL = "##*##";

    /**
     * Método que deve ser chamado para realizar a formatação
     *
     * @param ediTxt
     * @param mask
     * @return
     */
    public static TextWatcher mask(final EditText ediTxt, final String mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void afterTextChanged(final Editable s) {}

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {}

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final String str = UtilMaskEdit.unmask(s.toString());
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                String miMask = mask;
                if (miMask.equals(FORMAT_DECIMAL))
                    miMask = "##" + decimalFormatSymbols.getDecimalSeparator() + "##";

                for (final char m : miMask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (final Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }
        };
    }

    public static String unmask(final String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "").replaceAll("[/]", "").replaceAll("[(]", "").replaceAll("[ ]","").replaceAll("[:]", "").replaceAll("[)]", "");
    }
}