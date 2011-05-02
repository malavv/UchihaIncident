package com.ninja.exMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * La classe représentant la grille de jeu.
 */
public class PlayGrid {
  /** Maximum de lignes permises. */
  private static int kMaxRows = 50;
  /** Maximum de colonnes permises. */
  private static int kMaxCols = 50;
  /** Maximum de points sur la grille permis. */
  private static int kMaxDots = 10;
  /** Le rayons des points à dessiner. */
  private static float kPointRadius = 10;
  /** 
   * La distance des points à laquelle on peut touché et considéré sa comme si
   * on avait vraiment touché le point.
   * NOTE(malavv) : J'ai trouvé que mettre le touch radius au même que le rayon
   *                du point était vraiment dur.
   */
  private static float kTouchRadius = 25;
  /** 
   * Cette valeur est la partie de l'écran ou il n'est pas permis d'avoir des
   * points en pourcentage horizontal de chaque coté.
   */
  private static float kHorizontalUnusedPercent = 0.15f;
  /** 
   * Cette valeur est la partie de l'écran ou il n'est pas permis d'avoir des
   * points en pourcentage vertical de chaque coté.
   */
  private static float kVerticalUnusedPercent = 0.15f;
  
  /**
   * Le nombre de points à toucher pour gagné.
   */
  private int nDots;
  /**
   * Les points seront alignés sur une grille virtuelle avec nCols colonne.
   */
  private int nCols;
  /**
   * Les points seront alignés sur une grille virtuelle avec nRows lignes.
   */
  private int nRows;
  
  /**
   * La liste des points à toucher pour gagner sur une grille de 0 - nRows | nCols.
   */
  private ArrayList<GridElement> gridElements = new ArrayList<GridElement>();
  /**
   * La liste des points à toucher en valeurs absolue par rapport à l'écran.
   * Cette liste est temporaire et est seulement une cache pour ne pas recalculer
   * la position à chaque fois.
   */
  private ArrayList<DrawElementF> drawCache = new ArrayList<DrawElementF>();
  
  /** Un générateur de nombre aléatoire. */
  private Random random = new Random();
  
  /** La brosse pour les éléments non-cliqués. */
  private Paint unclickedItem = new Paint();
  /** La brosse pour les éléments cliqués. */
  private Paint clickedItem = new Paint();
  
  /** La couleur des éléments non-cliqués. */
  private int unclickedColor = Color.WHITE;
  /** La couleur des éléments cliqués. */
  private int clickedColor = Color.RED;
  
  /**
   * Getter pour le nombre de lignes.
   * @return Le nombre de lignes de la grille virtuelle.
   */
  public int Rows() { return nRows; }
  /**
   * Getter pour le nombre de colonne.
   * @return Le nombre de colonnes de la grille virtuelle.
   */
  public int Cols() { return nCols; }
  /**
   * Getter pour le nombre de points à placer dans la grille.
   * @return Le nombre de points à placer.
   */
  public int Dots() { return nDots; }
  
  /**
   * Setter pour le nombre de lignes, sera ajuster [0, kMaxRows].
   * @param rows Le nvx nombre de lignes.
   */
  public void Rows(int rows) {
    nRows = (rows > 0 && rows <= kMaxRows)? rows: nDots;
    ComputeGrid();
  }
  /**
   * Setter pour le nombre de colonnes, sera ajuster [0, kMaxCols].
   * @param rows Le nvx nombre de colonnes.
   */  
  public void Cols(int cols) {
    nCols = (cols > 0 && cols <= kMaxCols)? cols: nDots;
    ComputeGrid();  
  }
  /**
   * Setter pour le nombre de points, sera ajuster [0, kMaxDots].
   * @param rows Le nvx nombre de points.
   */  
  public void Dots(int dots) {
    nDots = (dots > 0 && dots <= kMaxDots)? dots: nDots;
    Rows(nRows);
    Cols(nCols);
    ComputeGrid();
  }
  
  /**
   * Constructeur par défaut pour la grille de jeux. Utilise les valeurs par
   * défault.
   */
  public PlayGrid() {
    nDots = 4;
    nCols = 10;
    nRows = 10;
    
    unclickedItem.setAntiAlias(true);
    unclickedItem.setColor(unclickedColor);
    
    clickedItem.setAntiAlias(true);
    clickedItem.setColor(clickedColor);
    
    ComputeGrid();
  }
  
  /**
   * Dessine la grille dans le canvas actuel.
   * @param c Le canvas dans lequel dessiner.
   * @param bounds Les dimmensions du canvas.
   */
  public void Draw(Canvas c, RectF bounds) {
    if (drawCache.size() == 0)  computeDraw(bounds);
    
    for (int i = 0; i < drawCache.size(); i++) {
      Paint color = (drawCache.get(i).element.isHit) ? clickedItem : unclickedItem;
      c.drawCircle(drawCache.get(i).pos.x, drawCache.get(i).pos.y, kPointRadius, color);
    }
  }
  
  /**
   * Gère les événements touch pour ajuster la grille.
   * @param e L'événement motion à tester.
   */
  public void DoTouch(MotionEvent e) {
    if (drawCache.size() == 0)  return;
    
    for (int i = 0; i < drawCache.size(); i++) {
      if (FollowupLine.DistanceBetweenTwoPoints(drawCache.get(i).pos, new PointF(e.getX(), e.getY())) <= kTouchRadius) {
        drawCache.get(i).element.isHit = true;
      }
    }
  }
  
  /**
   * Check pour voir si la grille est complète.
   * @return True si tous les points ont été touchés.
   */
  public boolean IsWon() {
    boolean clickedAll = true;
    for (GridElement e : gridElements)  if (!e.isHit)  clickedAll = false;
    return clickedAll;
  }
  
  /**
   * Calcul la correspondance entre les points de notre grille virtuelle et 
   * l'écran réel. Store ces valeurs en cache pour ne plus avoir à les calculés.
   * @param bounds Les dimensions du canvas actuel.
   */
  private void computeDraw(RectF bounds) {
    RectF validRect = new RectF(bounds.width() * kHorizontalUnusedPercent + bounds.left,
                                bounds.height() * kVerticalUnusedPercent + bounds.top,
                                bounds.right - bounds.width() * kHorizontalUnusedPercent,
                                bounds.bottom - bounds.height() * kVerticalUnusedPercent);
    for (int i = 0; i < gridElements.size(); i++) {
      float colsSize = validRect.width() / nCols;
      float rowsSize = validRect.height() / nRows;
      
      float x = validRect.left + gridElements.get(i).pos.x * colsSize + colsSize / 2;
      float y = validRect.bottom -  gridElements.get(i).pos.y * rowsSize - rowsSize / 2;
      
      drawCache.add(new DrawElementF(new PointF(x, y), gridElements.get(i)));
    }
  }
  
  /**
   * Générer une grille virtuelle à remplir de points.
   */
  private void ComputeGrid() {
    ArrayList<Point> availaibleGrid = GetFullAvailableGrid();
    gridElements.clear();
    
    for (int i = 0; i < nDots; i++) {
      Point chosenPoint = availaibleGrid.get(random.nextInt(availaibleGrid.size() - 1));
      gridElements.add(new GridElement(chosenPoint));
      availaibleGrid.removeAll(AssociatedPoint(chosenPoint));
    }
  }
  
  /**
   * Retourne une série de points relier à celui qui vient d'être ajouter pour
   * les retirer de la liste des points à ajouter.
   * @param point Le point qui viens d'être ajouté.
   * @return Tous les points reliés qui doivent être enlevé.
   */
  private Collection<Point> AssociatedPoint(Point point) {
    ArrayList<Point> pointsToRemove = new ArrayList<Point>();
    
    for (int i = 0; i < nCols; i++)  pointsToRemove.add(new Point(i, point.y));
    for (int i = 0; i < nRows; i++)  pointsToRemove.add(new Point(point.x, i));
    
    return pointsToRemove;
  }
  
  /**
   * Retourne une liste pleine de tous les points possible de la grille.
   * @return Une grille complète.
   */
  private ArrayList<Point> GetFullAvailableGrid() {
    ArrayList<Point> availGrid = new ArrayList<Point>();
    
    for (int i = 0; i < nCols; i++) {
      for (int j = 0; j < nRows; j++) {
        availGrid.add(new Point(i, j));
      }
    }
    
    return availGrid;
  }
  
  /**
   * Classe représentant un point dans la grille virtuelle.
   */
  private class GridElement {
    /**
     * Créé un GridElement à partir du point sur la grille, par défault à non-pressé.
     * @param p Le point de la grille virtuelle.
     */
    public GridElement(Point p) { pos = p; isHit = false; }
    /** Le point de la grille virtuelle. */
    public Point pos;
    /** Es-ce que le point à été touché. */
    public boolean isHit;
  }
  
  /**
   * Classe représentant l'élément en cache à dessiner.
   */
  private class DrawElementF {
    /** La cache pour l'élément. */
    public DrawElementF(PointF p, GridElement e) { pos = p; element= e; }
    /** Le point sur l'écran. */
    public PointF pos;
    /** L'élément sous-jacent étant représenté. */
    public GridElement element;
  }
}
