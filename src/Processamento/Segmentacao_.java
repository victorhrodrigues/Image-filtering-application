package Processamento;

import java.awt.Color;
import java.awt.image.BufferedImage;
import static java.lang.Math.abs;

/**
 * @author João Victor do Rozario Recla - 2022/2
 */
public class Segmentacao_ {
    
    
    /*  Metodo para calcular o histograma
        de uma imagem em niveis de cinza.   */
    private static int[] Calcular_Niveis_Cinza_(BufferedImage Imagem){
                
        int Nv_Cinza;
        int Linha, Coluna;
        int Altura  = Imagem.getHeight();   // Altura da imagem.
        int Largura = Imagem.getWidth();    // Largura da imagem.
        int Histograma[] = new int[256];    // Histograma da imagem.
        
        // Calculo do histograma em niveis de cinza.
        for(Linha = 0; Linha < Altura; Linha++){
            for(Coluna = 0; Coluna < Largura; Coluna++){
                
                Color Cx = new Color(Imagem.getRGB(Coluna, Linha));
                Nv_Cinza = (int)((Cx.getRed() +Cx.getGreen() +Cx.getBlue()) / 3);
                Histograma[Nv_Cinza]++;
            }
        }
        
        return Histograma;
    }
    
    
    /*  Metodo para encontrar o primeiro maior e o primeiro
        menor nivel de cinza no histograma de uma imagem.   */
    private static int[] Encontrar_Maior_Menor_Nivel_Cinza_(int Histograma[]){
        
        int i = 0, j = 255;
        int Niveis_[] = new int[2];
        
        // Loop para encontrar os niveis.
        while(true){
            
            // Casos de parada.
            if((i > 255) && (j < 0)) break;
            if((Niveis_[0] != 0) && (Niveis_[1] != 0)) break;

            
            if((i < 256) && (Histograma[i] != 0)){
                
                Niveis_[0] = i; // 1º Menor nivel encontrado.
                i = 256;        // break.
            }
            
            if((j >= 0) && (Histograma[j] != 0)){
                
                Niveis_[1] = j; // 1º Maior nivel encontrado.
                j = -1;         // break
            }
            
            i++;
            j--;
        }
        
        return Niveis_;
    }
    
    
    /*  Metodo para binarizar uma imagem, em
        niveis de cinza, com base em um limiar. */
    private static BufferedImage Aplicar_Binarizacao_(BufferedImage Img, int Limiar){
        
        int Nv_Cinza;
        int Linha, Coluna;
        int Altura  = Img.getHeight();  // Altura da imagem.
        int Largura = Img.getWidth();   // Largura da imagem.
        
        // Buffer para a imagem binarizada.
        BufferedImage Img_binarizada = new BufferedImage(Largura, Altura, Img.getType());
        
        
        // Loop para binarizar a imagem.
        for(Linha = 0; Linha < Altura; Linha++){
            for(Coluna = 0; Coluna < Largura; Coluna++){
                
                // Cor da imagem em niveis de cinza.
                Color Cx = new Color(Img.getRGB(Coluna, Linha));
                Nv_Cinza = (int)((Cx.getGreen() + Cx.getRed() + Cx.getBlue()) / 3);
                
                // Cor binaria para a imagem.
                Color Cor_bin;
                if(Nv_Cinza >= Limiar)  Cor_bin = new Color(255, 255, 255); // Branco.
                else                    Cor_bin = new Color(0);             // Preto.
                
                // Montagem da imagem binarizada.
                Img_binarizada.setRGB(Coluna, Linha, Cor_bin.getRGB());
            }
        }
        
        return Img_binarizada;
    }
    
    
    
    
    
    /*  Algoritmo para segmentacao
        (Binarizacao Otsu Tradicional). */
    public static BufferedImage Otsu_Binarization_(BufferedImage Imagem) {
        
        int Limiar, K, i;
        int Altura  = Imagem.getHeight();                       // Altura da imagem.
        int Largura = Imagem.getWidth();                        // Largura da imagem.
        int Histograma[]  = Calcular_Niveis_Cinza_(Imagem);     // Histograma da imagem em niveis de cinza.
        
        double Qnt_pixels = (double) (Largura * Altura);        // Total de pixels.
        double Proba[]    = new double[256];                    // Vetor de probabilidades.
        
        
        // Aloca as matrizes.
        double fSigmaBMax, fMiTotal;
        double [] fOmega = new double[256], fMi = new double[256], fSigmaB = new double[256];

        // Passo 2: Calculo das probabilidades.
        for(i = 0; i < 256; i++){
             
            Proba[i]  = (double) ((Histograma[i]) / (double)(Qnt_pixels));
            fOmega[i] = fMi[i] = 0.0;
        }

        for(K = 0; K < 256; K++){
            for(i = 0; i < K; i++){
                
                fOmega[K] += Proba[i];
                fMi[K]    += (Proba[i] * (i + 1));
            }
        }
        
        fMiTotal = fSigmaBMax = 0.0;
        Limiar = 128;     // Inicializacao do valor de limiar de Otsu.

        
        for(i = 0; i < 256; i++){
            fMiTotal += (i + 1) * Proba[i];
        }

        
        if((fOmega[0] * (1 - fOmega[0])) != 0.0){
                
            fSigmaBMax = (  (fMiTotal * fOmega[0] - fMi[0]) * (fMiTotal * fOmega[0] - fMi[0]) ) / (fOmega[0] * (1 - fOmega[0]));
            Limiar = 0;
        }

        
        for(K = 1; K < 256; K++){
            
            if((fOmega[K] * (1 - fOmega[K])) != 0.0){
                   
                fSigmaB[K] = (  (fMiTotal * fOmega[K] - fMi[K]) * (fMiTotal * fOmega[K] - fMi[K]) ) / (fOmega[K] * (1 - fOmega[K]));

                if(fSigmaB[K] > fSigmaBMax){
                
                    fSigmaBMax = fSigmaB[K];
                    Limiar = (int) K;
                }
            }
        }

        return Aplicar_Binarizacao_(Imagem, Limiar);
    }
    
    
    
    /*  Algoritmo para segmentacao
        (Binarizacao Fuzzy Huang).  */
    public static BufferedImage Fuzzy_Huang_Binarization_(BufferedImage Imagem){
        
        int i, j;
        int Altura  = Imagem.getHeight();                   // Altura da imagem.
	int Largura = Imagem.getWidth();                    // Largura da imagem.
        int Histograma[] = Calcular_Niveis_Cinza_(Imagem);  // Histograma da imagem em niveis de cinza.
        
        
        // ===================================
        // Calculo das funcoes Mi_0 e Mi_1.
        // ===================================

        int Aux_Mi_0[] = new int[2];
        int Aux_Mi_1[] = new int[2];
        
        double Mi_0[] = new double[256];    // Valores de Mi_0(t).
        double Mi_1[] = new double[256];    // Valores de Mi_1(t).

        // Loop para calcular os valores de Mi_0 e Mi_1.
        for(i = 0, j = 255; i < 256; i++, j--){
            
            // Equacao Mi_0.
            Aux_Mi_0[0] += (Histograma[i] * i);
            Aux_Mi_0[1] += Histograma[i];
            
            if(Aux_Mi_0[1] != 0)
                Mi_0[i] = (double)(Aux_Mi_0[0] / Aux_Mi_0[1]);
            
            
            // Equacao Mi_1.
            if(j > 0){
                
                Aux_Mi_1[0] += (Histograma[j] * j);
                Aux_Mi_1[1] += Histograma[j];

                if(Aux_Mi_1[1] != 0)
                    Mi_1[j-1] = (double)(Aux_Mi_1[0] / Aux_Mi_1[1]);
            }
        }
        
        
        // ============================
        // Calculo da funcao E(t).
        // ============================
        
        int Aux = 0;
        double Ux, Hf;                  // Valor de Ux(g) e Hf(x).
        double Et[] = new double[256];  // Valores de E(t).
        
        int Niveis[] = Encontrar_Maior_Menor_Nivel_Cinza_(Histograma);  // 1º maior e 1º menor nivel de cinza.
        int C        = (Niveis[1] - Niveis[0]);                         // Constante.
        double C1    = (1.0 / (Largura * Altura));                      // Constante.
        
        // Loop para calcular os valores de E(t).
        for(i = 0; i < 256; i++){
            for(j = 0; j < 256; j++){
                
                // Equacao Ux.
                if(j <= i)
                    Ux = (1.0 / (1 + abs(j - (Mi_0[i]/C))));
                else
                    Ux = (1.0 / (1 + abs(j - (Mi_1[i]/C))));
                
                // Equacao Hf.
                if((Ux <= 0) || (Ux >= 1))
                    Hf = 0;
                else
                    Hf = (-Ux * Math.log10(Ux)) - ((1-Ux) * (Math.log10(1 - Ux)));
                
                Aux += (Hf * Histograma[j]);
            }
            
            Et[i] = (C1 * Aux);
            Aux   = 0;
        }
        
        
        // ============================
        // Calculo do limiar.
        // ============================
        
        int Limiar = Niveis[0];     // 1º menor nivel de cinza.
        double Met = Et[Limiar];
        
        // Loop para calcular o limiar.
        for(i = 1; i < 256; i++){
            
            if(Met > Et[i]){
                
                Met    = Et[i];
                Limiar = i;
            }
        }
        
        return Aplicar_Binarizacao_(Imagem, Limiar);
    }
}