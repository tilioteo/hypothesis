/**
 * 
 */
package com.tilioteo.hypothesis.client;

import com.google.gwt.core.client.JavaScriptObject;
import processing.core.IJsEventHandler;
import processing.core.ISketchEventHandler;
import processing.core.PApplet;
import processing.eventjs.JsEventHandler;
import processing.eventjs.SkectchEvent;
import remixlab.dandelion.constraint.AxisPlaneConstraint;
import remixlab.dandelion.constraint.WorldConstraint;
import remixlab.dandelion.core.InteractiveFrame;
import remixlab.dandelion.geom.Vec;
import remixlab.proscene.Scene;

/**
 * @author kamil
 *
 */
public class Hanoi extends PApplet {

    public Hanoi(){}
    public Hanoi(JavaScriptObject ctx) {
    	super(ctx, new JsEventHandler(), new SkectchEvent());
    }
    public Hanoi(JavaScriptObject ctx, IJsEventHandler jsEventHandler,ISketchEventHandler sketchEventHandler  ){
    	super(ctx,jsEventHandler,sketchEventHandler);
    }

    //PASTE HERE THE SKETCH

    Scene scene;
    int nbdisques=5;
    Systeme systeme;
    InteractiveFrame[] frames;
    WorldConstraint contrainteGuide, contraintePlan, immobile ;

    public void setup() {
         size(500, 500, P3D);
         scene=new Scene(this);
         scene.setAxesVisualHint(false);//IsDrawn(false);
         scene.setGridVisualHint(false);//IsDrawn(false);
       
        scene.camera().setPosition(new Vec(0, 0, 600));
     
         frames=new InteractiveFrame[nbdisques];
         //les contraintes
         contraintePlan= new WorldConstraint();
         contraintePlan.setTranslationConstraint(AxisPlaneConstraint.Type.PLANE, new Vec(0, 1, 0));
         contraintePlan.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0)); 
         //
         contrainteGuide= new WorldConstraint();
         contrainteGuide.setTranslationConstraint(AxisPlaneConstraint.Type.AXIS, new Vec(0, 0, 1));
         contrainteGuide.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0)); 
         //
         immobile= new WorldConstraint();
         immobile.setTranslationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
         immobile.setRotationConstraint(AxisPlaneConstraint.Type.FORBIDDEN, new Vec(0, 0, 0));
         //
         for (int i=0;i<nbdisques;i++) {
              frames[i]=new InteractiveFrame(scene);
              frames[i].setConstraint(immobile);
              frames[i].setPosition(-150, 0, 14*(nbdisques-i-1));
              //scene.setInteractiveFrame(frames[i]);
         }
         systeme=new Systeme();
    }

    public void draw()
    {
         //background(50,50,130);
         background(3289730);
         //directionalLight(251, 155, 250, -1, -1, -0.72);
         //directionalLight(155, 155, 255, 0, 1, -0.1);
         //directionalLight(255, 255, 255, -0.7, -0.7, -0.5);     
         pushMatrix();
         fill(205,205,240);
         translate(0, 0, -17);
         box(500, 200, 6);
         fill(155,155,255);
         scene.drawCone(3.0f, 3.0f, 130.0f);
         translate(150, 0, 0);
         scene.drawCone(3.0f, 3.0f, 130f);
         translate(-300, 0, 0);
         scene.drawCone(3.0f, 3.0f, 130f);
         popMatrix();
         systeme.draw();
    }

    class Disque {

        float rayon;
        float hauteur=10;
        int numero, surLePiquet;
        InteractiveFrame repere;
        int c;

        public Disque(int surLePiquet, int numer, InteractiveFrame ir) {
             surLePiquet=0;
             numero=numer;
             rayon=numero*10+10;
             repere=ir;
             c=color(random(50,150),random(50,150),random(50,150));
             
        }

        public void draw() {
             pushMatrix();
             repere.applyTransformation();
             noStroke();
             /*if (repere.grabsMouse ())fill(255, 0, 0);
             else*/ fill(0, 0, 255);
             sphere(5);
             fill(c);
             translate(0, 0, -3);
             ellipse(0, 0, rayon*2, rayon*2);
             translate(0, 0, -hauteur);
             scene.drawCone(rayon, rayon, hauteur);
             ellipse(0, 0, rayon*2, rayon*2);
             popMatrix();
        }

        public void collerAuPiquet() {
             float  dis=sqrt(sq(repere.position().x())+sq(repere.position().y()));
             float  disd=sqrt(sq(repere.position().x()-150)+sq(repere.position().y()));
             float  disg=sqrt(sq(repere.position().x()+150)+sq(repere.position().y())); 
             if (repere.position().z()<120 ) {
                  if ( dis<25) { 
                       repere.setPosition(new Vec(0, 0, repere.position().z()));
                       surLePiquet=1;
                  }
                  if (disd<25) {
                       repere.setPosition(new Vec(150, 0, repere.position().z()));
                       surLePiquet=2;
                  }
                  if (disg<25) { 
                       repere.setPosition(new Vec(-150, 0, repere.position().z()));
                       surLePiquet=0;
                  }
             }
        }
   }

    public class Systeme {

        int[][] etat;
        int situation;
        Disque[] disques;

        public Systeme() {
             situation=0;
             etat= new int[4][nbdisques];
             for (int i=0;i<4;i++) {
                  for (int j=0;j<nbdisques;j++) {
                       etat[i][j]=5;
                  }
             }
             for (int j=0;j<nbdisques;j++) {
                  etat[0][j]=nbdisques-1-j;
             }
             //construction des disques 
             disques = new Disque[nbdisques];
             for (int i=0;i<nbdisques;i++) {
                  disques[i]=new Disque(0, i, frames[i]);
             }
        }

        public void    draw() {
             pilote();
             for (int i=0;i<4;i++) {
                  for (int j=0;j<nbdisques;j++) {
                       if (getEtat(i, j)!=5)disques[getEtat(i, j)].draw();
                  }
             }
             // imprimeEtat();
        }

        public int getEtat(int i, int j) {
             return etat[i][j];
        }

        public void setEtat(int i, int j, int d) {
             etat[i][j]=d;
        }

        public int surLaPile( int numd) {
             int piquet=disques[numd].surLePiquet;
             int reponse=5;
             if ((getEtat(piquet, 0) == numd) &&(getEtat(piquet, 1)) == 5) reponse=0;
             if ((getEtat(piquet, 1) == numd) &&(getEtat(piquet, 2)) == 5) reponse=1;
             if ((getEtat(piquet, 2) == numd) &&(getEtat(piquet, 3)) == 5) reponse=2;
             if ((getEtat(piquet, 3) == numd) &&(getEtat(piquet, 4)) == 5) reponse=3;
             if (getEtat(piquet, 4) == numd) reponse=4;
             return reponse;
        }

        public int calculHauteur(int n) {
             int rep=0;
             for (int i=0;i<nbdisques;i++) {
                  if (getEtat(n, i)<5) rep+=1;
             }
             return rep;
        }

        public void decollage(int disqueEnPrise)
        {

             int  piquet=disques[disqueEnPrise].surLePiquet;
             int h=surLaPile(disqueEnPrise); 
             Vec zplus15= new Vec(0, 0, 15);
             zplus15.add(disques[disqueEnPrise].repere.position());
             disques[disqueEnPrise].repere.setPosition(zplus15);
             disques[disqueEnPrise].repere.setConstraint(contrainteGuide);
             setEtat(3, 0, disqueEnPrise);
             setEtat(piquet, h, 5);
             situation=1;
        }

        public void attenteDeClic() 
        {
             int i=0;
             /*while ( (i<nbdisques)&& (!disques[i].repere.grabsMouse())) 
             {
                  i++;
             }*/

             if (i<nbdisques)
             {//le disque i est cliqué  collerAuPiquet();
                  //vérifier que c'est soit un disque de tete soit disque en mouvement 

                  if ((getEtat(3, 0)==5 )&& (surLaPile(i)<5))
                  {
                       decollage(i);
                       situation =1;
                  }//si c'est un disque immobile vérifier qu'il est en tete
             }
        }

        public void atterrissage() {

             int  disc = getEtat(3, 0);
             int piquet=disques[disc].surLePiquet;
             int h=calculHauteur(piquet);

             setEtat(3, 0, 5);
             setEtat(piquet, h, disc);
             disques[disc].repere.setConstraint(immobile);
        }

        public void dragageGuide() {
             int  disc = getEtat(3, 0);
             if (disc<5) {
                  int piquet=disques[disc].surLePiquet;
                  int h=calculHauteur(piquet);
                  disques[disc].repere.setConstraint(contrainteGuide);
                  if (disques[disc].repere.position().z()>120) situation=2;
                  else if (disques[disc].repere.position().z()<12*h)
                  {    
                       if (!regle(disc, piquet, h))ejection(disc, piquet); 
                       else {
                            disques[disc].repere.setPosition(new Vec(piquet*150-150, 0, 12*h));
                            setEtat(3, 0, 5);
                            setEtat(piquet, h, disc);
                            disques[disc].repere.setConstraint(immobile);
                            situation=0;
                            //delay(1000);
                       }
                  }
             }
             else {
                  //delay(1000);
                  situation=0;
             }
        }

        public void ejection(int nodisc, int nopiquet) {  
             disques[nodisc].repere.setPosition(150*nopiquet-150, 0, 110);
        }

        public boolean regle(int nodisc, int nopiquet, int haut) {
             boolean rep;
             if (haut==0)
                  rep=true;
             else
                  rep= (getEtat(nopiquet, haut-1)>nodisc);
             return rep;
        }

        public void dragageLibre() {
             int  d = getEtat(3, 0);
             disques[d].repere.setConstraint(contraintePlan);
             float  dis=sqrt(sq(disques[d].repere.position().x())+sq(disques[d].repere.position().y()));
             float  disd=sqrt(sq(disques[d].repere.position().x()-150)+sq(disques[d].repere.position().y()));
             float  disg=sqrt(sq(disques[d].repere.position().x()+150)+sq(disques[d].repere.position().y())); 
             if (disques[d].repere.position().z()<120 )
             {
                  if (dis<25) {
                       disques[d].repere.setPosition(new Vec(0, 0, disques[d].repere.position().z()));
                       disques[d].surLePiquet=1;
                       situation=1;
                  } 
                  else if (disd<25) {
                       disques[d].repere.setPosition(new Vec(150, 0, disques[d].repere.position().z()));
                       disques[d].surLePiquet=2;
                       situation=1;
                  } 
                  else  if (disg<25) { 
                       disques[d].repere.setPosition(new Vec(-150, 0, disques[d].repere.position().z()));
                       disques[d].surLePiquet=0; 
                       situation=1;
                  }
                  else {
                       Vec v=disques[d].repere.position();
                       v.setZ(125);
                       disques[d].repere.setPosition(v);
                  }
             }
        }

        public void pilote() {
             switch(situation) {
             case 0: 
                  attenteDeClic();
                  break;
             case 1:
                  dragageGuide();
                  break;
             case 2:
                  dragageLibre();
                  break;
             case 3:
                  atterrissage();
             }
        }

        public void imprimeEtat() {
             String s="piquet 0 ---> "+getEtat(0, 0)+"   "+getEtat(0, 1)+"   "+getEtat(0, 2)+"   "+getEtat(0, 3)+"   "+getEtat(0, 4) ;
             println(s);
             s="piquet 1 ---> "+getEtat(1, 0)+"   "+getEtat(1, 1)+"   "+getEtat(1, 2)+"   "+getEtat(1, 3)+"   "+getEtat(1, 4) ;
             println(s);
             s="piquet 2 ---> "+getEtat(2, 0)+"   "+getEtat(2, 1)+"   "+getEtat(2, 2)+"   "+getEtat(2, 3)+"   "+getEtat(2, 4) ;

             println(s);
             s="piquet 3 ---> "+getEtat(3, 0)+"   "+getEtat(3, 1)+"   "+getEtat(3, 2)+"   "+getEtat(3, 3)+"   "+getEtat(3, 4) ;   
             println(s);
             println("");
        }
   }//fin de classe

}
