package com.calculatrice;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculatrice extends AppCompatActivity
{
    private String sCalculEnCours;
    private int curseur;
    private ValueAnimator curseurAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        this.sCalculEnCours = "";
        this.curseur = 0;
    }

    public void boutonDeplacement(View view)
    {
        BoutonVibrant btn  = (BoutonVibrant) view;
        String        sBtn = btn.getText().toString();

        if (this.sCalculEnCours.contains("|"))
            this.sCalculEnCours = this.sCalculEnCours.replace("|", "");

        switch (sBtn)
        {
            case "▲":
                break;
            case "◄":
                if (this.sCalculEnCours.isEmpty() || this.curseur <= 0)
                    return;
                this.curseur--;
                if (this.sCalculEnCours.charAt( this.curseur) == 'D' || this.sCalculEnCours.charAt( this.curseur) == 's'  )
                    this.curseur -= 2;
                break;
            case "►":
                if (this.curseur >= this.sCalculEnCours.length())
                    return;
                if (this.sCalculEnCours.charAt( this.curseur) == 'M' || this.sCalculEnCours.charAt( this.curseur) == 'A' )
                    this.curseur += 3;
                else
                    this.curseur++;
                break;
            case "▼":
                break;
        }
        this.replacerCurseur();
    }

    private void enleverCurseur()
    {
        if (this.sCalculEnCours.contains("|"))
            this.sCalculEnCours = this.sCalculEnCours.replace("|", "");
    }

    private void replacerCurseur()
    {
        this.sCalculEnCours = this.sCalculEnCours.substring(0, this.curseur) +
                "|" +
                this.sCalculEnCours.substring(this.curseur);

        TextView ecran = (TextView) findViewById(R.id.ecran);
        this.animationCurseur(ecran, this.curseur);
    }

    public void boutonDelete(View view)
    {
        boolean bMot = false;
        if (this.sCalculEnCours.isEmpty() || this.curseur <= 0)
            return;
        this.curseur--;

        // Si c'est un D pour MOD ou S pour ANS
        if ( this.sCalculEnCours.charAt( this.curseur ) == 'D' || this.sCalculEnCours.charAt( this.curseur ) == 's'  )
        {
            bMot = true;
            this.curseur -= 2;
        }


        this.enleverCurseur();
        this.sCalculEnCours = this.sCalculEnCours.substring(0, this.curseur) +
                              this.sCalculEnCours.substring(this.curseur + ( bMot ? 3 : 1 ) );
        this.replacerCurseur();
    }

    public void boutonDeleteAll(View view)
    {
        if (this.sCalculEnCours.isEmpty())
            return;

        // Arrêter l'animation
        if (curseurAnimator != null && curseurAnimator.isRunning())
            curseurAnimator.cancel();

        TextView ecran = (TextView) findViewById(R.id.ecran);
        ecran.setText("");
        this.curseur = 0;
        this.sCalculEnCours = "";
    }

    public void boutonNumero(View view)
    {
        BoutonVibrant btn  = (BoutonVibrant) view;
        String        sBtn = btn.getText().toString();

        this.enleverCurseur();
        this.sCalculEnCours = this.sCalculEnCours.substring(0, this.curseur) +
                sBtn +
                this.sCalculEnCours.substring(this.curseur);

        this.curseur++;
        this.replacerCurseur();
    }

    public void boutonOperation(View view)
    {
        BoutonVibrant btn = (BoutonVibrant) view;
        String sBtn = btn.getText().toString();

        this.enleverCurseur();

        if ( this.sCalculEnCours.isEmpty() && !sBtn.equals("Ans") )
        {
            this.sCalculEnCours = "Ans" + sBtn ;
            this.curseur        = 3 + sBtn.length();
            this.replacerCurseur();
            return;
        }
        this.sCalculEnCours = this.sCalculEnCours.substring(0, this.curseur) +
                              sBtn +
                              this.sCalculEnCours.substring(this.curseur);

        if ( sBtn.equals("MOD") || sBtn.equals("Ans") )
            this.curseur += 3;
        else
            this.curseur++;

        this.replacerCurseur();
    }

    public void boutonEgal(View view)
    {
        // Arrêter l'animation
        if (curseurAnimator != null && curseurAnimator.isRunning())
            curseurAnimator.cancel();

        TextView ecran21 = (TextView) findViewById(R.id.ecran21);
        TextView ecran22 = (TextView) findViewById(R.id.ecran22);
        TextView ecran12 = (TextView) findViewById(R.id.ecran12);
        TextView ecran11 = (TextView) findViewById(R.id.ecran11);
        TextView ecran   = (TextView) findViewById(R.id.ecran);

        ecran22.setText(ecran12.getText());
        ecran21.setText(ecran11.getText());

        this.enleverCurseur();
        String resultat = this.calculerOperation();

        ecran11.setText(this.sCalculEnCours);
        ecran12.setText(resultat);
        ecran.setText("");

        this.sCalculEnCours = "";
        this.curseur        = 0;
    }

    private String calculerOperation()
    {
        // récupérer ecran12
        TextView ecran12       = (TextView) findViewById(R.id.ecran12);
        String   sResPrecedent = ( ecran12.getText().toString().isEmpty() ? "0" : ecran12.getText().toString() );

        try
        {
            if (this.sCalculEnCours == null || this.sCalculEnCours.isEmpty())
                return "";

            this.sCalculEnCours = this.sCalculEnCours.replace("MOD", "M")
                                                     .replace("Ans", sResPrecedent );

            if (!Outils.calculEstValide(this.sCalculEnCours))
            {
                this.sCalculEnCours = this.sCalculEnCours.replace("M", "MOD");
                return "[Erreur]";
            }

            String sCalculEnCoursTemp = this.sCalculEnCours;

            // Remettre M en MOD pour l'affichage
            this.sCalculEnCours = sCalculEnCoursTemp.replace("M", "MOD");

            if (sCalculEnCoursTemp.contains("!") || sCalculEnCoursTemp.contains("%") || sCalculEnCoursTemp.contains("²"))
                sCalculEnCoursTemp = this.calculer(sCalculEnCoursTemp, '!', '²', '%');

            if (sCalculEnCoursTemp.contains("^"))
                sCalculEnCoursTemp = this.calculer(sCalculEnCoursTemp, '^');

            if (sCalculEnCoursTemp.contains("*") || sCalculEnCoursTemp.contains("/") || sCalculEnCoursTemp.contains("M"))
                sCalculEnCoursTemp = this.calculer(sCalculEnCoursTemp, '*', '/', 'M');

            if (sCalculEnCoursTemp.contains("+") || sCalculEnCoursTemp.contains("-"))
                sCalculEnCoursTemp = this.calculer(sCalculEnCoursTemp, '+', '-');




            return sCalculEnCoursTemp;
        } catch (Exception e) { return "[Erreur]"; }
    }

    private String calculer(String expression, char... operateurs)
    {
        int position = 0;
        while (position < expression.length())
        {
            char caractere = expression.charAt(position);

            if (!estUnOperateur(caractere, operateurs))
            {
                position++;
                continue;
            }

            if (estSigneEnDebut(caractere, position, expression))
            {
                position++;
                continue;
            }

            BigDecimal gauche = Outils.calculerOperationGauche(position, expression);
            BigDecimal droite = this.extraireDroite(caractere, position, expression);

            if (gauche == null)
                return "[Erreur]";

            if (estDivisionParZero(caractere, droite))
                return "[Erreur]";

            BigDecimal resultat = this.appliquerOperation(caractere, gauche, droite);

            if (resultat == null)
                return "8.2Ε18";

            expression = this.remplacerDansExpression(expression, position, gauche, droite, caractere, resultat);

            if (expression.equals("[Erreur]"))
                return "[Erreur]";

            position = 0;
        }
        return expression;
    }

    private boolean estUnOperateur(char c, char[] operateurs)
    {
        for (char op : operateurs)
            if (c == op)
                return true;
        return false;
    }

    private boolean estSigneEnDebut(char c, int position, String expression)
    {
        return (c == '-' || c == '+') && position == 0 && expression.length() > 1 &&
                Character.isDigit(expression.charAt(1));
    }

    private BigDecimal extraireDroite(char operateur, int position, String expression)
    {
        if (operateur == '²' || operateur == '%' || operateur == '!')
            return null;
        return Outils.calculerOperationDroite(position + 1, expression);
    }

    private boolean estDivisionParZero(char operateur, BigDecimal droite)
    {
        return (operateur == '/' || operateur == 'M') && droite != null &&
                droite.equals(BigDecimal.ZERO);
    }

    private BigDecimal appliquerOperation(char operateur, BigDecimal gauche, BigDecimal droite)
    {
        try
        {
            switch (operateur)
            {
                case '+': return gauche.add(droite);
                case '-': return gauche.subtract(droite);
                case '*': return gauche.multiply(droite);
                case '/': return gauche.divide(droite, 10, RoundingMode.HALF_UP);
                case '²': return gauche.pow(2);
                case '^': return droite != null ? gauche.pow(droite.intValue()) : BigDecimal.ZERO;
                case '%': return gauche.multiply(BigDecimal.valueOf(0.01));
                case 'M': return gauche.remainder(droite);
                case '!': return Outils.factorielle(gauche);
                default: return BigDecimal.ZERO;
            }
        }
        catch (ArithmeticException e)
        {
            return null;
        }
    }

    private String remplacerDansExpression(String expression, int posOperateur,
                                           BigDecimal gauche, BigDecimal droite,
                                           char operateur, BigDecimal resultat)
    {
        String sGauche = gauche.toString();
        String sDroite = "";

        if (operateur != '²' && operateur != '%' && operateur != '!')
            sDroite = (droite != null) ? droite.toString() : "";

        int debutGauche = posOperateur - sGauche.length();
        if (debutGauche < 0)
            return "[Erreur]";

        int finDroite = posOperateur + sDroite.length() + (sDroite.isEmpty() ? 0 : 1);
        if (sDroite.isEmpty())
            finDroite = posOperateur + 1;

        return expression.substring(0, debutGauche) + resultat + expression.substring(finDroite);
    }

    /***********************/
    /* ANIMATION CURSEUR */
    /***********************/

    public void animationCurseur(TextView ecran, int positionCurseur)
    {
        // Arrêter l'animation précédente si elle existe
        if (curseurAnimator != null && curseurAnimator.isRunning())
            curseurAnimator.cancel();

        curseurAnimator = ValueAnimator.ofFloat(0f, 1f);
        curseurAnimator.setDuration(500);
        curseurAnimator.setRepeatCount(ValueAnimator.INFINITE);
        curseurAnimator.setRepeatMode(ValueAnimator.REVERSE);

        curseurAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();

                SpannableString spannable = new SpannableString(sCalculEnCours);

                // Calculer la couleur avec transparence pour le curseur uniquement
                int alphaValue = (int) (alpha * 255);
                int color = (alphaValue << 24) | 0x00000000; // Blanc avec alpha variable

                // Appliquer la couleur uniquement sur le caractère '|'
                if (spannable.length() > positionCurseur &&
                        spannable.charAt(positionCurseur) == '|')
                {
                    spannable.setSpan(
                            new ForegroundColorSpan(color),
                            positionCurseur,
                            positionCurseur + 1,
                            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }

                ecran.setText(spannable);
            }
        });

        curseurAnimator.start();
    }

    /***********************/
    /*      SAUVEGARDE     */
    /***********************/

    @Override
    public void onSaveInstanceState(Bundle bagOfData)
    {
        super.onSaveInstanceState(bagOfData);

        TextView ecran11 = (TextView) findViewById(R.id.ecran11);
        TextView ecran12 = (TextView) findViewById(R.id.ecran12);
        TextView ecran21 = (TextView) findViewById(R.id.ecran21);
        TextView ecran22 = (TextView) findViewById(R.id.ecran22);
        TextView ecran   = (TextView) findViewById(R.id.ecran);

        bagOfData.putString("ecran11", ecran11.getText().toString());
        bagOfData.putString("ecran12", ecran12.getText().toString());
        bagOfData.putString("ecran21", ecran21.getText().toString());
        bagOfData.putString("ecran22", ecran22.getText().toString());
        bagOfData.putString("ecran", ecran.getText().toString());
        bagOfData.putInt   ("curseur", this.curseur);
        bagOfData.putString("sCalculEnCours", this.sCalculEnCours);
    }

    @Override
    public void onRestoreInstanceState(Bundle bagOfData)
    {
        super.onRestoreInstanceState(bagOfData);

        TextView ecran11 = (TextView) findViewById(R.id.ecran11);
        TextView ecran12 = (TextView) findViewById(R.id.ecran12);
        TextView ecran21 = (TextView) findViewById(R.id.ecran21);
        TextView ecran22 = (TextView) findViewById(R.id.ecran22);
        TextView ecran = (TextView) findViewById(R.id.ecran);

        ecran.setText(bagOfData.getString("ecran"));
        ecran11.setText(bagOfData.getString("ecran11"));
        ecran12.setText(bagOfData.getString("ecran12"));
        ecran21.setText(bagOfData.getString("ecran21"));
        ecran22.setText(bagOfData.getString("ecran22"));

        this.curseur = bagOfData.getInt("curseur");
        this.sCalculEnCours = bagOfData.getString("sCalculEnCours");

        // Relancer l'animation si nécessaire
        if (this.sCalculEnCours.contains("|"))
        {
            this.animationCurseur(ecran, this.curseur);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Arrêter l'animation quand l'activité est en pause
        if (curseurAnimator != null && curseurAnimator.isRunning())
            curseurAnimator.cancel();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Relancer l'animation si le curseur est présent
        if (this.sCalculEnCours.contains("|"))
        {
            TextView ecran = (TextView) findViewById(R.id.ecran);
            this.animationCurseur(ecran, this.curseur);
        }
    }
}