package edu.upc.epsevg.prop.amazons;

import edu.upc.epsevg.prop.amazons.players.HumanPlayer;
import edu.upc.epsevg.prop.amazons.players.CarlinhosPlayer;
import edu.upc.epsevg.prop.amazons.players.*;
import java.io.BufferedInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.SwingUtilities;

/**
 *
 * @author bernat
 */
public class Amazons {
   /**
     * @param args
     */
    public static void main(String[] args) {


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                //IPlayer player1 = new HumanPlayer("Oferta");
                IPlayer player1 = new HacendadoRider("Chicken-Rider");
                //IPlayer player2 = new RandomPlayer("Chicken-Rider");
                IPlayer player2 = new CarlinhosPlayer();

                //IPlayer player2 = new Hacendado_Rider("Chicken-Rider");
                //IPlayer player1 = new CarlinhosPlayer();

                new AmazonsBoard(player1 , player2, 10, Level.HALF_BOARD);
                try {
                    //BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream("/resources/theEdgeOfDawn.wav"));
                    BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream("/resources/mercadona.wav"));
                    AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                    // Se obtiene un Clip de sonido
                    Clip sonido = AudioSystem.getClip();//Obtenemos el clip

                    // Se carga con un fichero wav
                    sonido.open(ais);//Abrimos

                    // Comienza la reproducción
                    //sonido.start();//Iniciamos

                    // Espera mientras se esté reproduciendo.
                    //
                    /*        while (sonido.isRunning()) {
                        Thread.sleep(1000);
                    }*/

                    // Se cierra el clip.
                    sonido.close();
                } catch (Exception e) {
                    System.out.println("" + e);
                }
            }
        });
    }
}
