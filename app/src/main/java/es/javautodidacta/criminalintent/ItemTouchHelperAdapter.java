package es.javautodidacta.criminalintent;

/**
 * This class interface works with the {@link SwipeController}.
 *
 * @author Miguel Callejón Berenguer
 * @version 2018.01
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
