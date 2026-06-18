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
public class ColorSpace_YCbCr {   
    /*  Algoritmo para deteccao de Pele, desenvolvido
        por Lucas Sala Alves - 2025/1.
    */
    public static BufferedImage Detection(BufferedImage Imagem,Tela_Espaco_cor Tela) {
    
        int Ymin  = (int) Tela.Ymin.getValue();
        int Ymax  = (int) Tela.Ymax.getValue();
        int Cbmin = (int) Tela.Cbmin.getValue();
        int Cbmax = (int) Tela.Cbmax.getValue();
        int Crmin = (int) Tela.Crmin.getValue();
        int Crmax = (int) Tela.Crmax.getValue();
        

        /*  Loop para percorrer os pixels da imagem (Coluna x Linha), e
            calcular os indices maximo e minimo de Bluness na imagem.   */
        for(int i = 0; i < Imagem.getWidth(); i++) {
            for(int j = 0; j < Imagem.getHeight(); j++) {

                // Niveis de cor de cada pixel.
                Color C = new Color(Imagem.getRGB(i, j));
                double R = Double.valueOf(C.getRed());
                double G = Double.valueOf(C.getGreen());
                double B = Double.valueOf(C.getBlue());

                double Y;
                double Cb;
                double Cr;

                Y  =  0.257*R + 0.504*G + 0.098*B + 16;
                Cb = -0.148*R - 0.291*G + 0.439*B + 128;
                Cr =  0.439*R - 0.368*G - 0.071*B + 128;
                
                Color Novo = new Color(0, 0, 0);
                if(Tela.Ymin.isEnabled() && !(Ymin<Y && Y<Ymax)){
                    Imagem.setRGB(i, j, Novo.getRGB());
                }      
                if(Tela.Cbmin.isEnabled() && !(Cbmin<Cb && Cb<Cbmax)){
                    Imagem.setRGB(i, j, Novo.getRGB());
                }    
                if(Tela.Crmin.isEnabled() && !(Crmin<Cr && Cr<Crmax)){
                    Imagem.setRGB(i, j, Novo.getRGB());
                }    
            }
        }

        return Imagem;
    }
}