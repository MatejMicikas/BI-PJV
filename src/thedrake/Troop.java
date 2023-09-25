package thedrake;

import java.io.PrintWriter;
import java.util.List;

public class Troop implements JSONSerializable {
    private final String name;
    private final Offset2D aversPivot, reversPivot;
    private final List<TroopAction> aversActions, reversActions;

    // Hlavní konstruktor
    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    // Konstruktor, který nastavuje oba pivoty na stejnou hodnotu
    public Troop(String name, Offset2D pivot, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = pivot;
        this.reversPivot = pivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    // Konstruktor, který nastavuje oba pivoty na hodnotu [1, 1]
    public Troop(String name) {
        this.name = name;
        this.aversPivot = new Offset2D(1, 1);
        this.reversPivot = new Offset2D(1, 1);
        this.aversActions = null;
        this.reversActions = null;
    }

    public Troop(String name, List<TroopAction> aversActions, List<TroopAction> reversActions) {
        this.name = name;
        this.aversPivot = new Offset2D(1, 1);
        this.reversPivot = new Offset2D(1, 1);
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public List<TroopAction> actions(TroopFace face) {
        return face.equals(TroopFace.AVERS) ? aversActions : reversActions;
    }

    // Vrací jméno jednotky
    public String name() {
        return this.name;
    }

    // Vrací pivot na zadané straně jednotky
    public Offset2D pivot(TroopFace face) {
        if (face.equals(TroopFace.AVERS)) {
            return this.aversPivot;
        } else return this.reversPivot;
    }

    @Override
    public void toJSON(PrintWriter writer) {
        writer.printf("\"%s\"", this.name);
    }
}
