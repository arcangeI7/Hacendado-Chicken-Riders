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
import java.util.Random;

/**
 *
 * @author rochi, presi
 */
public class HacendadoRider implements IPlayer, IAuto {

    private String name;
    private GameStatus s;

    public HacendadoRider(String name) {
        this.name = name;
    }

    @Override
    public void timeout() {
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

        private int max(GameStatus s, int depth, CellType player) {
        Point queenTo = null;
        Point queenFrom = null;
        Point arrowTo = null;
        Integer valor = Integer.MIN_VALUE;

        if (depth == 0 || s.isGameOver()) {
            Random rand = new Random();
            valor = rand.nextInt(500);
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
                    arrowTo = posicioRandom(backUp);
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
            Random rand = new Random();
            valor = rand.nextInt(500);
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
                    arrowTo = posicioRandom(backUp);
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
                arrowTo = posicioRandom(backUp);
                int value = min(backUp, depth - 1, player);
                if (value > valor){
                    valor = value;
                    millor = new Move(pendingAmazons.get(i), possibleMove.get(j), arrowTo, 0, 0, SearchType.MINIMAX);
                }
                ///for (int k = 0; k > 0; k++){}
            }
        }
       /* queenFrom = pendingAmazons.get(3);
        ArrayList<Point> possibleMove = new ArrayList<>();
        possibleMove = s.getAmazonMoves(pendingAmazons.get(3), false);
        queenTo = possibleMove.get(1);
        arrowTo = posicioRandom(s);*/

        //minimax(s, depth, player);
        return millor;
    }

    /*public Move move(GameStatus s) {
        this.s= s;
        CellType color = s.getCurrentPlayer();
        Point queenTo = null;
        Point queenFrom = null;
        CellType su = color.opposite(color); //color enemic
        Point arrowTo = s.getAmazon(su, 1);  //posicio fitxa1 enemic
            
        int qn = s.getNumberOfAmazonsForEachColor();   //amazones del tauler
        ArrayList<Point> pendingAmazons = new ArrayList<>(); //list de les amazones a avaluar
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getAmazon(color, q));
        }

            // Iterem aleatòriament per les reines fins que trobem una que es pot moure.
            while (queenTo == null) {
                queenFrom = pendingAmazons.remove(0); //treiem la primera amazona de la llista
                queenTo = posicioRandomAmazon(queenFrom); //li donem una posicio random
            }
            // LA FLETXA VA UNA POSICIO PER SOBRE LA DE LA AMAZONA RIVAL  //
            //arrowTo = s.getAmazon(su, 1);
            System.out.println("posicio amazona rival: "+ arrowTo.getLocation());
            arrowTo.setLocation​(arrowTo.getX(), arrowTo.getY()-1);
            System.out.println("posicio de la fletxa: "+ arrowTo.getLocation());
            //s.moveAmazon(queenFrom, queenTo);

        //}
        return new Move(queenFrom, queenTo, arrowTo, 0, 0, SearchType.MINIMAX);
    }*/

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }

    private Point posicioRandomAmazon(Point pos) {
        ArrayList<Point> points = new ArrayList<>();
        int[] dx = {1, -1, 0, 0, 1, -1, 1, -1};
        int[] dy = {0, 0, 1, -1, 1, -1, -1, 1};

        for (int d = 0; d < dx.length; d++) {
            int x = pos.x;
            int y = pos.y;
            x += dx[d];
            y += dy[d];
            while (isInBounds(x, y) && s.getPos(x, y) == CellType.EMPTY) {
                points.add(new Point(x, y));
                x += dx[d];
                y += dy[d];
            }
        }
        if (points.size() == 0) {
            return null;//no es pot moure
        }
        Random rand = new Random();
        int p = rand.nextInt(points.size());
        return points.get(p);
    }

    private boolean isInBounds(int x, int y) {
        return (x >= 0 && x < s.getSize())
                && (y >= 0 && y < s.getSize());
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

}
