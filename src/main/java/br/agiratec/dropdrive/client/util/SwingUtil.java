package br.agiratec.dropdrive.client.util;

import javax.swing.ImageIcon;

public class SwingUtil {

	/**
	* Este metodo retorna uma imagem criada baseado na url passada para ele
	* @return ImageIcon
	* @author Igor Maldonado Floor
	*/
	public static ImageIcon criarImageIcon(String caminho, String descricao) {  
        java.net.URL imgURL = SwingUtil.class.getResource(caminho);  
        if (imgURL != null) {  
            return new ImageIcon(imgURL, descricao);  
        } else {  
            System.err.println("Nao foi possivel carregar o arquivo de imagem: " + caminho);  
            return null;  
        }  
	}
	
}
