package Processamento;

import Telas.Tela_Espaco_cor;
import java.awt.image.BufferedImage;

/**
 * @author João Victor do Rozário Recla - 2022/2
 * @modifier Victor Henrique Rodrigues - 2026
 */
public class Processamento_Imagem_ {
        public static BufferedImage CriaCopia(BufferedImage Imagem){
            BufferedImage Copia = new BufferedImage(Imagem.getWidth(),Imagem.getHeight(),Imagem.getType());
            for(int x=0;x<Imagem.getWidth();x++){
                for(int y=0;y<Imagem.getHeight();y++){
                    Copia.setRGB(x,y,Imagem.getRGB(x,y));
                }
            }
            return Copia;
        }
        
        public static BufferedImage YCbCr(BufferedImage Imagem,Tela_Espaco_cor Tela){
            return ColorSpace_YCbCr.Detection(Imagem, Tela);
	}
        
        public static BufferedImage XYZ(BufferedImage Imagem,Tela_Espaco_cor Tela){
            return ColorSpace_XYZ.Detection(Imagem, Tela);
	}
        
	public static BufferedImage Segment_(BufferedImage Imagem, int Metodo) {
            switch(Metodo){
                case 1:  return Segmentacao_.Otsu_Binarization_(Imagem);
                case 2:  return Segmentacao_.Fuzzy_Huang_Binarization_(Imagem);
                default: return Imagem;
            }
	}
        
        public static BufferedImage CriaCopiaCinza(BufferedImage ImagemOriginal) {
            int largura = ImagemOriginal.getWidth();
            int altura = ImagemOriginal.getHeight();
            int tipo = ImagemOriginal.getType();
            BufferedImage Copia = new BufferedImage(largura, altura, tipo);

            for (int x = 0; x < largura; x++) {
                for (int y = 0; y < altura; y++) {
                    int pixelRGB = ImagemOriginal.getRGB(x, y);
                    Copia.setRGB(x, y, pixelRGB);
                }
            }
            return Copia;
        }
        
        public static BufferedImage AdaptiveMedianCinza(BufferedImage ImageOriginal, int wMax){
            return AdaptiveMedianFilter.adaptiveMedianCinza(ImageOriginal, wMax);
        }
        
        public static BufferedImage AdaptiveMedianCinzaGlobal(BufferedImage ImageOriginal, int wMax){
            return AdaptiveMedianFilterGlobal.adaptiveMedianCinza(ImageOriginal, wMax);
        }
        
        public static BufferedImage ConverterParaYIQ(BufferedImage Imagem, int canal) {
            return ColorSpace_YIQ.converterRGBparaYIQVisualizavel(CriaCopia(Imagem), canal);
        }

        public static double[][][] ExtrairMatrizYIQ(BufferedImage Imagem) {
            return ColorSpace_YIQ.converterRGBparaYIQMatriz(CriaCopia(Imagem));
        }

        public static BufferedImage ConverterMatrizYIQParaRGB(double[][][] matrizYIQ) {
            return ColorSpace_YIQ.converterYIQMatrizParaRGB(matrizYIQ);
        }
        
        /**
         * Gera a imagem visualizável da codificação tridimensional estável do YIQ.
         */
        public static BufferedImage GerarImagemYIQCompleta(double[][][] matrizYIQ) {
            return ColorSpace_YIQ.converterYIQMatrizParaImagemColorida(matrizYIQ);
        }
        
        public static BufferedImage FiltrarEConvertYIQ(BufferedImage Imagem, int wMax) {
            return AdaptiveMedianFilter.AdaptiveMedianCor(CriaCopia(Imagem), wMax);
        }
        
        public static BufferedImage FiltrarEConvertYIQGlobal(BufferedImage Imagem, int wMax) {
            return AdaptiveMedianFilterGlobal.AdaptiveMedianCorGlobal(CriaCopia(Imagem), wMax);
        }
        
}