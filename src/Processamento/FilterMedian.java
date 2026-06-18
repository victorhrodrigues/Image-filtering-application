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

public class FilterMedian {


    public static BufferedImage filterMedianCinza(BufferedImage Imagem) {
        int largura = Imagem.getWidth();
        int altura = Imagem.getHeight();
        
        // criando o buffer de saída 
        BufferedImage saida = Processamento_Imagem_.CriaCopiaCinza(Imagem);

        // Janela 3x3: começamos em 1 e terminamos em largura-1 para evitar erro de borda
        for (int i = 1; i < largura - 1; i++) {
            for (int j = 1; j < altura - 1; j++) {
                
                List<Integer> vizinhanca = new ArrayList<>();

                // Coleta dos pixels vizinhos
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        // Como a imagem é cinza, pegamos apenas um canal, os outros são iguais
                        int pixel = Imagem.getRGB(i + x, j + y);
                        int tomDeCinza = pixel & 0xFF; 
                        vizinhanca.add(tomDeCinza);
                    }
                }
                /*
                Ordenação para encontrar o valor central (Mediana), .sort utiliza a ordenação chamada TimSort
                que é um algoritmo derivado do MergSort com o InsertionSort, ele garante O(n log n) e funciona
                bem com dados parcialmente ordenados 
                */
                Collections.sort(vizinhanca);
                int mediana = vizinhanca.get(4); // O 5º elemento de 9

                // Montamos o pixel de volta (R, G e B recebem o mesmo valor da mediana)
                int pixelSaida = (0xFF << 24) | (mediana << 16) | (mediana << 8) | mediana;
                saida.setRGB(i, j, pixelSaida);
            }
        }
        return saida;
    }

 
    public static BufferedImage filterMedianColorida(BufferedImage Imagem) {
        // Lógica para processar R, G e B de forma independente
        return Imagem; 
    }
}
