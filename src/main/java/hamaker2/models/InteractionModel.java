/*
 * InteractionModel.java
 *
 * Created on March 24, 2007, 3:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hamaker2.models;

import hamaker2.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Define the interface for an interaction model
 * @author uli
 */
public interface InteractionModel {

   public ArrayList<GeometryClass> ImplementedGeometryClasses();

   public String id();

   public String name();
   public String reference();
   public String version();
   public boolean additionalParameters();
   public void showMoreDialog();
   
   public double interactionPotential(Serie serie, double size);
   public double interactionForce(Serie serie, double size);
   public double interactionPotential(Serie serie, double size1, double size2);
   public double interactionForce(Serie serie, double size1, double size2);
      
   public boolean getNeedsSave();
   public void save(BufferedWriter output) throws IOException;
   public void load(BufferedReader input) throws IOException;

   public InteractionModel duplicate();
}
