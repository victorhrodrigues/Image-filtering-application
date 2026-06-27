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
 * @author victor Henrique Rodrigues
 */
public class AdaptiveMedianFilterGlobal {
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
            pixels.get(0),                      // sMin
            pixels.get(pixels.size() / 2),      // sMed
            pixels.get(pixels.size() - 1)       // sMax
        };
    }
    
    private static void setPixelCinza(BufferedImage img, int x, int y, int valor) {
        int pixel = (0xFF << 24) | (valor << 16) | (valor << 8) | valor;
        img.setRGB(x, y, pixel);
    }
    
    public static BufferedImage adaptiveMedianCinza(BufferedImage Imagem, int wMax) {
        int largura = Imagem.getWidth();
        int altura = Imagem.getHeight();
        
        BufferedImage entradaAtual = Processamento_Imagem_.CriaCopiaCinza(Imagem);
        BufferedImage saida = Processamento_Imagem_.CriaCopiaCinza(Imagem);
        
        // Matriz para controlar se é um pixel ruído ou não é mais
        boolean[][] finalizado = new boolean[largura][altura];
        
        int w = 3; // Passo 1
        
        // O loop roda enquanto a janela for menor que o máximo escolhido
        while (w <= wMax) {
            boolean houveExpansaonestaRodada = false;
            
            for (int linha = 0; linha < largura; linha++) {
                for (int coluna = 0; coluna < altura; coluna++) {
                    
                    // Se o pixel já foi resolvido em uma janela menor, pula ele
                    if (finalizado[linha][coluna]) {
                        continue;
                    }
                    
                    // Passo 2
                    int[] niveisBrilho = calcularEstatisticas(entradaAtual, linha, coluna, w);
                    int sMin = niveisBrilho[0];
                    int sMed = niveisBrilho[1];
                    int sMax = niveisBrilho[2];
                    
                    // Passo 3(Verifica se a mediana(brilho) atual é confiável (não é ruído))
                    if (sMin < sMed && sMed < sMax) {
                        int y = entradaAtual.getRGB(linha, coluna) & 0xFF;
                        
                        // Passo 5 Verifica se o pixel original é saudável
                        if (sMin < y && y < sMax) {
                            setPixelCinza(saida, linha, coluna, y);
                        } else {
                            setPixelCinza(saida, linha, coluna, sMed);
                        }
                        
                        finalizado[linha][coluna] = true;
                    } else {
                        // se a mediana é ruidosa, expande
                        houveExpansaonestaRodada = true;
                        
                        // Passo 4 de segurança: se atingiu o limite do wMax, força a filtragem com a mediana
                        if (w + 2 > wMax) {
                            setPixelCinza(saida, linha, coluna, sMed);
                            finalizado[linha][coluna] = true;
                        }
                    }
                }
            }
            
            /* 
            A diferença para o algoritmo original:
            Se passamos por todos os pixels da imagem e NENHUM precisou expandir a janela,
            significa que o ruído acabou e podemos parar o processamento.
            */ 
            if (!houveExpansaonestaRodada) {
                System.out.println("Otimização: Processamento interrompido na janela " + w + "x" + w + ". Não há mais ruídos.");
                break; 
            }
            
            // Atualiza a imagem de entrada para a próxima rodada com os pixels já corrigidos
            entradaAtual = Processamento_Imagem_.CriaCopiaCinza(saida);
            w = w + 2; // Incrementa o tamanho da janela global
        }
        
        return saida;
    }
    
    public static BufferedImage AdaptiveMedianCorGlobal(BufferedImage Image, int wMax) {
        int largura = Image.getWidth();
        int altura = Image.getHeight();
        
        // 1. Converte a imagem RGB para matriz double pura 
        double[][][] matrizYIQ = ColorSpace_YIQ.converterRGBparaYIQMatriz(Image);
        
        // 2. Isola o Canal Y em uma estrutura de BufferedImage em tons de cinza
        BufferedImage canalYIsolado = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                int yInt = (int) Math.max(0, Math.min(255, Math.round(matrizYIQ[i][j][0])));
                int pixelCinza = (0xFF << 24) | (yInt << 16) | (yInt << 8) | yInt;
                canalYIsolado.setRGB(i, j, pixelCinza);
            }
        }
        
        // 3. Executa a filtragem com a Abordagem Global Otimizada no canal Y isolado
        BufferedImage canalYFiltrado = adaptiveMedianCinza(canalYIsolado, wMax);
        
        // 4. Devolve os dados limpos de luminância para a matriz original, preservando I e Q
        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                double yLimpo = canalYFiltrado.getRGB(i, j) & 0xFF;
                matrizYIQ[i][j][0] = yLimpo;
            }
        }
        
        // 5. Retorna para o espaço RGB reconstituído
        return ColorSpace_YIQ.converterYIQMatrizParaRGB(matrizYIQ);
    }
}


/*
Diferente do alg anterior esse vai fazer a passagem por todos os pixel com uma janela fixa, com essa janela vamos ordenar normalmente o nível dos
pixel, onde o pixel mais escuro fica em Smin, o pixel mais claro em Smax e o pixel da mediana Smed (o pixel no meio da lista ordenada).
Após isso fazermos a verificação se a mediana é confiável ou não (if (sMin < sMed && sMed < sMax)).
verdade:
{
se o if for verdadeiro então a mediana não é ruido, logo a janela w atual é suficiente para tomar a decisão sobre o pixel original y.
como a janela é suficiente para analisar o y vamos para o passo 5, analisando se o pixel original y está corrompido ou saudável:
se SIM Smin < y < Smax o píxel é saudável e é mantido
se NÃO então y é ruído e ele é subistituído por Smed
}
por fim o pixel é marcado como finalizado
falso:
{
se o if for falso então a mediana também é ruido, logo precisamos fazer a expansão (desde que não tenha chegado na expansão máxima) da janela e 
o pixel continua marcado como false

caso seja a ultima expansão possível subistituimos o pixel y pela mediana ruidosa Smed e forçamos o encerramento 
}

Como fazemos para não expandir desnecessáriamente, é controlado pela variável houveExpansaonestaRodada, se for true então w = w + 2
, caso a variável continue false isso significa que não foi necessário a expansão da janela em nenhum pixel analisado e por consequência
não é necessário expandir a janela.
*/