/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Processamento;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Victor Henrique Rodrigues
 */
public class AdaptiveMedianFilter {
    
    /* 
    não é necessário, agora existe um input na aplicação:   
    tamanho da janela máximo (tem que fazer baseado no ruído), segue a tabela disponibilizada no artigo com os valores
      noise level    W_MAX x W_MAX
        r < 25%          5 x 5
    25% < r < 40%        7 x 7
    40% < r < 60%        9 x 9
    60% < r < 70%       13 x 13
    70% < r < 80%       17 x 17
    80% < r < 85%       25 x 25
    85% < r < 90%       39 x 39
    */
    //private static final int W_MAX = 5;
    
    private static int[] calcularEstatisticas(BufferedImage img, int x, int y, int w) {
        List<Integer> pixels = new ArrayList<>();
        int raio = w / 2;
        
        for (int ix = -raio; ix <= raio; ix++) {
            for (int iy = -raio; iy <= raio; iy++) {
                int posX = Math.max(0, Math.min(img.getWidth() - 1, x + ix));
                int posY = Math.max(0, Math.min(img.getHeight() - 1, y + iy));
                pixels.add(img.getRGB(posX, posY) & 0xFF);
            }
        }
        Collections.sort(pixels);
        return new int[]{
            pixels.get(0),                      // brilho mínimo - sMin
            pixels.get(pixels.size() / 2),      // brilho médio - sMed
            pixels.get(pixels.size() - 1)       // brilho máximo - sMax
        };
    }
    
    
    private static void setPixelCinza(BufferedImage img, int x, int y, int valor) {
        /*
        0xFF até 24 é a transparência da imagem, de 23 até 16 canal vermelho, de 
        15 até 8 canal verde e de 7 até 0 bits é o canal azul
        */
        int pixel = (0xFF << 24) | (valor << 16) | (valor << 8) | valor;
        img.setRGB(x, y, pixel);
    }
    
    public static BufferedImage adaptiveMedianCinza(BufferedImage Imagem, int wMax){
        
        BufferedImage saida = Processamento_Imagem_.CriaCopiaCinza(Imagem);
        
        for (int linha = 0; linha < Imagem.getWidth(); linha++){
            for (int coluna = 0; coluna < Imagem.getHeight(); coluna ++){
                // passo 1 
                    int w = 3;
                boolean pixelProcessado = false;
                
                while( w <= wMax && !pixelProcessado){
                    // passo 2
                    int [] niveisBrilho = calcularEstatisticas(Imagem, linha, coluna, w);
                    int sMin = niveisBrilho [0];
                    int sMed = niveisBrilho [1];
                    int sMax = niveisBrilho [2];
                    
                    // passo 3 - verifica se o brilho médio é ruído
                    if(sMin < sMed && sMed < sMax){
                        /* 
                        passo 5 - verifica se o pixel original y é ruído
                        & 0xFF é uma mascara para 255, uma vez que a imagem 
                        é cinza precisamos pegar paenas um dos canais de cor, 
                        como getRGB passa a cor em hexadecimal 0xFF equivale a 
                        255, usando o operador & comparamos bit a bit (tudo que 
                        for comparado com 0 do 0xFF vira zero na informação getRGB
                        tudo que for comparado a 1 no 0xFF manter seu valor no getRGB)
                        */
                        int y = Imagem.getRGB(linha, coluna) & 0xFF; 
                        if(sMin < y && y < sMax){
                            setPixelCinza(saida, linha, coluna, y);
                        }
                        else{
                            setPixelCinza(saida, linha, coluna, sMed);
                        }
                        pixelProcessado = true;
                    }
                    else{
                        // brilho médio é ruidoso, temos que aumentar a janela de acordo com o passo 3
                        w = w + 2;
                    }
                    
                    // passo 4 - w chegou em W_MAX
                    if(w > wMax &&  !pixelProcessado){
                        setPixelCinza(saida, linha, coluna, sMed);
                        pixelProcessado = true;
                    }
                }
            }           
        }
        
        return saida;
    }
    
    public static BufferedImage AdaptiveMedianCor(BufferedImage Image, int wMax){
        int largura = Image.getWidth();
        int altura = Image.getHeight();
        
        // Converte a imagem RGB para a matriz tridimensional pura em double - acredito que assim não precio normalizar entre 0 e 1 
        double[][][] matrizYIQ = ColorSpace_YIQ.converterRGBparaYIQMatriz(Image);
        
        // Isola o Canal Y em uma BufferedImage estruturada em tons de cinza
        BufferedImage canalYImagem = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                int yInt = (int) Math.max(0, Math.min(255, Math.round(matrizYIQ[i][j][0])));
                int pixelCinza = (0xFF << 24) | (yInt << 16) | (yInt << 8) | yInt;
                canalYImagem.setRGB(i, j, pixelCinza);
            }
        }
        
        // aplica o filtro adaptativo  na imagem com brilho isolad
        BufferedImage canalYFiltrado = adaptiveMedianCinza(canalYImagem, wMax);
        
        // Devolve o brilho limpo para a matriz YIQ
        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                double yLimpo = canalYFiltrado.getRGB(i, j) & 0xFF;
                matrizYIQ[i][j][0] = yLimpo; 
            }
        }
        
        // Executa a conversão inversa (Matriz YIQ -> RGB Colorida Reconstituída)
        return ColorSpace_YIQ.converterYIQMatrizParaRGB(matrizYIQ);
    }
}
