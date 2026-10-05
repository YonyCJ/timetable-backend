package utils;

public class NumberToTextUtil {
    private static final String[] UNIDADES = {"", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "};
    private static final String[] DECENAS = {"DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ", "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA "};
    private static final String[] CENTENAS = {"", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "};

    public static String convertir(double monto) {
        long entero = (long) monto;
        long centavos = Math.round((monto - entero) * 100);

        String texto = convertirNumero(entero) + "CON " + String.format("%02d", centavos) + "/100 SOLES";
        return texto.toUpperCase();
    }

    private static String convertirNumero(long n) {
        if (n == 0) return "CERO ";
        if (n == 100) return "CIEN ";
        if (n < 10) return UNIDADES[(int) n];
        if (n < 20) return DECENAS[(int) (n - 10)];
        if (n < 30) return "VEINTI" + UNIDADES[(int) (n - 20)];
        if (n < 100) return DECENAS[(int) (n / 10 + 8)] + (n % 10 != 0 ? "Y " + UNIDADES[(int) (n % 10)] : "");
        if (n < 1000) return CENTENAS[(int) (n / 100)] + convertirNumero(n % 100);
        if (n < 1000000) {
            String mil = (n / 1000 == 1) ? "MIL " : convertirNumero(n / 1000) + "MIL ";
            return mil + convertirNumero(n % 1000);
        }
        return "ERROR_RANGO";
    }
}