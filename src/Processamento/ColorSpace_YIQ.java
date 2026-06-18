package Processamento;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * @author Victor Henrique Rodrigues
 */
public class ColorSpace_YIQ {
    
    /**
     * Converte a imagem RGB para uma matriz tridimensional double pura (Sem normalização).
     */
    public static double[][][] converterRGBparaYIQMatriz(BufferedImage Imagem) {
        int largura = Imagem.getWidth();
        int altura = Imagem.getHeight();
        double[][][] matriz = new double[largura][altura][3];

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                Color C = new Color(Imagem.getRGB(i, j));
                double R = C.getRed();
                double G = C.getGreen();
                double B = C.getBlue();

                // Fórmulas diretas oficiais (Matriz do Artigo)
                matriz[i][j][0] = 0.299 * R + 0.587 * G + 0.114 * B; // Y (Brilho)
                matriz[i][j][1] = 0.596 * R - 0.274 * G - 0.322 * B; // I
                matriz[i][j][2] = 0.211 * R - 0.523 * G + 0.312 * B; // Q
            }
        }
        return matriz;
    }

    /**
     * Converte uma matriz double tridimensional YIQ de volta para imagem colorida RGB.
     */
    public static BufferedImage converterYIQMatrizParaRGB(double[][][] matrizYIQ) {
        int largura = matrizYIQ.length;
        int altura = matrizYIQ[0].length;
        BufferedImage saidaColorida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                double Y = matrizYIQ[i][j][0];
                double I = matrizYIQ[i][j][1];
                double Q = matrizYIQ[i][j][2];

                // Fórmulas inversas oficiais (Matriz do Artigo)
                double R_rec = 1.000 * Y + 0.956 * I + 0.621 * Q;
                double G_rec = 1.000 * Y - 0.272 * I - 0.647 * Q;
                double B_rec = 1.000 * Y - 1.106 * I + 1.703 * Q;

                int rFinal = (int) Math.max(0, Math.min(255, R_rec));
                int gFinal = (int) Math.max(0, Math.min(255, G_rec));
                int bFinal = (int) Math.max(0, Math.min(255, B_rec));

                int pixelColorido = (0xFF << 24) | (rFinal << 16) | (gFinal << 8) | bFinal;
                saidaColorida.setRGB(i, j, pixelColorido);
            }
        }
        return saidaColorida;
    }
    
    /**
     * Renderiza a matriz tridimensional YIQ completa em uma imagem colorida estável 
     * aplicando o offset técnico de +128 nas componentes cromáticas diferenciais.
     */
    public static BufferedImage converterYIQMatrizParaImagemColorida(double[][][] matrizYIQ) {
        int largura = matrizYIQ.length;
        int altura = matrizYIQ[0].length;
        BufferedImage saida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                int Y = (int) Math.max(0, Math.min(255, matrizYIQ[i][j][0]));
                int I = (int) Math.max(0, Math.min(255, matrizYIQ[i][j][1] + 128));
                int Q = (int) Math.max(0, Math.min(255, matrizYIQ[i][j][2] + 128));

                int pixelYIQ = (0xFF << 24) | (Y << 16) | (I << 8) | Q;
                saida.setRGB(i, j, pixelYIQ);
            }
        }
        return saida;
    }
    
    /**
     * Gera os canais Y, I e Q individuais para exibição em escala de cinza.
     */
    public static BufferedImage converterRGBparaYIQVisualizavel(BufferedImage Imagem, int canal) {
        int largura = Imagem.getWidth();
        int altura = Imagem.getHeight();
        BufferedImage saida = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);

        double[][][] matrizYIQ = converterRGBparaYIQMatriz(Imagem);

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                double valorPuro = matrizYIQ[i][j][canal - 1];
                int valorFinal = 0;

                switch (canal) {
                    case 1:
                        valorFinal = (int) Math.max(0, Math.min(255, valorPuro));
                        break;
                    case 2:
                    case 3:
                        // Offset técnico aplicado para centralizar e eixos negativos na escala de cinza
                        valorFinal = (int) Math.max(0, Math.min(255, valorPuro + 128));
                        break; 
                }

                int pixelCinza = (0xFF << 24) | (valorFinal << 16) | (valorFinal << 8) | valorFinal;
                saida.setRGB(i, j, pixelCinza);
            }
        }
        return saida;
    }
}