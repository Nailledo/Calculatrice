package com.calculatrice;

import java.math.BigDecimal;

public class Outils
{
    public static String enleverZero(String calculEnCours)
    {
        String calculSansZero = "";
        int    deb            = 0;

        if ( calculEnCours.charAt( 0 ) == '-' ) deb = 1;

        for (int cpt = deb; cpt < calculEnCours.length(); cpt++)
        {
            if ( ! Outils.verificationOperation( cpt, calculEnCours ) )
            {
                BigDecimal valGauche  = Outils.calculerOperationGauche( cpt, calculEnCours );
                String sValGauche = "" + valGauche;
                String operation  = "" + calculEnCours.charAt( cpt++ );

                calculSansZero += sValGauche + operation;
            }
        }
        BigDecimal valGauche  = Outils.calculerOperationGauche( calculEnCours.length(), calculEnCours );
        if (valGauche != null)
            calculSansZero += valGauche;

        return calculSansZero;
    }

    public static boolean calculEstValide(String calculEnCours)
    {
        if (calculEnCours == null || calculEnCours.isEmpty())
            return false;

        // Ne peut pas commencer par un opérateur (sauf '-' pour les négatifs)
        if (!verificationOperation(0, calculEnCours) && calculEnCours.charAt(0) != '-')
            return false;

        for (int cpt = 1; cpt < calculEnCours.length(); cpt++)
        {
            char courant   = calculEnCours.charAt(cpt);

            boolean courantEstOp   = !verificationOperation(cpt,     calculEnCours);
            boolean precedentEstOp = !verificationOperation(cpt - 1, calculEnCours);

            // Deux opérateurs consécutifs : autorisé seulement si le 2e est '-'
            if (courantEstOp && precedentEstOp && courant != '-')
                return false;

            // Un opérateur sans opérande gauche ou droite (sauf '²' et '%' et '!' qui sont unaires)
            if (courantEstOp && courant != '²' && courant != '%' && courant != '!' )
            {
                if (courant == '-' && precedentEstOp)
                {
                    boolean pasDeDroite = calculerOperationDroite(cpt + 1, calculEnCours) == null;
                    if (pasDeDroite)
                        return false;

                }
                else
                {
                    boolean pasDeGauche = calculerOperationGauche(cpt    , calculEnCours) == null;
                    boolean pasDeDroite = calculerOperationDroite(cpt + 1, calculEnCours) == null;

                    if (pasDeGauche || pasDeDroite)
                        return false;
                }
            }
        }

        return true;
    }
    public static BigDecimal calculerOperationGauche( int cpt, String calculEnCours )
    {
        if ( Outils.estPI( cpt - 1, calculEnCours) ) return BigDecimal.valueOf(Math.PI);

        String valeurGauche = "";
        int cptGauche = cpt - 1;
        while ( cptGauche >= 0 &&
                Outils.verificationOperation( cptGauche, calculEnCours ) )
        {
            valeurGauche += calculEnCours.charAt( cptGauche-- );
        }
        valeurGauche = new StringBuilder(valeurGauche).reverse().toString();

        if (cptGauche >= 0 && calculEnCours.charAt(cptGauche) == '-')
        {
            if (cptGauche == 0 || !Outils.verificationOperation(cptGauche - 1, calculEnCours))
            {
                valeurGauche = "-" + valeurGauche;
            }
        }

        if (valeurGauche.isEmpty() || valeurGauche.equals("-"))
            return null;

        return new BigDecimal(valeurGauche);
    }

    public static BigDecimal calculerOperationDroite(int cpt, String calculEnCours )
    {
        if ( Outils.estPI( cpt + 1, calculEnCours) ) return BigDecimal.valueOf(Math.PI);

        String valeurDroite = "";
        int cptDroite = cpt;

        if (cptDroite < calculEnCours.length() && calculEnCours.charAt(cptDroite) == '-')
        {
            valeurDroite += "-";
            cptDroite++;
        }

        while ( cptDroite < calculEnCours.length() &&
                Outils.verificationOperation( cptDroite, calculEnCours ) )
        {
            valeurDroite += calculEnCours.charAt(cptDroite++);
        }

        if (valeurDroite.isEmpty() || valeurDroite.equals("-"))
            return null;

        return new BigDecimal(valeurDroite);
    }

    private static boolean verificationOperation( int cpt, String calculEnCours )
    {
        if (cpt < 0 || cpt >= calculEnCours.length())
            return true;

        return  calculEnCours.charAt( cpt ) != '+' &&
                calculEnCours.charAt( cpt ) != '-' &&
                calculEnCours.charAt( cpt ) != '*' &&
                calculEnCours.charAt( cpt ) != '/' &&
                calculEnCours.charAt( cpt ) != '^' &&
                calculEnCours.charAt( cpt ) != '²' &&
                calculEnCours.charAt( cpt ) != '%' &&
                calculEnCours.charAt( cpt ) != '!' &&
                calculEnCours.charAt( cpt ) != 'M' ;
    }

    private static boolean estPI( int cpt, String calculEnCours )
    {
        if (cpt < 0 || cpt >= calculEnCours.length() )
            return false;
        return calculEnCours.charAt( cpt ) == 'π';
    }

    public static BigDecimal factorielle(BigDecimal n)
    {
        if (n.compareTo(BigDecimal.ZERO) < 0) {
            return new BigDecimal("-1"); // Factorial of a negative number is undefined
        }

        if (n.compareTo(BigDecimal.ZERO) == 0)
        {
            return BigDecimal.ONE;
        }
        else
        {
            return n.multiply ( Outils.factorielle ( n.subtract ( BigDecimal.ONE ) ) );
        }
    }



}
