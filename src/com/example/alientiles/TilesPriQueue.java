package com.example.alientiles;

public class TilesPriQueue extends TilesList {

    public TilesPriQueue() {
        super();
    }

    public TilesPriQueue(TileNode node) {
        super(node);
    }

    public void add(TileNode node) {
        double newScore = node.getScore();
        TileNode entry;
        for (int i = 0; i < nodes.size(); i++) {
            entry = (TileNode) nodes.get(i);
            if (newScore <= entry.getScore()) {
                nodes.add(i, node);
                return;
            }
        }
        nodes.add(node);
    }

    public TileNode removeFirst() {
        return (TileNode) nodes.remove(0);
    }
}
