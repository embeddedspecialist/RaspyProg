/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes.gui;

/**
 *
 * @author newtohm
 */

public class CoppiaComp <E extends Comparable<E>,F extends Comparable<F>> extends Coppia<E,F> implements Comparable<CoppiaComp<E,F>> {

    public CoppiaComp(E fst, F snd){
        super(fst, snd);
    }

    @Override
    public int compareTo(CoppiaComp<E,F> obj){
        int comp = getFst().compareTo(obj.getFst());
        if (comp != 0) return comp;
        return getSnd().compareTo(obj.getSnd());
    }
}
