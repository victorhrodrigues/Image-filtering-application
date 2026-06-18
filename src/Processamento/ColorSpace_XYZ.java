/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Processamento;

import Telas.Tela_Espaco_cor;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Lucas Sala Alves
 */
public class ColorSpace_XYZ {
     /*  Algoritmo para deteccao de Pele, desenvolvido
        por Lucas Sala Alves - 2025/1.
    */
    public static BufferedImage Detection(BufferedImage Imagem,Tela_Espaco_cor Tela) {
    
        int Xmin = (int) Tela.Xmin.getValue();
        int Xmax = (int) Tela.Xmax.getValue();
        int Ymin = (int) Tela.Yxyzmin.getValue();
        int Ymax = (int) Tela.Yxyzmax.getValue();
        int Zmin = (int) Tela.Zmin.getValue();
        int Zmax = (int) Tela.Zmax.getValue();

        /*  Loop para percorrer os pixels da imagem (Coluna x Linha), e
            calcular os indices maximo e minimo de Bluness na imagem.   */
        for(int i = 0; i < Imagem.getWidth(); i++) {
            for(int j = 0; j < Imagem.getHeight(); j++) {

                // Niveis de cor de cada pixel.
                Color C = new Color(Imagem.getRGB(i, j));
                double R = Double.valueOf(C.getRed());
                double G = Double.valueOf(C.getGreen());
                double B = Double.valueOf(C.getBlue());

                double X;
                double Y;
                double Z;

                X =  0.412453*R + 0.357580*G + 0.180423*B;
                Y =  0.212671*R + 0.715160*G + 0.072169*B;
                Z =  0.019334*R + 0.119193*G + 0.950227*B;

                Color Novo = new Color(0, 0, 0);
                if(Tela.Xmin.isEnabled() && !(Xmin<X && X<Xmax)){
                    Imagem.setRGB(i, j, Novo.getRGB());
                }      
                if(Tela.Yxyzmin.isEnabled() && !(Ymin<Y && Y<Ymax)){
                    Imagem.setRGB(i, j, Novo.getRGB());
                }    
                if(Tela.Zmin.isEnabled() && !(Zmin<Z && Z<Zmax)){
                    Imagem.setRGB(i, j, Novo.getRGB());
                }    
            }
        }

        return Imagem;
    }
}
