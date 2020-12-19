/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.amazons.players;

import edu.upc.epsevg.prop.amazons.CellType;
import edu.upc.epsevg.prop.amazons.GameStatus;
import edu.upc.epsevg.prop.amazons.IAuto;
import edu.upc.epsevg.prop.amazons.IPlayer;
import edu.upc.epsevg.prop.amazons.Move;
import edu.upc.epsevg.prop.amazons.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author rochi, presi
 */
public class HacendadoRider implements IPlayer, IAuto {

    private String name;
    private GameStatus s;
    private boolean haAcabat = false;

    public HacendadoRider(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
        haAcabat = true;
    }

    private int max(GameStatus s, int depth, CellType player) {
        Point queenTo = null;
        Point queenFrom = null;
        Point arrowTo = null;
        Integer valor = Integer.MIN_VALUE;

        if (depth == 0 || s.isGameOver()) {
            //Random rand = new Random();
            //valor = rand.nextInt(500);
            valor = heuristica(s);
        } else {
            int qn = s.getNumberOfAmazonsForEachColor();
            ArrayList<Point> pendingAmazons = new ArrayList<>();
            for (int q = 0; q < qn; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }

            for (int i = 0; i < pendingAmazons.size(); i++) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                for (int j = 0; j < possibleMove.size(); j++) {
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    backUp.moveAmazon(pendingAmazons.get(i), actual);
                    //moure fletxa
                    //arrowTo = posicioRandom(backUp);
                    arrowTo = fletxa(backUp);
                    int value = min(backUp, depth - 1, player);
                    ///for (int k = 0; k > 0; k++){}
                    valor = Math.max(value, valor);
                }
            }
        }
        return valor;
    }

    private int min(GameStatus s, int depth, CellType player) {
        Point queenTo = null;
        Point queenFrom = null;
        Point arrowTo = null;
        Integer valor = Integer.MAX_VALUE;

        if (depth == 0 || s.isGameOver()) {
            //Random rand = new Random();
            //valor = rand.nextInt(500);
            valor = heuristica(s);
        } else {
            int qn = s.getNumberOfAmazonsForEachColor();
            ArrayList<Point> pendingAmazons = new ArrayList<>();
            for (int q = 0; q < qn; q++) {
                pendingAmazons.add(s.getAmazon(player, q));
            }

            for (int i = 0; i < pendingAmazons.size(); i++) {
                ArrayList<Point> possibleMove = new ArrayList<>();
                possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
                for (int j = 0; j < possibleMove.size(); j++) {
                    GameStatus backUp = new GameStatus(s);
                    Point actual = new Point(possibleMove.get(j));
                    backUp.moveAmazon(pendingAmazons.get(i), actual);
                    //moure fletxa
                    //arrowTo = posicioRandom(backUp);
                    arrowTo = fletxa(backUp);
                    int value = max(backUp, depth - 1, player);
                    ///for (int k = 0; k > 0; k++){}
                    valor = Math.min(value, valor);
                }
            }
        }
        return valor;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public Move move(GameStatus s) {
        Point queenTo = null;
        Point queenFrom = null;
        Point arrowTo = null;
        CellType player = s.getCurrentPlayer();
        this.s = s;
        int depth = 2;
        int valor = -9999;
        Move millor = null;
        int qn = s.getNumberOfAmazonsForEachColor();
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }

        for (int i = 0; i < pendingAmazons.size(); i++) {
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
            for (int j = 0; j < possibleMove.size(); j++) {
                GameStatus backUp = new GameStatus(s);
                Point actual = new Point(possibleMove.get(j));
                backUp.moveAmazon(pendingAmazons.get(i), actual);
                //moure fletxa
                //arrowTo = posicioRandom(backUp);
                arrowTo = fletxa(backUp);
                int value = min(backUp, depth - 1, player);
                if (value > valor) {
                    valor = value;
                    millor = new Move(pendingAmazons.get(i), possibleMove.get(j), arrowTo, 0, 0, SearchType.MINIMAX);
                }
                ///for (int k = 0; k > 0; k++){}
            }
        }
        return millor;
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }

    private Point posicioRandom(GameStatus s) {
        int n = s.getEmptyCellsCount();

        Random rand = new Random();
        int p = rand.nextInt(n) + 1;//de 1 a n
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i, j) == CellType.EMPTY) {
                    p--;
                    if (p == 0) {
                        return new Point(i, j);
                    }
                }
            }
        }
        throw new RuntimeException("Random exhausted");
    }

    private int heuristica(GameStatus s) {
        int res = 0;

        CellType player = s.getCurrentPlayer();
        //fletxa

        //jugador amic
        int qn = s.getNumberOfAmazonsForEachColor();
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }

        for (int i = 0; i < pendingAmazons.size(); i++) {
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
            res += possibleMove.size();
        }

        //jugador enemic
        player = player.opposite(player);
        pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }

        for (int i = 0; i < pendingAmazons.size(); i++) {
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);
            res -= possibleMove.size();
        }

        return res;
    }
//}

    private Point fletxa(GameStatus s) {
        /* mirem o estan totes les amazones enemigues
     mirem quines tenen mes moviments
     tanquem la direccio millor a la que te mes moviments
         */
        ArrayList<Integer> direccions = new ArrayList<>();
        CellType player = s.getCurrentPlayer();
        player = player.opposite(player);
        int qn = s.getNumberOfAmazonsForEachColor();
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(player, q));
        }
        int movesAmazon = 0;
        Point bestAmazon = new Point(0, 0);
        for (int i = 0; i < pendingAmazons.size(); i++) { //escollir millor Amazon enemiga per putejar
            ArrayList<Point> possibleMove = new ArrayList<>();
            possibleMove = s.getAmazonMoves(pendingAmazons.get(i), false);

            if (possibleMove.size() > movesAmazon) {
                bestAmazon = pendingAmazons.get(i);
                movesAmazon = possibleMove.size();
            }
        }
 
        //puteada de la bestAmazon
        int cont = 0;//contador d'espais buits a la dreta

        double x = bestAmazon.getX() + 1;
        double y = bestAmazon.getY();
        boolean trobat = false;
        while (!trobat && x < s.getSize()) {//paret lateral dreta
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            x++;
        }
        direccions.add(cont);
        cont = 0;//contador d'espais buits a l'esquerra

        x = bestAmazon.getX() - 1;
        trobat = false;
        while (!trobat && x > -1) {//paret lateral esquerra
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            x--;
        }
        direccions.add(cont);

        cont = 0;//contador d'espais buits cap a dalt

        x = bestAmazon.getX();
        y = bestAmazon.getY() + 1;
        trobat = false;
        while (!trobat && y < s.getSize()) {//paret lateral superior
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y++;
        }
        direccions.add(cont);

        cont = 0;//contador d'espais buits cap a baix

        y = bestAmazon.getY() - 1;
        trobat = false;
        while (!trobat && y > -1) {//paret lateral inferior
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y--;
        }
        direccions.add(cont);

        cont = 0;//contador d'espais buits dreta superior

        x = bestAmazon.getX() + 1;
        y = bestAmazon.getY() + 1;
        trobat = false;
        while (!trobat && y < s.getSize() && x < s.getSize()) {//parets laterals superior-dreta
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y++;
            x++;
        }
        direccions.add(cont);

        cont = 0;//contador d'espais buits inferior-esquerra

        x = bestAmazon.getX() - 1;
        y = bestAmazon.getY() - 1;
        trobat = false;
        while (!trobat && y > -1 && x > -1) {//paret laterals inferior-esquerra
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y--;
            x--;
        }
        direccions.add(cont);

        cont = 0;//contador d'espais buits inferior-dreta

        x = bestAmazon.getX() + 1;
        y = bestAmazon.getY() - 1;
        trobat = false;
        while (!trobat && y > -1 && x < s.getSize()) {//paret laterals inferior-dreta
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y--;
            x++;
        }
        direccions.add(cont);

        cont = 0;//contador d'espais buits superior-esquerra

        x = bestAmazon.getX() - 1;
        y = bestAmazon.getY() + 1;
        trobat = false;
        while (!trobat && x > -1 && y < s.getSize()) {//paret laterals superior-esquerra
            if (s.getPos((int)(x), (int)(y)) == CellType.EMPTY) {
                cont++;
            } else {
                trobat = true;
            }
            y++;
            x--;
        }
        direccions.add(cont);

        int maxim = Collections.max(direccions);
        int i = 0;
        if(maxim != 0){
          trobat = false;
          while (!trobat){
            if(direccions.get(i) == maxim) trobat = true;
            else ++i;
          }
        } else i = 9;


        switch (i) {
            case 0 -> {
                //contador d'espais buits a la dreta
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX() + 1), (int)(bestAmazon.getY()));
                return mouFletxa;
            }

            case 1 -> {
                //contador d'espais buits a l'esquerra
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX() - 1), (int)(bestAmazon.getY()));
                return mouFletxa;
            }

            case 2 -> {
                //contador d'espais buits cap a dalt
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX()), (int)(bestAmazon.getY() + 1));
                return mouFletxa;
            }

            case 3 -> {
                //contador d'espais buits cap a baix
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX()), (int)(bestAmazon.getY() - 1));
                return mouFletxa;
            }

            case 4 -> {
                //contador d'espais buits dreta superior
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX() + 1), (int)(bestAmazon.getY() + 1));
                return mouFletxa;
            }

            case 5 -> {
                //contador d'espais buits inferior-esquerra
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX() - 1), (int)(bestAmazon.getY() - 1));
                return mouFletxa;
            }

            case 6 -> {
                //contador d'espais buits inferior-dreta
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX() + 1), (int)(bestAmazon.getY() - 1));
                return mouFletxa;
            }

            case 7 -> {
                //contador d'espais buits superior-esquerra
                // code block
                Point mouFletxa = new Point((int)(bestAmazon.getX() - 1), (int)(bestAmazon.getY() + 1));
                return mouFletxa;
            }

            case 9 -> {
                //contador d'espais buits superior-esquerra
                // code block
                Point mouFletxa = posicioRandom(s);
                return mouFletxa;
            }
        }

        //constants getPos() -> EMPTY, PLAYER1, PLAYER2 o FIRE
        //for +X,0 getPos(x,y) dreta
        //for -X,0 esquerre
        //return res;
        return null;
    }
}
